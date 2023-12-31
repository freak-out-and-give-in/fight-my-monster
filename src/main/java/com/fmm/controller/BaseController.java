package com.fmm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
public class BaseController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> userAutomaticallyRedirectedToLandingPage() {
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                .header("Location", "/fmm/landing-page").build();
    }

    @GetMapping("/fmm/landing-page")
    public ModelAndView showLandingPage() {

        return new ModelAndView("/base/landing-page");
    }

    @GetMapping("/fmm/registration")
    public ModelAndView showRegistrationPage() {

        return new ModelAndView("/base/registration");
    }

    @GetMapping("/fmm/login")
    public ModelAndView showLoginPage() {

        return new ModelAndView("/base/login");
    }

}

