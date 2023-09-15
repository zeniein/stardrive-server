package cn.zeniein.stardrive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretConfig {

    public static String aesKey;

    public static String aesIv;

    public static String rsaPublicKey;

    public static String rsaPrivateKey;

    @Value("${secret.aes.key}")
    public void setAesKey(String aesKey) {
        SecretConfig.aesKey = aesKey;
    }

    @Value("${secret.aes.iv}")
    public void setAesIv(String aesIv) {
        SecretConfig.aesIv = aesIv;
    }
    @Value("${secret.rsa.public-key}")
    public void setRsaPublicKey(String rsaPublicKey) {
        SecretConfig.rsaPublicKey = rsaPublicKey;
    }
    @Value("${secret.rsa.private-key}")
    public void setRsaPrivateKey(String rsaPrivateKey) {
        SecretConfig.rsaPrivateKey = rsaPrivateKey;
    }


}
