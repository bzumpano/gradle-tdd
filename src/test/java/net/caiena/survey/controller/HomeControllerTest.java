package net.caiena.survey.controller;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Created by bzumpano on 3/21/16.
 */
public class HomeControllerTest extends BaseControllerTest {

    @Test
    @WithMockUser("admin")
    public void successAccessHomePage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
