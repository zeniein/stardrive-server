package cn.zeniein.stardrive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UploadConfig {

    public static String basePath;

    public static String apiHost;

    @Value("${upload.path.base}")
    public void setBasePath(String basePath) {
        UploadConfig.basePath = basePath;
    }
    @Value("${host.api:}")
    public void setApiHost(String apiHost) {
        UploadConfig.apiHost = apiHost;
    }
}
