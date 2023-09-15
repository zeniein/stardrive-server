package cn.zeniein.stardrive.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecycleBinVO {

    private String id;

    private String name;

    private Integer type;

    private Long size;

    private String thumbnailUrl;

    private LocalDateTime trashedAt;

    private LocalDateTime expiredAt;

    private String fileExtension;

}
