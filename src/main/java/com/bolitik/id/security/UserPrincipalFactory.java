package com.bolitik.id.security;

import com.bolitik.id.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipalFactory {

    public static UserPrincipal build(Users user){
        List<GrantedAuthority> authorities =
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName().name())).collect(Collectors.toList());
        return new UserPrincipal(user.getEmail(), user.getPassword(), authorities);
    }
}
