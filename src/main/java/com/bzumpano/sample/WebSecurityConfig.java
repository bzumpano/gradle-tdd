package com.bzumpano.sample;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by bzumpano on 3/21/16.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().and()
            .csrf().and()
            .formLogin().and()
            .authorizeRequests()
            .antMatchers("/", "/assets/**").permitAll()
                .antMatchers("/**").fullyAuthenticated();
    }

}
