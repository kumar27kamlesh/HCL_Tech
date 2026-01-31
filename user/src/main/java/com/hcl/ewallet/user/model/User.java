package com.hcl.ewallet.user.model;

import com.hcl.ewallet.user.enums.Role;
import com.hcl.ewallet.user.enums.UserStatus;
import com.hcl.ewallet.user.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String country;
    private String currency;
    private UserStatus userStatus;
    private int age;
    @Enumerated(EnumType.STRING)
    private Role role;
    private UserType userType;
}

