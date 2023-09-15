package cn.zeniein.stardrive.common;

public enum ResponseEnum {
    /**
     * 成功响应
     */
    SUCCESS(0,"SUCCESS"),
    /**
     * 错误响应
     */
    ERROR(1001,"ERROR"),
    /**
     * 没找到文件
     */
    NOT_FOUND_FILE_BY_ID(10001, "The resource file_id cannot be found."),
    /**
     * 未找到文件夹
     */
    NOT_FOUND_FOLDER_BY_ID(10002, "The resource file_id cannot be found."),

    /**
     * 文件名重复
     */
    DUPLICATE_FILENAME(20001, "Duplicate filenames"),
    /**
     * 文件夹不支持下载
     */
    FOLDER_NONSUPPORT_DOWNLOAD(20002, "Folder nonsupport download"),
    /**
     * 容量不足
     */
    INSUFFICIENT_STORAGE_SPACE(20003, "Insufficient storage space"),
    /**
     * 文件上传任务不存在
     */
    FILE_UPLOAD_TASK_NOT_EXISTS(20004, "The upload task does not exist"),
    /**
     * 文件上传失败
     */
    FILE_UPLOAD_FAILURE(20005, "File upload failure"),
    /**
     * 文件检验MD5值失败
     */
    FILE_VALIDATION_FAILED(20006, "File validation failed"),
    /**
     * 头像上传失败
     */
    AVATAR_UPLOAD_FAILURE(20007, "Avatar upload failure"),
    /**
     * 回收站空间不足
     */
    RECYCLE_BIN_INSUFFICIENT_STORAGE_SPACE(20008, "Recycle bin insufficient storage space"),


    /**
     * 链接过期
     */
    EXPIRED_LINK(30001, "The link has expired."),
    /**
     * 登录失败
     */
    LOGIN_FAILURE(30002, "Login failure"),
    /**
     * 手机号存在
     */
    PHONE_NUMBER_ALREADY_EXISTS(30003, "The phone number already exists"),
    /**
     * 注册失败
     */
    REGISTER_FAILURE(30004, "Register failure"),
    /**
     * 手机号码不合法
     */
    PHONE_NUMBER_NOT_VALID(30005, "The phone number is not valid"),
    /**
     * 未知异常
     */
    UNEXPECTED(-1, "UNEXPECTED")
    ;

    private final int status;
    private final String msg;

    ResponseEnum(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}