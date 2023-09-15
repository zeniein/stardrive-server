package cn.zeniein.stardrive.common.constant;

public class FileConstant {

    /**
     * 缩略图文件前缀
     */
    public static final String THUMBNAIL_PREFIX = "thumbnail_";
    /**
     * 预览图片文件前缀
     */
    public static final String PREVIEW_PREFIX = "preview_";

    /**
     * 缩略图基准宽度
     */
    public static final int THUMBNAIL_STANDARD_WIDTH = 200;
    /**
     * 预览图片基准宽度
     */
    public static final int PREVIEW_STANDARD_WIDTH = 1200;

    /**
     * 头像图片基准宽度
     */
    public static final int AVATAR_STANDARD_WIDTH = 200;

    /**
     * 视频质量-原画
     */
    public static final String VIDEO_QUALITY_ORIGIN = "origin";
    /**
     * 视频质量-标清
     */
    public static final String VIDEO_QUALITY_STANDARD = "standard";
    /**
     * m3u8 索引名
     */
    public static final String M3U8_INDEX_NAME = "index.m3u8";

    /**
     * 文件状态，正常
     */
    public static final int STATUS_NORMAL = 0;
    /**
     * 文件状态，等待删除
     */
    public static final int STATUS_WAIT_DELETED = 1;
    /**
     * 文件状态，已删除(逻辑删除)
     */
    public static final int STATUS_DELETED = 2;
    /**
     * 文件状态，已删除(硬删除)
     */
    public static final int STATUS_HARD_DELETED= 3;


    /**
     * 回收站默认过期时间 10天
     */
    public static final int RECYCLE_BIN_DEFAULT_EXPIRED_DAYS = 10;
    /**
     * 回收站大小
     */
    public static final int RECYCLE_BIN_MAX_SIZE = 200;

    /**
     * 用户根目录
     */
    public static final String USER_ROOT_FOLDER = "root";
    /**
     * 一天
     */
    public static final long FILE_ADDRESS_DEFAULT_EXPIRES = 86400000;
    /**
     * 四小时
     */
    public static final long FILE_DOWNLOAD_ADDRESS_EXPIRES = 14400000;



}
