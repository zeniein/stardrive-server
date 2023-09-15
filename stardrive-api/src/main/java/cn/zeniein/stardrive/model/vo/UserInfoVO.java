package cn.zeniein.stardrive.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class UserInfoVO {
    String userId;
    String nickname;
    String role;
    String avatar;
}
