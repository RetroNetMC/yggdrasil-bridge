package hu.retronet.mc.portal.controller;

import com.czompi.mcservicessdk.model.MinecraftAccount;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/")
public class BaseController {

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        MinecraftAccount profile = (MinecraftAccount) session.getAttribute("profile");
        if (profile == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("profile", profile);
        return "index";
    }
}
