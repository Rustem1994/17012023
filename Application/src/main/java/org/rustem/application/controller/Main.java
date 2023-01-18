package org.rustem.application.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Slf4j
public class Main {
    @GetMapping("/")
    public String main(ModelMap model) {
        model.addAttribute("message", "Hello Spring MVC Framework!");
        log.info("model {}",model);
        return "hello";
    }
}
