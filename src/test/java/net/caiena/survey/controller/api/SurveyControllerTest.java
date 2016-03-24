package net.caiena.survey.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.caiena.survey.controller.BaseControllerTest;
import net.caiena.survey.entity.Survey;
import net.caiena.survey.entity.User;
import net.caiena.survey.enumeration.Role;
import net.caiena.survey.service.SurveyService;
import net.caiena.survey.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

/**
 * @author bzumpano
 * @since 3/24/16
 */
public class SurveyControllerTest extends BaseControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SurveyService surveyService;

    private User user;

    private Survey survey;

    @Before
    public void createUser() {
        user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setRole(Role.ADMIN);

        userService.save(user);
    }

    @Before
    public void createSurvey() {
        survey = new Survey();
        survey.setDescription("Test survey");
        surveyService.save(survey);
    }

    @Test
    public void successList() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/surveys")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(user.getUsername(), user.getPassword())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.isA(List.class)));

    }

    @Test
    public void successShow() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/surveys/{id}", survey.getId())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(user.getUsername(), user.getPassword())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(survey.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(survey.getDescription())));

    }

    @Test
    public void successUpdate() throws Exception {

        final User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setRole(Role.ADMIN);
        userService.save(user);

        final String oldDescription = survey.getDescription();
        survey.setDescription("New description");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/surveys/{id}", survey.getId())
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(survey)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(survey.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.not(oldDescription)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(survey.getDescription())));

    }

    private String asJsonString(final Survey survey) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(survey);
    }

}
