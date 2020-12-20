package ru.alwertus.serveradmin.controller.rest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alwertus.serveradmin.cfg.ApplicationsCfgLoader;

@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {

    ApplicationsCfgLoader cfg;

    @Autowired
    public ConfigController(ApplicationsCfgLoader cfg) {
        this.cfg = cfg;
    }

    @PostMapping("reload")
    public String reloadConfig(@RequestBody String requestBody) {
        System.out.println("reload config");

        cfg.reloadFile();

        JSONObject rs = new JSONObject();
        rs.put("result", "OK");
        return rs.toString();
    }

}