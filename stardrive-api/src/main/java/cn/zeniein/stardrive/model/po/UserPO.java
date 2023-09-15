package cn.zeniein.stardrive.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("user")
public class UserPO implements Serializable {

    @TableId(type = IdType.INPUT)
    private String id;

    private String email;

    private String phone;

    private String password;

    private String nickname;

    private LocalDateTime createTime;

    private String avatar;

    private String role;
}
