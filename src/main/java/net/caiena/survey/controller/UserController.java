package net.caiena.survey.controller;

import net.caiena.survey.entity.User;
import net.caiena.survey.exception.ResourceNotFoundException;
import net.caiena.survey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by bzumpano on 3/23/16.
 */
@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("users", userService.list());

        return "users/index";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public String show(Model model, @PathVariable("id") Long id) {

        User user = userService.find(id);

        if (user == null) throw new ResourceNotFoundException("Usuário não existe.");

        model.addAttribute("user", user);

        return "users/show";
    }
}
