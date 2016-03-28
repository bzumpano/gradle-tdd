package net.caiena.survey.controller;

import net.caiena.survey.entity.Survey;
import net.caiena.survey.exception.ResourceNotFoundException;
import net.caiena.survey.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author bzumpano
 * @since 3/24/16
 */
@Controller
@RequestMapping("/surveys")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(final Model model) {

        model.addAttribute("surveys", surveyService.list());

        return "surveys/index";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public String show(final Model model, @PathVariable("id") final Long id) {

        final Survey survey = surveyService.find(id);

        if (survey == null) {
            throw new ResourceNotFoundException("Survey not found.");
        }

        model.addAttribute("survey", survey);

        return "surveys/show";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/new")
    public String fresh(final Model model) {
        return "surveys/new";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(final Model model, @ModelAttribute("survey") final Survey survey,
                         final BindingResult result) {

        surveyService.save(survey);

        model.addAttribute("survey", survey);

        return "surveys/show";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/edit")
    public String edit(final Model model, @PathVariable("id") final Long id) {

        final Survey survey = surveyService.find(id);

        if (survey == null) {
            throw new ResourceNotFoundException("Survey not found.");
        }

        model.addAttribute("survey", survey);

        return "surveys/edit";
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public String update(final Model model, @ModelAttribute("survey") final Survey survey,
                         final BindingResult result) {

        surveyService.save(survey);

        model.addAttribute("survey", survey);

        return "surveys/show";
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public String destroy(@PathVariable("id") final Long id) {

        surveyService.delete(id);

        return "redirect:/surveys";
    }
}
