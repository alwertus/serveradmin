package ru.alwertus.serveradmin.deleteme;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/* works too long
* bad structure */
public class ServerProcess {

    private File processDir;
    private String errorText = "";
    private String findString = "";


    private ServerProcess() { }

    public static ServerProcess getServerProcessFromPID(int pid) {
        ServerProcess sp = new ServerProcess();
        sp.processDir = new File("/proc/" + pid);

        return sp;
    }

    private static List<File> findProcessFromCmdPart(String cmdPart) {
        FileFilter ff = (pathname) -> pathname.isDirectory() && pathname.getName().matches("\\d+");
        File dir = new File("/proc/");
        List<File> resultList = new ArrayList<>();
        String cmdline = "";

        for (File f : Objects.requireNonNull(dir.listFiles(ff))) {
            try {
                cmdline = "";
                Scanner sc = new Scanner((new File(f.getPath() + "/" + "cmdline")));
                if (sc.hasNext()) cmdline = sc.nextLine();
            } catch (FileNotFoundException ignored) {

            }
            if (cmdline.contains(cmdPart)) {
                resultList.add(f);
//                System.out.println((f.isDirectory() ? "DIR  " : "FILE ") + f.getName() + " [ " + (cmdline.contains("mc") ? "TRUE" : "FALSE") + " ] " + cmdline);
            }
        }
        return resultList;
    }

    public static ServerProcess getServerProcessFromCmcPart(String cmdPart) {
        ServerProcess sp = new ServerProcess();
        sp.findString = cmdPart;

        List<File> foundProcesses = findProcessFromCmdPart(cmdPart);

        if (foundProcesses.size() > 1)
            sp.errorText = "Too many matching lines found (" + foundProcesses.size() + "). Try to refine your search";

        if (foundProcesses.size() == 0)
            sp.errorText = "Find no elements";

        sp.processDir = sp.errorText.length() == 0
                ?  foundProcesses.get(0)
                : null;

        return sp;
    }

    public String getCmdLine() {
        String cmdline = "";
        if (processDir != null)
            cmdline = readOneLineFromFile(new File(processDir.getPath() + "/cmdline"));

//        if (cmdline.length() == 0)
        return cmdline;
    }

    public int getPID() {
        try {
            return Integer.parseInt(processDir.getName());
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean isExists() {
        return getCmdLine().length() > 0;
    }

    public String getStatus() {
        if (processDir == null) return errorText;

        String[] stats = readOneLineFromFile(new File(processDir.getPath() + "/stat")).split(" ");
        return stats.length > 3 ? stats[2] : "unknown";
    }

    private static String readOneLineFromFile(File file) {
        try {
            Scanner sc = new Scanner(file);
            return sc.hasNext()
                    ? sc.nextLine()
                    : "";
        } catch (FileNotFoundException e) {
            return "";
        }
    }

    @Override
    public String toString() {
        return "PID=" + getPID() + (isExists() ? " (alive)" : " (die)") + " | Status=" + getStatus() + " | CMD=" + getCmdLine();
    }
}