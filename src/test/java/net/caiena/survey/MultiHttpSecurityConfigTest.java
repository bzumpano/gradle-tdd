package net.caiena.survey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * @author bzumpano
 * @since 3/23/16
 *
 * Se apontar {@link SpringApplicationConfiguration} para o {@link ApplicationTests} irá fazer scan completo no projeto,
 * ao invés disso devemos configurar somente o necessário. Isso reduz o tempo de carregamento do Spring Boot.
 *
 * @see <a href="https://github.com/spring-projects/spring-security/blob/master/test/src/test/java/org/springframework/security/test/web/servlet/response/SecurityMockMvcResultMatchersTests.java"></a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({MultiHttpSecurityConfig.class, MultiHttpSecurityConfigTest.Config.class})
@WebAppConfiguration
@TestExecutionListeners(listeners={ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class MultiHttpSecurityConfigTest {

    private static final String USER_NAME = "user";
    private static final String USER_PWD = "password";

    @Autowired
    private WebApplicationContext context;

    protected MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void authenticationFailedFormLoginBadCredentials() throws Exception {
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user("notfound").password("invalid"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?error"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @Test
    @WithMockUser("admin")
    public void authenticationFailedFormLoginBadCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/").with(SecurityMockMvcRequestPostProcessors.csrf().useInvalidToken()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void authenticationSuccessFormLogin() throws Exception {
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user(USER_NAME).password(USER_PWD))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated().withUsername(USER_NAME));
    }

    @Test
    public void authenticationFailureHttpBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/notfound")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("notfound", "invalid")))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @Test
    public void authenticationSuccessHttpBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/notfound")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_NAME, USER_PWD)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @EnableWebSecurity
    @EnableWebMvc
    @Order(Ordered.LOWEST_PRECEDENCE)
    static class Config extends WebSecurityConfigurerAdapter {

        @Autowired
        public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .inMemoryAuthentication()
                    .withUser(USER_NAME).roles("ADMIN").password(USER_PWD);
        }
    }

    @Controller
    static class TestCsrfController {

        @RequestMapping(value = "/", method = RequestMethod.POST)
        @ResponseBody
        public String index() {
            return "test";
        }
    }
}
