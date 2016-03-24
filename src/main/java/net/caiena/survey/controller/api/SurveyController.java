package net.caiena.survey.controller.api;

import net.caiena.survey.entity.Survey;
import net.caiena.survey.exception.ResourceNotFoundException;
import net.caiena.survey.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bzumpano
 * @since 3/24/16
 */
@RestController("apiSurveyController")
@RequestMapping("/api/surveys")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Survey> index() {

        final List<Survey> surveys = surveyService.list();

        return surveys;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Survey show(@PathVariable("id") final Long id) {

        final Survey survey = surveyService.find(id);

        if (survey == null) {
            throw new ResourceNotFoundException("Survey not found.");
        }

        return survey;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public Survey update(@PathVariable("id") final Long id,
                         @RequestBody final Survey survey,
                         final BindingResult result) {

        survey.setId(id);
        surveyService.save(survey);

        return survey;
    }
}
