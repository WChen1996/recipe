package com.info7255.recipe.config;
import com.info7255.recipe.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.security.*;
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // token based distributed authentication, so no session is required
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // Configure permissions
                .authorizeRequests()
                // Login login CaptchaImage allows anonymous access
                .antMatchers("/token").anonymous()
                // Static resource release
                .antMatchers("/**").permitAll()
                // Except for all the above requests, authentication is required
                .anyRequest().authenticated()
                .and()
                // Allows cross domain access, equivalent to corsConfigurationSource of config class
                .cors()
                .and()
                // CRSF is disabled. Because the session is not used, cross site csrf attack defense is disabled. Otherwise, the login cannot succeed
                .csrf().disable();


        // Add JWT filter
        //http.addFilterBefore(jwtAuthenticationFilter,OncePerRequestFilter.class);
    }


}

