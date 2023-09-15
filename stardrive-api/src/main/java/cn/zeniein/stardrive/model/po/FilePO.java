package cn.zeniein.stardrive.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("file")
@NoArgsConstructor
@AllArgsConstructor
public class FilePO implements Serializable {

    @TableId(type = IdType.INPUT)
    private String id;

    private String fileLocation;

    private String userId;

    private String name;

    private Integer type;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 修改时间
     */
    private LocalDateTime modifiedAt;
    /**
     * 放入回收站时间
     */
    private LocalDateTime trashedAt;
    /**
     * 过期时间
     */
    private LocalDateTime expiredAt;

    private String parentFileId;

    private String fileMd5;
    /**
     * 文件状态 0-正常 1-待删除 2-已删除
     */
    private Integer status;

    private Long size;

    private String fileExtension;

}
