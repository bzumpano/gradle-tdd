package net.caiena.survey.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.caiena.survey.ApplicationTests;
import net.caiena.survey.entity.Survey;
import net.caiena.survey.entity.User;
import net.caiena.survey.entity.builder.UserBuilder;
import net.caiena.survey.enumeration.Role;
import net.caiena.survey.service.SurveyService;
import net.caiena.survey.service.UserService;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

/**
 * @author bzumpano
 * @since 3/24/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationTests.class)
@WebAppConfiguration
@TestExecutionListeners(listeners={ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class SurveyControllerTest {

    @Autowired
    private WebApplicationContext context;

    protected MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private SurveyService surveyService;

    private User user;

    private Survey survey;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).
                apply(SecurityMockMvcConfigurers.springSecurity()).
                build();

        user = new UserBuilder().role(Role.ADMIN).build();
        user = userService.save(user);

        createSurvey();
    }

    @After
    public void destroy() {
        userService.delete(user.getId());
    }

    @Test
    public void successList() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/surveys")
                .with(SecurityMockMvcRequestPostProcessors.
                        digest(user.getUsername()).
                        password(user.getPassword()).
                        realm("Digest Realm")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.isA(List.class)));

    }

    @Test
    public void successShow() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/surveys/{id}", survey.getId())
                .with(SecurityMockMvcRequestPostProcessors.
                        digest(user.getUsername()).
                        password(user.getPassword()).
                        realm("Digest Realm")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(survey.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(survey.getDescription())));

    }

    @Test
    public void successUpdate() throws Exception {

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

    private void createSurvey() {
        survey = new Survey();
        survey.setDescription("Test survey");
        surveyService.save(survey);
    }

}
