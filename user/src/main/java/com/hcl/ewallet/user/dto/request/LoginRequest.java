package com.hcl.ewallet.user.dto.request;

import com.hcl.ewallet.user.enums.Role;
import com.hcl.ewallet.user.enums.UserStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequest {
    private String email;
    private String password;
    private Role role;
}
