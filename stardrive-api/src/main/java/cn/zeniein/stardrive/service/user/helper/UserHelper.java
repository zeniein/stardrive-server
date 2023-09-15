package cn.zeniein.stardrive.service.user.helper;

import cn.zeniein.stardrive.service.file.helper.FilePathHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class UserHelper {
    /**
     * 初始化用户目录
     */
    public static void initUserDir(String userId) {
        String userDir = FilePathHelper.getUserBasePath(userId);
        String fileDir = FilePathHelper.getFilePath(userId);
        String avatarDir = FilePathHelper.getAvatarPath(userId);
        String imageDir = FilePathHelper.getImagePath(userId);
        Path userDirPath = Paths.get(userDir);
        Path fileDirPath = Paths.get(fileDir);
        Path avatarDirPath = Paths.get(avatarDir);
        Path imageDirPath = Paths.get(imageDir);
        try {
            if (!Files.exists(userDirPath)) {
                Path directories = Files.createDirectories(userDirPath);
                log.info("用户目录初始化: {}", directories);
            }
            if (!Files.exists(fileDirPath)) {
                Path directories = Files.createDirectories(fileDirPath);
                log.info("用户文件目录初始化: {}", directories);
            }
            if (!Files.exists(avatarDirPath)) {
                Path directories = Files.createDirectories(avatarDirPath);
                log.info("用户头像目录初始化: {}", directories);
            }
            if(!Files.exists(imageDirPath)) {
                Path directories = Files.createDirectories(imageDirPath);
                log.info("用户图片目录初始化: {}", directories);
            }
        }catch (IOException e) {
            log.error("初始化用户目录失败, error=" + e);
        }
    }
}
