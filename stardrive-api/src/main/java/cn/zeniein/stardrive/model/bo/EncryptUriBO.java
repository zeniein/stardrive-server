package cn.zeniein.stardrive.model.bo;

import cn.zeniein.stardrive.common.constant.FileConstant;
import cn.zeniein.stardrive.utils.EncryptUtils;
import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptUriBO {
    String userId;
    String fileId;
    Long timestamp;
    Long expires;

    public static String encrypt(String userId, String fileId) {
        long timestamp = System.currentTimeMillis();
        EncryptUriBO encryptUriBO = new EncryptUriBO(userId, fileId, timestamp, FileConstant.FILE_ADDRESS_DEFAULT_EXPIRES);
        String jsonStr = JSON.toJSONString(encryptUriBO);
        return EncryptUtils.aesEncrypt(jsonStr);
    }


    public static String encrypt(String userId, String fileId, long timestamp, long expires) {
        EncryptUriBO encryptUriBO = new EncryptUriBO(userId, fileId, timestamp, expires);
        String jsonStr = JSON.toJSONString(encryptUriBO);
        return EncryptUtils.aesEncrypt(jsonStr);
    }

    public static EncryptUriBO decrypt(String content) {
        String decryptStr = EncryptUtils.aesDecrypt(content);
        return JSON.parseObject(decryptStr, EncryptUriBO.class);
    }

    public boolean isExpired() {
        long now = System.currentTimeMillis();
        return now > timestamp + expires;
    }

}
