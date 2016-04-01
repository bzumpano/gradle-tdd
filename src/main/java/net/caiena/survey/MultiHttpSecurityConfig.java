package net.caiena.survey;

import net.caiena.survey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

/**
 * @author bzumpano
 * @since 3/23/16
 *
 * @see <a href="http://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html#multiple-httpsecurity"></a>
 */
@EnableWebSecurity
public class MultiHttpSecurityConfig {

    /**
     * @see <a href="https://docs.spring.io/spring-security/site/docs/3.0.x/reference/basic.html"></a>
     */
    @Configuration
    @Order(1)
    public static class DigestSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserService userService;

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.
                    antMatcher("/api/**").
                    authorizeRequests().
                    anyRequest().
                    hasAnyRole("USER", "ADMIN").
                    and().
                    addFilter(digestAuthenticationFilter(digestEntryPoint())).
                    csrf().disable();
        }


        @Bean
        public DigestAuthenticationEntryPoint digestEntryPoint() {
            final DigestAuthenticationEntryPoint digestEntryPoint = new DigestAuthenticationEntryPoint();
            digestEntryPoint.setKey("acegi");
            digestEntryPoint.setRealmName("Digest Realm");

            return digestEntryPoint;
        }

        @Bean
        public DigestAuthenticationFilter digestAuthenticationFilter(final DigestAuthenticationEntryPoint digestAuthenticationEntryPoint) {
            final DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
            digestAuthenticationFilter.setAuthenticationEntryPoint(digestAuthenticationEntryPoint);
            digestAuthenticationFilter.setUserDetailsService(userService);
            digestAuthenticationFilter.setCreateAuthenticatedToken(true);

            return digestAuthenticationFilter;
        }

    }

    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserService userService;

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.
                    authorizeRequests().
                        anyRequest().authenticated().
                        and().
                    formLogin().
                        and().
                    rememberMe();
        }

        @Override
        protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userService);
        }
    }

}
