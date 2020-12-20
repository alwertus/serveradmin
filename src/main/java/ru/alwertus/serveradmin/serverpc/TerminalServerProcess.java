package ru.alwertus.serveradmin.serverpc;

import java.util.List;
import java.util.Optional;

/**
 * Information site:   https://losst.ru/kak-uznat-pid-protsessa-v-linux
 */
public class TerminalServerProcess extends LinuxTerminal implements IServerProcess {
    private final String findString;
    private final String startScript;
    private final String stopScript;

    public TerminalServerProcess(String cmd, String startScript, String stopScript) {
        this.findString = cmd;
        this.startScript = startScript;
        this.stopScript = stopScript;
    }

    @Override
    public boolean isExists() {
        return getCmdline().length() > 0;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    /* return:
     *      0 -> not found
     *      -1 -> error
     *      -2 -> too many results
     *      or process PID
     */
    public int getPID() {
        List<String> results = runCommandReadResults("pgrep", "-f", findString);

        if (results.size() == 0) return 0;

        if (results.size() > 1) return -2;

        int resultInt;
        try {
             resultInt = Integer.parseInt(results.get(0));
        } catch (NumberFormatException e) {
            resultInt = -1;
        }

        return resultInt;
    }

    @Override
    public String getCmdline() {
        int pid = getPID();
        if (pid < 1) return "";

        List<String> results = runCommandReadResults("/bin/sh", "-c", "ps -q " + pid + " -o command");

        return results.size() > 1
                ? results.get(1)
                : (results.size() == 1
                    ? results.get(0)
                    : "");
    }

    @Override
    public void start() {
        boolean addSH = startScript.contains("zzzSHzzz");

        String startComand = replaceAllOccurrences(startScript, 0);
//                startScript.replaceAll("zzzPIDzzz", String.valueOf(getPID()));
//        startComand = startComand.replaceAll("zzzSHzzz", "");

        System.out.println("START: << " + startComand + " >>");

        if (startScript.equals(""))
            throw new NullPointerException("Script 'START' not defined");
        if (!isExists())
            if (addSH)
                runCommandSHReadResults(startComand);
            else
                runCommandReadResults(startComand);
    }

    @Override
    public void stop() {
        int pid = getPID();
        if (pid < 1) throw new NullPointerException("PID " + pid + " not exists");

        if (stopScript.equals("zzzKILLzzz")) {
            Optional<ProcessHandle> p = ProcessHandle.of(pid);
            p.ifPresent(ProcessHandle::destroy);
            System.out.println("STOP application PID=" + pid + " programmatically");
            return;
        }


        String stopCommand = replaceAllOccurrences(stopScript, pid);
        System.out.println("STOP: " + stopCommand);

        if (stopScript.equals(""))
            throw new NullPointerException("Script 'STOP' not defined");
        if (isExists())
            runCommandReadResults(stopCommand);
    }

    private String replaceAllOccurrences(String rawString, int pid) {
        return rawString
                .replaceAll("zzzPIDzzz", String.valueOf(pid))
                .replaceAll("zzzSHzzz", "");
    }

    @Override
    public String toString() {
        return "PID=" + getPID() + (isExists() ? " (alive)" : " (die)") + " | Status=" + getStatus() + " | CMD=" + getCmdline();
    }
}
