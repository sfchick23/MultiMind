package ru.sfchick.MultiMind.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sfchick.MultiMind.services.ApiService;

import java.util.List;

@Controller
public class PromptController {

    private final ApiService apiService;

    @Autowired
    public PromptController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/generate/image")
    public String generateImages(@RequestParam("prompt") String prompt, Model model) {
        List<String> images = apiService.generateImages(prompt);
        System.out.println(images);
        model.addAttribute("images", images);
        return "result";
    }

    @PostMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt, Model model) {
        List<String> message = apiService.chatBotPrompt(prompt);
        System.out.println(message);
        model.addAttribute("message", message);
        return "chat";
    }
}
