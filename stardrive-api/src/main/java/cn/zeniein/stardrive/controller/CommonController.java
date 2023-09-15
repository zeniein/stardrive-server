package cn.zeniein.stardrive.controller;

import cn.zeniein.stardrive.common.ResponseData;
import cn.zeniein.stardrive.config.SecretConfig;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/common")
public class CommonController {

    @GetMapping("/key")
    public ResponseData<?> getPublicKey() {
        Map<String, Object> res = new HashMap<>(2);
        res.put("result", SecretConfig.rsaPublicKey);
        return ResponseData.success(res);
    }

}
