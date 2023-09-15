package cn.zeniein.stardrive.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileVO {

    private String id;

    private String name;

    private Integer type;

    private String thumbnailUrl;

    private String previewUrl;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private String parentFileId;

    private String fileMd5;

    private Long size;

    private String fileExtension;

}
