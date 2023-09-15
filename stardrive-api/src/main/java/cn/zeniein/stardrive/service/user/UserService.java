package cn.zeniein.stardrive.service.user;

import cn.zeniein.stardrive.model.dto.LoginDTO;
import cn.zeniein.stardrive.model.dto.RegisterDTO;
import cn.zeniein.stardrive.model.vo.UserInfoVO;
import com.nimbusds.jose.JOSEException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    /**
     * 用户登录
     *
     * @param login 登录信息
     * @return jwt
     * @throws JOSEException the JOSEException
     */
    String login(LoginDTO login) throws JOSEException;

    /**
     * 用户注册
     *
     * @param register 注册信息
     */
    void register(RegisterDTO register);

    /**
     * 上传头像
     *
     * @param userId 用户Id
     * @param avatar 头像文件
     */
    void uploadAvatar(String userId, MultipartFile avatar);

    /**
     * 获取用户信息
     *
     * @param userId 用户Id
     * @return the userInfoVO
     */
    UserInfoVO getUserInfo(String userId);

    /**
     * 更新密码
     *
     * @param userId      用户Id
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void updatePassword(String userId, String oldPassword, String newPassword);

}
