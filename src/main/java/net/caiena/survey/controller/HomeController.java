package net.caiena.survey.controller;

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
@RequestMapping("/")
public class HomeController {

    @ResponseBody
    public String init() {

        return "ok";
    }

}
