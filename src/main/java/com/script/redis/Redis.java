package com.script.redis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Redis {

    private FileWriter fileWriter;
    private int logLines = 0;

    // this is a redis-like database, support a variety of datastructure
    // it has two files to support the persistence, one is WAL which is appendonly,
    // every new operation on the redis will be appended in the file.
    // the other file is snapshot, which will store the current state of the redis,
    // once the WAL is too long, it would save a snapshot and reset the WAL file.
    // so when it is to recover, it would firstly recover from the latest snapshot,
    // then replay the WAL files.
    public Redis(String filename) throws Exception {
        this.fileWriter = new FileWriter(filename, true);
    }

    public Redis() {

    }

    // SET key value
    // GET key
    private Map<String, String> keyValue = new HashMap<>();

    private void set(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        this.keyValue.put(key, value);
    }

    private String get(String key) {
        if (key == null) {
            return null;
        }
        return this.keyValue.get(key);
    }

    // LPUSH key value [value ...]
    // RPUSH key value [value ...]
    // lget
    private Map<String, List<String>> keyList = new HashMap<>();

    private void lpush(String key, String[] values) {
        List<String> list = this.keyList.computeIfAbsent(key, f -> new ArrayList<>());
        list.addAll(0, Arrays.stream(values).collect(Collectors.toList()));
    }

    private void del(String key) {

        this.keyValue.remove(key);
        this.keyList.remove(key);
        this.keySet.remove(key);
        this.keyMap.remove(key);

    }

    private void rpush(String key, String[] values) {
        List<String> list = this.keyList.computeIfAbsent(key, f -> new ArrayList<>());
        list.addAll(Arrays.stream(values).collect(Collectors.toList()));
    }

    private List<String> lrange(String key, int left, int right) {
        if (this.keyList.containsKey(key) == false) {
            return null;
        }
        return this.keyList.get(key).subList(left, right);
    }

    // SADD key member [member ...]
    // SREM key member [member ...]
    // SMEMBERS key
    private Map<String, Set<String>> keySet = new HashMap<>();

    private void sadd(String key, String[] values) {
        Set<String> set = this.keySet.computeIfAbsent(key, f -> new HashSet<>());
        set.addAll(Arrays.stream(values).toList());
    }

    private void srem(String key, String[] values) {
        Set<String> set = this.keySet.computeIfAbsent(key, f -> new HashSet<>());
        set.removeAll(Arrays.stream(values).toList());
    }

    private Set<String> smembers(String key) {
        Set<String> result = this.keySet.get(key);
        return result;
    }

    // HSET key field value
    // HGET key field
    private Map<String, Map<String, String>> keyMap = new HashMap<>();

    private void hset(String key, String field, String value) {
        Map<String, String> map = this.keyMap.computeIfAbsent(key, f -> new HashMap<>());
        map.put(field, value);
    }

    private String hget(String key, String field) {
        Map<String, String> map = this.keyMap.computeIfAbsent(key, f -> new HashMap<>());
        return map.get(field);
    }

    public Object exec(String cmdStr) {
        return this.exec(cmdStr, true);
    }

    public Object exec(String cmdStr, boolean append) {

        try {
            Object result = 1;
            String[] cmdParams = cmdStr.split(" ");
            String cmd = cmdParams[0];
            if (cmd.equals("SET")) {
                this.set(cmdParams[1], cmdParams[2]);
            } else if (cmd.equals("GET")) {
                result = this.get(cmdParams[1]);
            } else if (cmd.equals("LPUSH")) {
                this.lpush(cmdParams[1], Arrays.copyOfRange(cmdParams, 2, cmdParams.length));
            } else if (cmd.equals("RPUSH")) {
                this.rpush(cmdParams[1], Arrays.copyOfRange(cmdParams, 2, cmdParams.length));
            } else if (cmd.equals("LRANGE")) {
                result = this.lrange(cmdParams[1], Integer.parseInt(cmdParams[2]), Integer.parseInt(cmdParams[3]));
            } else if (cmd.equals("SADD")) {
                this.sadd(cmdParams[1], Arrays.copyOfRange(cmdParams, 2, cmdParams.length));
            } else if (cmd.equals("SREM")) {
                this.srem(cmdParams[1], Arrays.copyOfRange(cmdParams, 2, cmdParams.length));
            } else if (cmd.equals("SMEMBERS")) {
                result = this.smembers(cmdParams[1]);
            } else if (cmd.equals("HSET")) {
                this.hset(cmdParams[1], cmdParams[2], cmdParams[3]);
            } else if (cmd.equals("HGET")) {
                result = hget(cmdParams[1], cmdParams[2]);
            } else if (cmd.equals("DEL")) {
                del(cmdParams[1]);
            }

            if (append) {
                this.appendLog(cmdStr);
            }

            return result;
        } catch (Exception ex) {
            return 0;
        }
    }

    private void appendLog(String cmdStr) throws IOException {
        if (this.fileWriter == null) {
            return;
        }
        this.fileWriter.append(cmdStr + "\n");
        this.fileWriter.flush();
    }

    public void readFromLogs(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Read each line until the end of the file
            while ((line = br.readLine()) != null) {
                this.exec(line, false);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public void saveSnapShot(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this.keyValue);
            oos.writeObject(this.keyList);
            oos.writeObject(this.keySet);
            oos.writeObject(this.keyMap);
        } catch (IOException e) {
            System.err.println("Error saving snapshot: " + e.getMessage());
        }
    }

    public void readFromSnapShot(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            this.keyValue = (Map<String, String>) ois.readObject();
            this.keyList = (Map<String, List<String>>) ois.readObject();
            this.keySet = (Map<String, Set<String>>) ois.readObject();
            this.keyMap = (Map<String, Map<String, String>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading snapshot: " + e.getMessage());
        }
    }

}
