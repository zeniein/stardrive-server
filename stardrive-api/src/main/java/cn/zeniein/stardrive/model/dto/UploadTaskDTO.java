package cn.zeniein.stardrive.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadTaskDTO {
    private String md5;
    private String name;
    private Long size;


    private String uploadId;
}
