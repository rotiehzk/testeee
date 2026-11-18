package com.starterkit.springboot.Dashboard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/api/utilizadores")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    

}
