package cn.zeniein.stardrive.service.file.helper;

import cn.zeniein.stardrive.enums.FileTypeEnum;

import java.util.HashMap;
import java.util.Map;

public class FileTypeHelper {

    public static final Map<String, FileTypeEnum> FILE_TYPE_MAP = new HashMap<>(48);

    static {

        FILE_TYPE_MAP.put("jpg", FileTypeEnum.IMAGE);
        FILE_TYPE_MAP.put("png", FileTypeEnum.IMAGE);

        FILE_TYPE_MAP.put("mp3", FileTypeEnum.AUDIO);
        FILE_TYPE_MAP.put("m4a", FileTypeEnum.AUDIO);
        FILE_TYPE_MAP.put("wav", FileTypeEnum.AUDIO);
        FILE_TYPE_MAP.put("flac", FileTypeEnum.AUDIO);
        FILE_TYPE_MAP.put("aac", FileTypeEnum.AUDIO);

        FILE_TYPE_MAP.put("mp4", FileTypeEnum.VIDEO);
        FILE_TYPE_MAP.put("avi", FileTypeEnum.VIDEO);
        FILE_TYPE_MAP.put("mkv", FileTypeEnum.VIDEO);
        FILE_TYPE_MAP.put("mov", FileTypeEnum.VIDEO);

        FILE_TYPE_MAP.put("zip", FileTypeEnum.ARCHIVE);
        FILE_TYPE_MAP.put("rar", FileTypeEnum.ARCHIVE);
        FILE_TYPE_MAP.put("7z", FileTypeEnum.ARCHIVE);

        FILE_TYPE_MAP.put("docx", FileTypeEnum.WORD);
        FILE_TYPE_MAP.put("doc", FileTypeEnum.WORD);

        FILE_TYPE_MAP.put("pptx", FileTypeEnum.PPT);
        FILE_TYPE_MAP.put("ppt", FileTypeEnum.PPT);

        FILE_TYPE_MAP.put("xlsx", FileTypeEnum.EXCEL);
        FILE_TYPE_MAP.put("xls", FileTypeEnum.EXCEL);

        FILE_TYPE_MAP.put("pdf", FileTypeEnum.PDF);

        FILE_TYPE_MAP.put("c", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("cpp", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("java", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("py", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("cs", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("go", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("html", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("css", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("js", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("sql", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("json", FileTypeEnum.CODE);
        FILE_TYPE_MAP.put("xml", FileTypeEnum.CODE);

        FILE_TYPE_MAP.put("md", FileTypeEnum.MARKDOWN);

        FILE_TYPE_MAP.put("txt", FileTypeEnum.TXT);

        FILE_TYPE_MAP.put("exe", FileTypeEnum.EXE);

    }

}
