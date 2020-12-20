package ru.alwertus.serveradmin.controller.rest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alwertus.serveradmin.cfg.ApplicationsCfgLoader;
import ru.alwertus.serveradmin.serverpc.IServerProcess;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/app/action")
public class AppActionsController {

    private final ApplicationsCfgLoader cfg;

    @Autowired
    public AppActionsController(ApplicationsCfgLoader cfg) {
        this.cfg = cfg;
    }

    @PostMapping({"start","stop"})
    public String startRequest(@RequestBody String requestBody) {
//        System.out.println(requestBody);
        JSONObject rq = new JSONObject(requestBody);

        String program = (String) rq.get("program");
        String action = (String) rq.get("action");

        System.out.println(program + " - " + action);

        IServerProcess proc =
                (Objects.requireNonNull(cfg.getApplicationsList().stream()
                        .filter(serverProcessRest -> program.equals(serverProcessRest.getTitle()))
                        .findAny())
                    .get().sp);

        JSONObject rs = new JSONObject();

        String result = "OK";

        try {
            if (action.equals("Start")) proc.start();
            if (action.equals("Stop")) proc.stop();
        } catch (Exception e) {
            result = "ERROR";
            rs.put("error", e.getMessage());
        }

        rs.put("result", result);
        return rs.toString();
    }



}