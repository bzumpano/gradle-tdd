package com.bzumpano.sample;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bzumpano on 3/3/16.
 */
@Controller
@EnableAutoConfiguration
public class SampleController {

    @RequestMapping("/")
    public String init(final Model model) {

        model.addAttribute("name", "World");

        return "exibicao";
    }

    @RequestMapping("/teste")
    public String home(final Model model, @RequestParam(value="name", required=false, defaultValue="World") final String name) {

        model.addAttribute("name", name);

        return "exibicao";
    }

}
