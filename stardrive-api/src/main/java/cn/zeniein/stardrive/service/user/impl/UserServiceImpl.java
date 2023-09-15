package cn.zeniein.stardrive.service.user.impl;

import cn.zeniein.stardrive.cache.VerifiableCodeCache;
import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.common.constant.FileConstant;
import cn.zeniein.stardrive.config.UploadConfig;
import cn.zeniein.stardrive.model.dto.LoginDTO;
import cn.zeniein.stardrive.model.dto.RegisterDTO;
import cn.zeniein.stardrive.model.po.UserPO;
import cn.zeniein.stardrive.model.vo.UserInfoVO;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.mapper.UserMapper;
import cn.zeniein.stardrive.service.capacity.CapacityService;
import cn.zeniein.stardrive.service.file.helper.FilePathHelper;
import cn.zeniein.stardrive.service.user.UserService;
import cn.zeniein.stardrive.service.user.helper.UserHelper;
import cn.zeniein.stardrive.support.jwt.JwtUtils;
import cn.zeniein.stardrive.utils.ImageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private static final String FOLDER_BASE_AVATAR = UploadConfig.basePath + "/avatar";

    private final UserMapper userMapper;

    private final CapacityService capacityService;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, CapacityService capacityService, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.capacityService = capacityService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public String login(LoginDTO login) throws JOSEException {

        String username = login.getUsername();
        String password = login.getPassword();

        LambdaQueryWrapper<UserPO> queryWrapper = new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getPhone, username);
        UserPO user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BizException(ResponseEnum.LOGIN_FAILURE);
        }

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            throw new BizException(ResponseEnum.LOGIN_FAILURE);
        }

        return JwtUtils.generate(user.getId(), user.getRole());
    }

    /**
     * 用户注册
     *
     * 从缓存中获取验证码进行判断
     *
     * @param register 注册信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO register) {
        String code = register.getCode();
        String phone = register.getPhone();

        String inCacheCode = VerifiableCodeCache.get(phone);
        if(inCacheCode == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "验证码错误");
        }
        if(!inCacheCode.equals(code)) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "验证码错误");
        }
        LambdaQueryWrapper<UserPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPO::getPhone, phone);
        boolean phoneExists = userMapper.exists(queryWrapper);
        if (phoneExists) {
            throw new BizException(ResponseEnum.PHONE_NUMBER_ALREADY_EXISTS);
        }
        String nickname = "用户" + phone.substring(0, 3) + "***" + phone.substring(8);
        String encodePassword = passwordEncoder.encode(register.getPassword());
        String userId = IdWorker.getIdStr();
        UserPO user = UserPO.builder()
                .id(userId)
                .createTime(LocalDateTime.now())
                .nickname(nickname)
                .password(encodePassword)
                .phone(register.getPhone())
                .role("user")
                .build();

        capacityService.userInit(userId);
        userMapper.insert(user);
        UserHelper.initUserDir(userId);
        VerifiableCodeCache.remove(phone);
    }

    @Override
    public void uploadAvatar(String userId, MultipartFile avatar) {
        String filename = avatar.getOriginalFilename();
        if (filename == null) {
            throw new BizException("");
        }
        int dotIndex = filename.lastIndexOf(".");

        String fileSuffix = dotIndex == -1 ? "" : filename.substring(dotIndex);

        String path = "/" + IdWorker.getMillisecond() + fileSuffix;


        try {
            String userAvatarPath = FilePathHelper.getAvatarPath(userId) + path;
            if(Files.notExists(Paths.get(FOLDER_BASE_AVATAR))) {
                Path avatarBasePath = Paths.get(FOLDER_BASE_AVATAR);
                Files.createDirectories(avatarBasePath);
            }
            String avatarPath = FOLDER_BASE_AVATAR + path;
            ImageUtils.scaleAndSaveImage(avatar, FileConstant.AVATAR_STANDARD_WIDTH, userAvatarPath);
            ImageUtils.scaleAndSaveImage(avatar, FileConstant.AVATAR_STANDARD_WIDTH, avatarPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException(ResponseEnum.AVATAR_UPLOAD_FAILURE);
        }
        LambdaUpdateWrapper<UserPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserPO::getAvatar, path);
        updateWrapper.eq(UserPO::getId, userId);
        userMapper.update(null, updateWrapper);
    }

    @Override
    public UserInfoVO getUserInfo(String userId) {
        UserPO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "获取用户信息失败");
        }
        String avatar = user.getAvatar();
        String avatarUrl = avatar == null ? "" : UploadConfig.apiHost + "/image/avatar" + avatar;
        return UserInfoVO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatar(avatarUrl)
                .role(user.getRole())
                .build();
    }

    @Override
    public void updatePassword(String userId, String oldPassword, String newPassword) {
        UserPO user = userMapper.selectById(userId);
        if(user == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "用户不存在");
        }
        String password = user.getPassword();
        boolean matches = passwordEncoder.matches(oldPassword, password);
        if(!matches) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "原密码不一致");
        }
        LambdaUpdateWrapper<UserPO> updateWrapper = new LambdaUpdateWrapper<>();
        newPassword = passwordEncoder.encode(newPassword);
        updateWrapper.set(UserPO::getPassword, newPassword);
        updateWrapper.eq(UserPO::getId, userId);
        userMapper.update(null, updateWrapper);
    }
}
