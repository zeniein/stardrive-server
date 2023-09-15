package cn.zeniein.stardrive.service.file.helper;

import cn.zeniein.stardrive.config.UploadConfig;

public class FilePathHelper {

    private static final String FOLDER_DATA = UploadConfig.basePath + "/data";

    private static final String FOLDER_BASE_AVATAR = UploadConfig.basePath + "/avatar";

    private static final String USER_BASE_PATH = FOLDER_DATA + "/${userId}";

    private static final String FILE_PATH = FOLDER_DATA + "/${userId}/files";

    private static final String AVATAR_PATH = FOLDER_DATA + "/${userId}/avatar";

    private static final String IMAGE_PATH = FOLDER_DATA + "/${userId}/image";

    private static final String VIDEO_PATH = FOLDER_DATA + "/${userId}/video";

    private static final String FILE_LOCATION = "/${userId}/files/${fileId}${suffix}";

    private static final String IMAGE_THUMBNAIL_URL_FORMAT = "/image/thumbnail/%s?type=%s&uid=%s";

    private static final String IMAGE_PREVIEW_URL_FORMAT = "/image/preview/%s?type=%s&uid=%s";

    private static final String VIDEO_COVER_FORMAT = "/video/cover?file_id=%s&uid=%s";

    private static final String VIDEO_SOURCE_URL = "/video/${userId}/${fileId}/index.m3u8";

    public static String getFolderBaseAvatar() {
        return FOLDER_BASE_AVATAR;
    }

    public static String getUserBasePath(String userId) {
        return USER_BASE_PATH.replace("${userId}", userId);
    }

    public static String getFilePath(String userId){
        return FILE_PATH.replace("${userId}", userId);
    }

    public static String getAvatarPath(String userId){
        return AVATAR_PATH.replace("${userId}", userId);
    }

    public static String getImagePath(String userId) {
        return IMAGE_PATH.replace("${userId}", userId);
    }

    public static String getVideoPath(String userId){
        return VIDEO_PATH.replace("${userId}", userId);
    }

    public static String getFileStoragePath(String userId, String name) {
        return FILE_PATH.replace("${userId}", userId) + "/" + name;
    }

    public static String getFileStoragePath(String fileLocation) {
        return FOLDER_DATA + fileLocation;
    }

    public static String getFileLocation(String userId, String fileId, String suffix) {
        return  FILE_LOCATION.replace("${userId}", userId).replace("${fileId}", fileId).replace("${suffix}", suffix);
    }

    public static String getImageStoragePath(String userId, String name) {
        return IMAGE_PATH.replace("${userId}", userId) + "/" + name;
    }

    public static String getVideoCoverPath(String userId, String fileId) {
        return VIDEO_PATH.replace("${userId}", userId) + "/" + fileId + "/cover.png";
    }

    public static String getVideoSlicePath(String userId, String fileId, String quality, String index) {
        return VIDEO_PATH.replace("${userId}", userId) + "/" + fileId + "/" + quality + "/" + index;
    }

    public static String getImageThumbnailUrl(String fileId, String type, String userId) {
        return String.format(IMAGE_THUMBNAIL_URL_FORMAT, fileId, type, userId);
    }

    public static String getImageThumbnailUrl(String uri, String fileExtension) {
        String suffix = fileExtension.length() == 0 ? "" : "." + fileExtension;
        return UploadConfig.apiHost + "/image/thumbnail/" + uri + suffix;
    }

    public static String getImagePreviewUrl(String fileId, String type, String userId) {
        return String.format(IMAGE_PREVIEW_URL_FORMAT, fileId, type, userId);
    }

    public static String getImagePreviewUrl(String uri, String fileExtension) {
        String suffix = fileExtension.length() == 0 ? "" : "." + fileExtension;
        return UploadConfig.apiHost + "/image/preview/" + uri + suffix;
    }

    public static String getVideoCoverUrl(String fileId, String userId) {
        return String.format(VIDEO_COVER_FORMAT, fileId, userId);
    }

    public static String getVideoCoverUrl(String uri) {
        return UploadConfig.apiHost + "/video/cover/" + uri;
    }

    public static String getVideoSourceUrl(String userId, String fileId) {
        return VIDEO_SOURCE_URL.replace("${userId}", userId).replace("${fileId}", fileId);
    }

    public static String getVideoSourceUrl(String uri) {
        return UploadConfig.apiHost + "/video/" + uri + "/index.m3u8";
    }
}
