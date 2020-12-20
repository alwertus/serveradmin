package ru.alwertus.serveradmin.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alwertus.serveradmin.cfg.ApplicationsCfgLoader;
import ru.alwertus.serveradmin.serverpc.ServerProcessRest;

import java.util.List;

@RestController
public class AppStatusController {

    final
    ApplicationsCfgLoader cfg;

    @Autowired
    public AppStatusController(ApplicationsCfgLoader cfg) {
        this.cfg = cfg;

    }


    @GetMapping("/api/v1/app/status")
    public List<ServerProcessRest> getAllStatuses() {

        cfg.getApplicationsList().forEach(System.out::println);

        return cfg.getApplicationsList();
    }
}
