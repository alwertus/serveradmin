package ru.alwertus.serveradmin.serverpc;

public interface IServerProcess {

    boolean isExists();

    String getStatus();

    int getPID();

    String getCmdline();

    void start();

    void stop();
}
