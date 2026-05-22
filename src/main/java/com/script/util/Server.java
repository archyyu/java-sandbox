package com.script.util;

public class Server {

    public Server(String name, String identity) {
        this.name = name;
        this.identity = identity;
    }
    
    private String name;
    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public long getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(long heartbeat) {
        this.heartbeat = heartbeat;
    }

    private String identity;

    private long heartbeat;

    public String getName() {
        return this.name;
    }

}
