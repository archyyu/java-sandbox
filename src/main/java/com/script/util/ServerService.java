package com.script.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerService {
    
    private final int evictTime = 5;
    
    private Map<String, Map<String,Server>> serverMap = new ConcurrentHashMap<>();

    public ServerService() {

    }

    public void startEvictTask() {

        

    }

    public void registerServer(Server server) {
        Map<String, Server> map = serverMap.computeIfAbsent(server.getIdentity(), k -> new ConcurrentHashMap<>());
        map.put(server.getName(), server);
    }

    public boolean unregisterServer(Server server) {
        Map<String, Server> map = serverMap.get(server.getIdentity());
        if (map == null) {
            return false;
        }

        return map.remove(server.getName(), server);
    }

    public void serverHeartBeat(Server server) {
        Map<String, Server> map = this.serverMap.get(server.getIdentity());
        if (map == null) {
            return;
        }

        Server target = map.get(server.getName());
        if (target == null) {
            return;
        }
        target.setHeartbeat(System.currentTimeMillis());
    }

    public void sync() {

        try {

            this.serverMap.forEach((key, value) -> {
                value.entrySet().removeIf( e -> {
                    boolean idle = System.currentTimeMillis() - e.getValue().getHeartbeat() > this.evictTime * 1000;
                    return idle;
                });
            });;

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

    }

}
