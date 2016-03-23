package net.caiena.survey.controller;

import net.caiena.survey.entity.User;
import net.caiena.survey.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Created by bzumpano on 3/23/16.
 */
@WithMockUser("admin")
public class UserControllerTest extends BaseControllerTest {

    @Autowired
    private UserService userService;

    @Test
    public void successList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("users"));
    }

    @Test
    public void successShow() throws Exception {

        User user = userService.save(new User());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    public void errorShow() throws Exception {

        Long nonExistingUserId = Long.MAX_VALUE;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", nonExistingUserId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/error"));
    }

}
