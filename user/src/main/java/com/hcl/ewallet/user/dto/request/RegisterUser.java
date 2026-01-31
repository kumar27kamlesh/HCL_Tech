package com.hcl.ewallet.user.dto.request;

import com.hcl.ewallet.user.enums.Role;
import com.hcl.ewallet.user.enums.UserType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegisterUser {
    private String name;
    private String email;
    private String password;
    private String country;
    private String currency;
    private int age;
    private UserType userType;
    private Role role;
}
