package net.caiena.survey.controller;

import net.caiena.survey.ApplicationTests;
import net.caiena.survey.entity.User;
import net.caiena.survey.enumeration.Role;
import net.caiena.survey.exception.ResourceNotFoundException;
import net.caiena.survey.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author bzumpano
 * @since 3/23/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationTests.class)
@WebAppConfiguration
@TestExecutionListeners(listeners={ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
@WithMockUser("admin")
public class UserControllerTest {

    private static final String ATTR_USER = "user";

    @Autowired
    private UserService userService;

    private User user;

    @Autowired
    private WebApplicationContext context;

    protected MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).
                apply(SecurityMockMvcConfigurers.springSecurity()).
                build();
    }

    @Before
    public void createUser() {
        user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setRole(Role.USER);
    }

    @Test
    public void successList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("users/index")).
                andExpect(MockMvcResultMatchers.model().attributeExists("users"));
    }

    @Test
    public void successShow() throws Exception {

        userService.save(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId())).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("users/show")).
                andExpect(MockMvcResultMatchers.model().
                            attribute(ATTR_USER, Matchers.hasProperty("id", Matchers.is(user.getId()))));
    }

    @Test
    public void errorShow() throws Exception {

        final Long nonExistingUserId = Long.MAX_VALUE;

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", nonExistingUserId)).
                                    andExpect(MockMvcResultMatchers.status().isNotFound()).
                                    andReturn();

        Assert.assertEquals(result.getResolvedException().getClass(), ResourceNotFoundException.class);
    }

    @Test
    public void successNew() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/users/new")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("users/new"));

    }

    @Test
    public void successCreate() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/users").
                    with(SecurityMockMvcRequestPostProcessors.csrf()).
                    param("username", "user").
                    param("password", "password").
                    param("role", Role.USER.name())).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("users/show")).
                andExpect(MockMvcResultMatchers.model().attributeExists(ATTR_USER));
    }

    @Test
    public void successEdit() throws Exception {

        userService.save(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/edit", user.getId())).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("users/edit"));

    }

    @Test
    public void successUpdate() throws Exception {

        final String oldPassword = user.getPassword();
        userService.save(user);

        user.setPassword("newPassword");


        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", user.getId()).
                        with(SecurityMockMvcRequestPostProcessors.csrf()).
                param("username", user.getUsername()).
                param("password", user.getPassword()).
                param("role", user.getRole().name())).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("users/show")).
                andExpect(MockMvcResultMatchers.model().attribute(ATTR_USER,
                        Matchers.hasProperty("password", Matchers.not(oldPassword))));

    }

    @Test
    public void successDestroy() throws Exception {

        userService.save(user);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", user.getId()).
                with(SecurityMockMvcRequestPostProcessors.csrf())).
                andExpect(MockMvcResultMatchers.status().isFound()).
                andExpect(MockMvcResultMatchers.redirectedUrl("/users"));

        Assert.assertNull(userService.find(user.getId()));

    }

}
