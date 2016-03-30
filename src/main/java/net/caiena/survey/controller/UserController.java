package net.caiena.survey.controller;

import net.caiena.survey.entity.User;
import net.caiena.survey.enumeration.Role;
import net.caiena.survey.exception.ResourceNotFoundException;
import net.caiena.survey.service.UserService;
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
 * @since 3/23/16
 */
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @ModelAttribute("roles")
    public Role[] roles() {
        return Role.values();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(final Model model) {

        model.addAttribute("users", userService.list());

        return "users/index";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public String show(final Model model, @PathVariable("id") final Long id) {

        final User user = userService.find(id);

        if (user == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        model.addAttribute("user", user);

        return "users/show";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/new")
    public String fresh(final Model model) {

        model.addAttribute("user", new User());

        return "users/new";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(final Model model, @ModelAttribute("user") final User user,
                        final BindingResult result) {

        userService.save(user);

        model.addAttribute("user", user);

        return "users/" + user.getId();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/edit")
    public String edit(final Model model, @PathVariable("id") final Long id) {

        final User user = userService.find(id);

        if (user == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        model.addAttribute("user", user);

        return "users/edit";
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public String update(final Model model, @PathVariable("id") final Long id,
                         @ModelAttribute("user") final User user,
                         final BindingResult result) {

        userService.save(user);

        model.addAttribute("user", user);

        return "users/" + id;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public String destroy(@PathVariable("id") final Long id) {

        userService.delete(id);

        return "redirect:/users";
    }
}
