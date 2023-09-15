package cn.zeniein.stardrive.controller;

import cn.zeniein.stardrive.cache.VerifiableCodeCache;
import cn.zeniein.stardrive.common.ResponseData;
import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.common.constant.RegexConstant;
import cn.zeniein.stardrive.model.dto.LoginDTO;
import cn.zeniein.stardrive.model.dto.RegisterDTO;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.service.user.UserService;
import cn.zeniein.stardrive.utils.EncryptUtils;
import com.nimbusds.jose.JOSEException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseData<?> login(@RequestBody LoginDTO login) throws JOSEException {
        String username = login.getUsername();
        String password = login.getPassword();
        if(username == null || password == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "参数错误");
        }
        password = EncryptUtils.rsaDecrypt(password);
        login.setPassword(password);
        String jwt = userService.login(login);
        Map<String, Object> data  = new HashMap<>(2);
        data.put("result", jwt);
        return ResponseData.success(data);
    }


    @PostMapping("/register")
    public ResponseData<?> register(@RequestBody RegisterDTO register) {
        String password = register.getPassword();
        password = EncryptUtils.rsaDecrypt(password);
        register.setPassword(password);
        String phone = register.getPhone();
        String code = register.getCode();
        if(phone == null || password == null || code == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "参数错误");
        }
        if(!Pattern.matches(RegexConstant.PHONE, phone)){
            throw new BizException(ResponseEnum.PHONE_NUMBER_NOT_VALID.getStatus(), String.format("The phone number is not valid. phone number is: %s", phone));
        }

        if(!Pattern.matches(RegexConstant.PASSWORD, password)) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "密码长度6-18");
        }

        userService.register(register);
        return ResponseData.success();
    }

    @PostMapping("/send/code")
    public ResponseData<?> send(@RequestBody RegisterDTO register) {
        String phone = register.getPhone();
        if(phone == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "参数错误");
        }

        if(!Pattern.matches(RegexConstant.PHONE, phone)){
            throw new BizException(ResponseEnum.PHONE_NUMBER_NOT_VALID.getStatus(), String.format("The phone number is not valid. phone number is: %s", phone));
        }
        // 临时使用默认值
        VerifiableCodeCache.put(phone, VerifiableCodeCache.DEFAULT_CODE);
        Map<String, Object> data  = new HashMap<>(2);
        data.put("result", phone);
        return ResponseData.success(data);
    }


}
