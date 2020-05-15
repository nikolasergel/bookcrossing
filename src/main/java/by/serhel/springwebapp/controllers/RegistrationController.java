package by.serhel.springwebapp.controllers;

import by.serhel.springwebapp.entities.Role;
import by.serhel.springwebapp.entities.User;
import by.serhel.springwebapp.repositories.UserRepository;
import by.serhel.springwebapp.service.UserService;
import org.hibernate.id.AbstractUUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam("password1") String passwordComfirm,
                          @Valid User user,
                          BindingResult bindingResult,
                          Model model
    ){
        boolean passwordConfirmEmpty = StringUtils.isEmpty(passwordComfirm);

        if(passwordConfirmEmpty){
            model.addAttribute("password1Error", "Password confirmation cannot be empty");
        }

        if(user.getPassword() != null && !user.getPassword().equals(passwordComfirm)){
            model.addAttribute("passwordError", "Password are different!");
        }

        model.addAttribute("user", null);

        if(passwordConfirmEmpty || bindingResult.hasErrors()){
            model.addAttribute("user", user);
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }

        if(!userService.addUser(user)){
            model.addAttribute("userError", "User exist");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute("message", "User successfully activated");
            model.addAttribute("messageType", "success");
        }
        else{
            model.addAttribute("message", "Activation code not found!");
            model.addAttribute("messageType", "danger");
        }
        return "login";
    }
}
