package cn.zeniein.stardrive.utils;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.zeniein.stardrive.config.SecretConfig;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class EncryptUtils {

    private static final String ALGORITHM_AES = "AES";
    private static final String ALGORITHM_RSA = "RSA";

    public static String aesEncrypt(String content) {

        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, new SecretKeySpec(SecretConfig.aesKey.getBytes(), ALGORITHM_AES), new IvParameterSpec(SecretConfig.aesIv.getBytes()));

        return aes.encryptHex(content);
    }

    public static String aesDecrypt(String content) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, new SecretKeySpec(SecretConfig.aesKey.getBytes(), ALGORITHM_AES), new IvParameterSpec(SecretConfig.aesIv.getBytes()));

        byte[] decrypt = aes.decrypt(content);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    public static String rsaEncrypt(String content) {
        RSA rsa = new RSA(null, SecretConfig.rsaPublicKey);
        return rsa.encryptHex(content, KeyType.PublicKey);
    }

    public static String rsaDecrypt(String content) {
        RSA rsa = new RSA(SecretConfig.rsaPrivateKey, null);
        return rsa.decryptStr(content, KeyType.PrivateKey);
    }

}
