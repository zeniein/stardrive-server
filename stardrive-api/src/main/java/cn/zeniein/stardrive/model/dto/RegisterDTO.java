package cn.zeniein.stardrive.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {

    @NotNull(message = "密码不能为空")
    private String password;
    @NotNull
    private String phone;
    @NotNull
    private String code;
}
