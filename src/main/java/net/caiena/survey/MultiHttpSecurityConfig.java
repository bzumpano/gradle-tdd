package net.caiena.survey;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author bzumpano
 * @since 3/23/16
 *
 * @see <a href="http://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html#multiple-httpsecurity"></a>
 */
@EnableWebSecurity
public class MultiHttpSecurityConfig {

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.
                    antMatcher("/api/**").
                        authorizeRequests().
                    anyRequest().
                        hasRole("ADMIN").
                        and().
                    httpBasic().
                        and().
                    csrf().disable();
        }

    }

    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.
                    authorizeRequests().
                        anyRequest().authenticated().
                        and().
                    formLogin();
        }
    }

}
