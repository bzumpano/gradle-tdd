package net.caiena.survey;

import net.caiena.survey.entity.User;
import net.caiena.survey.entity.builder.UserBuilder;
import net.caiena.survey.enumeration.Role;
import net.caiena.survey.service.UserService;
import org.junit.After;
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
@SpringApplicationConfiguration(ApplicationTests.class)
@WebAppConfiguration
@TestExecutionListeners(listeners={ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class MultiHttpSecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    protected MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User user;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).
                apply(springSecurity()).
                build();

        user = new UserBuilder().role(Role.ADMIN).build();
        user = userService.save(user);
    }

    @After
    public void destroy() {
        userService.delete(user.getId());
    }


    @Test
    public void authenticationFailedFormLoginBadCredentials() throws Exception {
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user("notfound").password("invalid")).
                andExpect(MockMvcResultMatchers.status().isFound()).
                andExpect(MockMvcResultMatchers.redirectedUrl("/login?error")).
                andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @Test
    @WithMockUser("admin")
    public void authenticationFailedFormLoginBadCsrf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/").with(SecurityMockMvcRequestPostProcessors.csrf().useInvalidToken())).
                andDo(MockMvcResultHandlers.print()).
                andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void authenticationSuccessFormLogin() throws Exception {
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user(user.getUsername()).password(user.getPassword())).
                andExpect(MockMvcResultMatchers.status().isFound()).
                andExpect(MockMvcResultMatchers.redirectedUrl("/")).
                andExpect(SecurityMockMvcResultMatchers.authenticated().withUsername(user.getUsername()));
    }

    @Test
    public void authenticationFailureHttpDigest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/notfound").
                with(SecurityMockMvcRequestPostProcessors.
                        digest("invalidUser").
                        password("invalidPassword").
                        realm("invalidRealm"))).
                andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @Test
    public void authenticationSuccessHttpDigest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/notfound").
                with(SecurityMockMvcRequestPostProcessors.
                        digest(user.getUsername()).
                                    password(user.getPassword()).
                                    realm("Digest Realm"))).
                andDo(MockMvcResultHandlers.print()).
                andExpect(MockMvcResultMatchers.status().isNotFound());
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
