package com.sara.commerce_platform.auth;

import com.sara.commerce_platform.user.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService, InvalidatedTokenRepository invalidatedTokenRepository) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // extract the Bearer token from the request header
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        //  extract the username from the token
        String jwt = authHeader.substring(7);

        // check if token is on Blacklist (logout)
        boolean isTokenBlacklisted = invalidatedTokenRepository.existsById(jwt);
        if(isTokenBlacklisted) {
            filterChain.doFilter(request, response);
            return;
        }


        String username = jwtService.extractUsername(jwt);

        // check context
        if (username != null && SecurityContextHolder.getContext()
                .getAuthentication() == null) {

            // load user details by username
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // ensure the token is valid for the given user
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // create authToken
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails( new WebAuthenticationDetailsSource()
                        .buildDetails(request)
                );

                // if valid, set the authentication in the SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // continue chain
        filterChain.doFilter(request, response);

    }
}
