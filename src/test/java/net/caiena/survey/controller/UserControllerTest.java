package net.caiena.survey.controller;

import net.caiena.survey.entity.User;
import net.caiena.survey.enumeration.Role;
import net.caiena.survey.exception.ResourceNotFoundException;
import net.caiena.survey.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author bzumpano
 * @since 3/23/16
 */
@WithMockUser("admin")
public class UserControllerTest extends BaseControllerTest {

    private static final String ATTR_USER = "user";

    @Autowired
    private UserService userService;

    private User user;

    @Before
    public void createUser() {
        user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setRole(Role.USER);
    }


    @Test
    public void successList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("users"));
    }

    @Test
    public void successShow() throws Exception {

        userService.save(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/show"))
                .andExpect(MockMvcResultMatchers.model()
                            .attribute(ATTR_USER, Matchers.hasProperty("id", Matchers.is(user.getId()))));
    }

    @Test
    public void errorShow() throws Exception {

        final Long nonExistingUserId = Long.MAX_VALUE;

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", nonExistingUserId))
                                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                                    .andReturn();

        assertEquals(result.getResolvedException().getClass(), ResourceNotFoundException.class);
    }

    @Test
    public void successNew() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/users/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/new"));

    }

    @Test
    public void successCreate() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .param("username", "user")
                    .param("password", "password")
                    .param("role", Role.USER.name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists(ATTR_USER));
    }

    @Test
    public void successEdit() throws Exception {

        userService.save(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/edit", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/edit"));

    }

    @Test
    public void successUpdate() throws Exception {

        final String oldPassword = user.getPassword();
        userService.save(user);

        user.setPassword("newPassword");


        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", user.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("username", user.getUsername())
                        .param("password", user.getPassword())
                        .param("role", user.getRole().name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/show"))
                .andExpect(MockMvcResultMatchers.model().attribute(ATTR_USER,
                        Matchers.hasProperty("password", Matchers.not(oldPassword))));

    }

    @Test
    public void successDestroy() throws Exception {

        userService.save(user);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", user.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users"));

        assertNull(userService.find(user.getId()));

    }

}
