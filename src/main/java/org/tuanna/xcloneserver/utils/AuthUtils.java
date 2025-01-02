package org.tuanna.xcloneserver.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.tuanna.xcloneserver.modules.auth.AuthenticationPrincipal;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;

public class AuthUtils {

    public static void setAuthentication(JWTPayload jwtPayload) {
        if (jwtPayload == null || CollectionUtils.isEmpty(jwtPayload.getPermissions())) {
            return;
        }
        AuthenticationPrincipal principal = AuthenticationPrincipal.builder()
                .userId(jwtPayload.getSubjectId())
                .tokenId(jwtPayload.getTokenId())
                .permissions(jwtPayload.getPermissions())
                .build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                jwtPayload.getPermissions().stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static AuthenticationPrincipal getAuthenticationPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticationPrincipal)) {
            return null;
        }
        return (AuthenticationPrincipal) authentication.getPrincipal();
    }

}