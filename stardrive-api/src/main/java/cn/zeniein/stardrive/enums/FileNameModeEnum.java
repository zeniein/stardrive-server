package cn.zeniein.stardrive.enums;

/**
 * 重名的时候支持的文件命名模式，
 */
public enum FileNameModeEnum {

    /**
     * 自动重命名
     */
    AUTO_RENAME("auto_rename"),
    /**
     * 拒绝
     */
    REFUSE("refuse"),
    /**
     * 忽略
     */
    IGNORE("ignore")
    ;


    private final String name;

    FileNameModeEnum(String name){
        this.name = name;
    }

    public static FileNameModeEnum match(String nameMode){
        for (FileNameModeEnum value : FileNameModeEnum.values()) {
            if(value.name.equals(nameMode)) {
                return value;
            }
        }
        return FileNameModeEnum.IGNORE;
    }

}
