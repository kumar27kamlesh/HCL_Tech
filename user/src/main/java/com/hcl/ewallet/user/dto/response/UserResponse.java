package com.hcl.ewallet.user.dto.response;

import com.hcl.ewallet.user.enums.UserStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String country;
    private String currency;
    private UserStatus userStatus;
    private int age;
}
