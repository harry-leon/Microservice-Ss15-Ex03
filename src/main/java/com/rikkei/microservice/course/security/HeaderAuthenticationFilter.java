package com.rikkei.microservice.course.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_USER_NAME = "X-User-Name";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String roleHeader = request.getHeader(HEADER_USER_ROLE);
        String usernameHeader = request.getHeader(HEADER_USER_NAME);

        if (StringUtils.hasText(roleHeader)) {
            String principal = StringUtils.hasText(usernameHeader) ? usernameHeader : "gateway-user";

            // Roles might be comma-separated or prefixed (e.g. ROLE_STUDENT or STUDENT)
            String[] rawRoles = roleHeader.split(",");
            List<GrantedAuthority> authorities = new ArrayList<>();

            for (String rawRole : rawRoles) {
                String role = rawRole.trim();
                if (!role.isEmpty()) {
                    // Add authority as-is (e.g., STUDENT or INSTRUCTOR)
                    authorities.add(new SimpleGrantedAuthority(role));

                    // Also support ROLE_ prefixed if not present, and vice versa
                    if (role.startsWith("ROLE_")) {
                        authorities.add(new SimpleGrantedAuthority(role.substring(5)));
                    } else {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                }
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
