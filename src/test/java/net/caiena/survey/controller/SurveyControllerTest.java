package net.caiena.survey.controller;

import net.caiena.survey.ApplicationTests;
import net.caiena.survey.entity.Survey;
import net.caiena.survey.exception.ResourceNotFoundException;
import net.caiena.survey.service.SurveyService;
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
@WithMockUser("admin")
public class SurveyControllerTest {

    @Autowired
    private WebApplicationContext context;

    protected MockMvc mockMvc;

    @Autowired
    private SurveyService surveyService;

    private Survey survey;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).
                apply(SecurityMockMvcConfigurers.springSecurity()).
                build();
    }

    @Before
    public void createSurvey() {
        survey = new Survey();
        survey.setDescription("Test survey");
        surveyService.save(survey);
    }


    @Test
    public void successList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/surveys")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("surveys/index")).
                andExpect(MockMvcResultMatchers.model().attributeExists("surveys"));
    }

    @Test
    public void successShow() throws Exception {

        surveyService.save(survey);

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/{id}", survey.getId())).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("surveys/show")).
                andExpect(MockMvcResultMatchers.model().
                        attribute("survey", Matchers.hasProperty("id", Matchers.is(survey.getId()))));
    }

    @Test
    public void errorShow() throws Exception {

        final Long nonExistingUserId = Long.MAX_VALUE;

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/surveys/{id}", nonExistingUserId)).
                andExpect(MockMvcResultMatchers.status().isNotFound()).
                andReturn();

        Assert.assertEquals(result.getResolvedException().getClass(), ResourceNotFoundException.class);
    }

    @Test
    public void successNew() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/new")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("surveys/new"));

    }

    @Test
    public void successCreate() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/surveys").
                with(SecurityMockMvcRequestPostProcessors.csrf()).
                param("description", "Test survey")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("surveys/show")).
                andExpect(MockMvcResultMatchers.model().attributeExists("survey"));
    }

    @Test
    public void successEdit() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/{id}/edit", survey.getId())).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("surveys/edit"));

    }

    @Test
    public void successUpdate() throws Exception {

        final String oldDescription = survey.getDescription();
        survey.setDescription("New description");

        mockMvc.perform(MockMvcRequestBuilders.put("/surveys/{id}", survey.getId()).
                with(SecurityMockMvcRequestPostProcessors.csrf()).
                param("description", survey.getDescription())).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.view().name("surveys/show")).
                andExpect(MockMvcResultMatchers.model().attribute("survey",
                        Matchers.hasProperty("description", Matchers.not(oldDescription))));

    }

    @Test
    public void successDestroy() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/surveys/{id}", survey.getId()).
                with(SecurityMockMvcRequestPostProcessors.csrf())).
                andExpect(MockMvcResultMatchers.status().isFound()).
                andExpect(MockMvcResultMatchers.redirectedUrl("/surveys"));

        Assert.assertNull(surveyService.find(survey.getId()));

    }

}
