package cn.zeniein.stardrive.enums;

import cn.zeniein.stardrive.service.file.helper.FileTypeHelper;

import java.util.Objects;

public enum FileTypeEnum {

    /**
     * 文件夹
     */
    FOLDER(0),
    /**
     * 图片
     */
    IMAGE(1),
    /**
     * 音频
     */
    AUDIO(2),
    /**
     * 视频
     */
    VIDEO(3),
    /**
     * 压缩包
     */
    ARCHIVE(4),
    /**
     * word
     */
    WORD(5),
    /**
     * ppt
     */
    PPT(6),
    /**
     * excel
     */
    EXCEL(7),
    /**
     * PDF
     */
    PDF(8),
    /**
     * 代码文件
     */
    CODE(9),
    /**
     * markdown文件
     */
    MARKDOWN(10),
    /**
     * 文本文件
     */
    TXT(11),
    /**
     * 可执行文件
     */
    EXE(12),
    /**
     * 其他文件
     */
    OTHER(99),
    ;

    private final int type;

    FileTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    /**
     * 根据文件后缀获取类型
     * @param fileSuffix 文件后缀
     * @return the fileTypeEnum
     */
    public static FileTypeEnum byFileExtension(String fileSuffix) {
        String suffix = fileSuffix.toLowerCase();
        FileTypeEnum fileTypeEnum = FileTypeHelper.FILE_TYPE_MAP.get(suffix);
        return Objects.requireNonNullElse(fileTypeEnum, FileTypeEnum.OTHER);
    }
}
