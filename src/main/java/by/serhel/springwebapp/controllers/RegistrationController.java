package by.serhel.springwebapp.controllers;

import by.serhel.springwebapp.entities.User;
import by.serhel.springwebapp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class RegistrationController {
    private static Logger logger = LogManager.getLogger(RegistrationController.class.getName());

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration()
    {
        logger.info("return 'registration'");
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam("password1") String passwordConfirm,
                          @Valid User user,
                          BindingResult bindingResult,
                          Model model,
                          HttpServletResponse httpResponse
    ){
        logger.info("start 'addUser'");

        boolean isNotValid = StringUtils.isEmpty(passwordConfirm);
        if(isNotValid){
            model.addAttribute("password1Error", "message.correct.password1");
        }
        isNotValid = isNotValid || user.getPassword() != null && !user.getPassword().equals(passwordConfirm);
        if(isNotValid){
            model.addAttribute("passwordError", "message.confirm.password");
        }

        model.addAttribute("user", null);

        if(isNotValid || bindingResult.hasErrors()){
            model.addAttribute("user", user);
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }

        if(!userService.addUser(user)){
            model.addAttribute("userError", "User exist");
            return "registration";
        }
        model.addAttribute("messageSuccess", "Registration is successfully");

        logger.info("finish 'addUser' and redirect to /login");

        return "login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        logger.info("start 'activate'");

        String message = "Activation code not found!";
        String messageType = "danger";

        if(userService.activateUser(code)){
            message= "User successfully activated";
            messageType = "success";
        }
        model.addAttribute("message", message);
        model.addAttribute("messageType", messageType);

        logger.info("finish 'activate'");
        return "login";
    }
}
