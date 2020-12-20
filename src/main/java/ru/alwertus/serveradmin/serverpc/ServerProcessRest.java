package ru.alwertus.serveradmin.serverpc;

import lombok.Getter;

public class ServerProcessRest {
    @Getter
    private String title;

    public IServerProcess sp;

    public ServerProcessRest(String title, IServerProcess sp) {
        this.title = title;
        this.sp = sp;
    }

    public String getStatus() {
        return sp.getStatus();
    }

    public boolean isExists() {
        return sp.isExists();
    }

    @Override
    public String toString() {
        return sp.toString();
    }
}
