package cn.zeniein.stardrive.controller;

import cn.zeniein.stardrive.common.ResponseData;
import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.model.vo.UserInfoVO;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.service.user.UserService;
import cn.zeniein.stardrive.support.jwt.SecurityContextHolder;
import cn.zeniein.stardrive.utils.EncryptUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/avatar/upload")
    public ResponseData<?> uploadUserAvatar(MultipartFile file) {
        String userId = SecurityContextHolder.getContext().getUserId();
        userService.uploadAvatar(userId, file);
        return ResponseData.success();
    }
    @GetMapping("/info")
    public ResponseData<?> getUserInfo() {
        String userId = SecurityContextHolder.getContext().getUserId();
        UserInfoVO userInfo = userService.getUserInfo(userId);
        return ResponseData.success(userInfo);
    }

    @PostMapping("/update/password")
    public ResponseData<?> updatePassword(@RequestBody Map<String, String> params) {
        String userId = SecurityContextHolder.getContext().getUserId();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if(oldPassword == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "原密码不能为空");
        }
        if(newPassword == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "新密码不能为空");
        }
        oldPassword = EncryptUtils.rsaDecrypt(oldPassword);
        newPassword = EncryptUtils.rsaDecrypt(newPassword);

        userService.updatePassword(userId, oldPassword, newPassword);
        return ResponseData.success();
    }

}
