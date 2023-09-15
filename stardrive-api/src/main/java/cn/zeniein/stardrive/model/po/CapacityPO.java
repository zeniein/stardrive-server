package cn.zeniein.stardrive.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("capacity")
public class CapacityPO {
    @TableId(type = IdType.INPUT)
    private String userId;

    private Long useSpace;

    private Long totalSpace;

}
