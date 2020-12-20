package ru.alwertus.serveradmin.cfg;

import org.springframework.stereotype.Component;
import ru.alwertus.serveradmin.serverpc.ServerProcessRest;
import ru.alwertus.serveradmin.serverpc.TerminalServerProcess;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class ApplicationsCfgLoader {
    public static final String CFG_FILENAME = "programs.cfg";
    public static final String P_APP = "app";
    private List<ServerProcessRest> applicationsList;

    public ApplicationsCfgLoader() {
        reloadFile();
    }

    public void reloadFile() {
        applicationsList = new ArrayList<>();
        try ( InputStream in = new FileInputStream(CFG_FILENAME)){
            Properties props = new Properties();
            props.load(in);

            int appCount = Integer.parseInt(props.getProperty("count", "0"));

            for (int i = 1; i <= appCount; i++) {
                String currentApp = P_APP + i + ".";
                applicationsList.add(
                        new ServerProcessRest(
                                props.getProperty(currentApp + "title"),
                                new TerminalServerProcess(
                                        props.getProperty(currentApp + "find"),
                                        props.getProperty(currentApp + "start"),
                                        props.getProperty(currentApp + "stop")
                                )));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ServerProcessRest> getApplicationsList() {
        return applicationsList;
    }
}