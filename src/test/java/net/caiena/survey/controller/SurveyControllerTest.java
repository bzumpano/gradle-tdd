package net.caiena.survey.controller;

import net.caiena.survey.entity.Survey;
import net.caiena.survey.exception.ResourceNotFoundException;
import net.caiena.survey.service.SurveyService;
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
 * @since 3/24/16
 */
@WithMockUser("admin")
public class SurveyControllerTest extends BaseControllerTest {

    @Autowired
    private SurveyService surveyService;

    private Survey survey;

    @Before
    public void createSurvey() {
        survey = new Survey();
        survey.setDescription("Test survey");
        surveyService.save(survey);
    }


    @Test
    public void successList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/surveys"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("surveys/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("surveys"));
    }

    @Test
    public void successShow() throws Exception {

        surveyService.save(survey);

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/{id}", survey.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("surveys/show"))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("survey", Matchers.hasProperty("id", Matchers.is(survey.getId()))));
    }

    @Test
    public void errorShow() throws Exception {

        final Long nonExistingUserId = Long.MAX_VALUE;

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/surveys/{id}", nonExistingUserId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        assertEquals(result.getResolvedException().getClass(), ResourceNotFoundException.class);
    }

    @Test
    public void successNew() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("surveys/new"));

    }

    @Test
    public void successCreate() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/surveys")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("description", "Test survey"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("surveys/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("survey"));
    }

    @Test
    public void successEdit() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/{id}/edit", survey.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("surveys/edit"));

    }

    @Test
    public void successUpdate() throws Exception {

        final String oldDescription = survey.getDescription();
        survey.setDescription("New description");

        mockMvc.perform(MockMvcRequestBuilders.put("/surveys/{id}", survey.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("description", survey.getDescription()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("surveys/show"))
                .andExpect(MockMvcResultMatchers.model().attribute("survey",
                        Matchers.hasProperty("description", Matchers.not(oldDescription))));

    }

    @Test
    public void successDestroy() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/surveys/{id}", survey.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/surveys"));

        assertNull(surveyService.find(survey.getId()));

    }

}