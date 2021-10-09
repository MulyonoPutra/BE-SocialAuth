package com.bolitik.id.controller;



import com.bolitik.id.dto.TokenDTO;
import com.bolitik.id.entity.Role;
import com.bolitik.id.entity.Users;
import com.bolitik.id.enums.RoleName;
import com.bolitik.id.security.JWT.JWTProvider;
import com.bolitik.id.service.RoleService;
import com.bolitik.id.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/oauth")
@CrossOrigin
public class OAuthController {

    @Value("${google.clientId}")
    public String googleClientId;

    @Value("${secretPsw}")
    String secretPsw;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTProvider jwtProvider;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @PostMapping("/google")
    public ResponseEntity<TokenDTO> google(@RequestBody TokenDTO tokenDto) throws IOException {
        final NetHttpTransport transport = new NetHttpTransport();
        final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
        GoogleIdTokenVerifier.Builder verifier =
                new GoogleIdTokenVerifier.Builder(transport, jacksonFactory)
                        .setAudience(Collections.singletonList(googleClientId));
        final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), tokenDto.getValue());
        final GoogleIdToken.Payload payload = googleIdToken.getPayload();
        Users users;
        if(userService.existsEmail(payload.getEmail()))
            users = userService.getByEmail(payload.getEmail()).get();
        else
            users = saveUsers(payload.getEmail());
        TokenDTO tokenRes = login(users);
        return new ResponseEntity(tokenRes, HttpStatus.OK);
    }

    @PostMapping("/facebook")
    public ResponseEntity<TokenDTO> facebook(@RequestBody TokenDTO tokenDto) throws IOException {
        Facebook facebook = new FacebookTemplate(tokenDto.getValue());
        final String [] fields = {"email", "picture"};
        User user = facebook.fetchObject("me", User.class, fields);
        Users users;
        if(userService.existsEmail(user.getEmail()))
            users = userService.getByEmail(user.getEmail()).get();
        else
            users = saveUsers(user.getEmail());
        TokenDTO tokenRes = login(users);
        return new ResponseEntity(tokenRes, HttpStatus.OK);
    }

    private TokenDTO login(Users users){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(users.getEmail(), secretPsw)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        TokenDTO tokenDto = new TokenDTO();
        tokenDto.setValue(jwt);
        return tokenDto;
    }

    private Users saveUsers(String email){
        Users users = new Users(email, passwordEncoder.encode(secretPsw));
        Role roleUsers = new Role();
        if (roleUsers == null) {
            return null;
        }
        roleUsers = roleService.getByRoleName(RoleName.ROLE_USER).isPresent() ? roleService.getByRoleName(RoleName.ROLE_USER).get() : null;
        Set<Role> roles = new HashSet<>();
        roles.add(roleUsers);
        users.setRoles(roles);
        return userService.save(users);
    }
}
