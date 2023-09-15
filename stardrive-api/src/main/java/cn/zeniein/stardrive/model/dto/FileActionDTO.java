package cn.zeniein.stardrive.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FileActionDTO {

    private String fileId;

    private String parentFileId;

    private String toParentFileId;

    private String name;

    private String mode;

    /**
     * 文件ID集合
     */
    private List<String> fileIds;

}
