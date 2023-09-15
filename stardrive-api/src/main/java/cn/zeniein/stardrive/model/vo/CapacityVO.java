package cn.zeniein.stardrive.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class CapacityVO {

    private Long useSpace;

    private Long totalSpace;

}
