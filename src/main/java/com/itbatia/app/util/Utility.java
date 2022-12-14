package com.itbatia.app.util;

import com.itbatia.app.model.User;
import com.itbatia.app.security.UserDetailsImpl;
import com.itbatia.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class Utility {

    private final UserService userService;

    public User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userService.findByUsername(userDetails.getUsername()).get();
    }

    public static Filter getSecurityFilter() {
        return (request, response, chain) -> {
            try {
                chain.doFilter(request, response);
            } catch (AccessDeniedException e) {
                log.error("IN getSecurityFilter - Caught AccessDeniedException: '" + e.getMessage() + "' - " + LocalDateTime.now());
            }
        };
    }
}
