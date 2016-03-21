package com.bzumpano.sample.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by bzumpano on 3/3/16.
 */
@Controller
@EnableAutoConfiguration
public class HomeController {

    @RequestMapping("/")
    public String init(final Model model) {

        model.addAttribute("name", "World");

        return "exibicao";
    }

    @RequestMapping("/hello")
    public String home(final Model model, @RequestParam(value="name", required=false, defaultValue="World") final String name) {

        model.addAttribute("name", name);

        return "exibicao";
    }

}
