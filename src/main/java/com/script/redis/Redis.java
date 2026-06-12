package com.script.redis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Redis {

    private FileWriter fileWriter;
    private final int logCapacity = 10000;
    private String logsFileName;
    private String snapshotFileName;
    private AtomicInteger linesCount = new AtomicInteger(0);

    // this is a redis-like database, support a variety of datastructure
    // it has two files to support the persistence, one is WAL which is appendonly,
    // every new operation on the redis will be appended in the file.
    // the other file is snapshot, which will store the current state of the redis,
    // once the WAL is too long, it would save a snapshot and reset the WAL file.
    // so when it is to recover, it would firstly recover from the latest snapshot,
    // then replay the WAL files.
    public Redis(String filename) throws IOException {

        this.fileWriter = new FileWriter(filename, true);
        this.logsFileName = filename;
        this.snapshotFileName = filename;
        // this.readFromLogs(filename);
    }

    // SET key value
    // GET key
    private Map<String, String> keyValue = new ConcurrentHashMap<>();

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
        String result = this.keyValue.get(key);
        return result;
    }

    // LPUSH key value [value ...]
    // RPUSH key value [value ...]
    // lget
    private Map<String, List<String>> keyList = new ConcurrentHashMap<>();

    private void lpush(String key, String[] values) {
        List<String> list = this.keyList.computeIfAbsent(key, f -> Collections.synchronizedList(new ArrayList<>()));
        list.addAll(0, Arrays.stream(values).collect(Collectors.toList()));
    }

    private void del(String key) {

        this.keyValue.remove(key);
        this.keyList.remove(key);
        this.keySet.remove(key);
        this.keyMap.remove(key);

    }

    private void rpush(String key, String[] values) {
        List<String> list = this.keyList.computeIfAbsent(key, f -> Collections.synchronizedList(new ArrayList<>()));
        list.addAll(Arrays.stream(values).collect(Collectors.toList()));
    }

    private List<String> lrange(String key, int left, int right) {
        if (this.keyList.containsKey(key) == false) {
            return null;
        }

        List<String> values = this.keyList.get(key);

        if (left < 0) {
            left = 0;
        }

        if (right > values.size()) {
            right = values.size();
        }

        List<String> result = this.keyList.get(key).subList(left, right);
        return result;
    }

    // SADD key member [member ...]
    // SREM key member [member ...]
    // SMEMBERS key
    private Map<String, Set<String>> keySet = new ConcurrentHashMap<>();

    private void sadd(String key, String[] values) {
        
        Set<String> set = this.keySet.computeIfAbsent(key, f -> new ConcurrentSkipListSet<>());
        set.addAll(Arrays.stream(values).toList());
    }

    private void srem(String key, String[] values) {
        Set<String> set = this.keySet.computeIfAbsent(key, f -> new ConcurrentSkipListSet<>());
        set.removeAll(Arrays.stream(values).toList());
    }

    private Set<String> smembers(String key) {
        Set<String> result = Collections.unmodifiableSet(this.keySet.get(key));
        System.err.println("key: " + key);
        System.err.println("value:" + result);
        return result;
    }

    // HSET key field value
    // HGET key field
    private Map<String, Map<String, String>> keyMap = new ConcurrentHashMap<>();

    private void hset(String key, String field, String value) {
        Map<String, String> map = this.keyMap.computeIfAbsent(key, f -> new ConcurrentHashMap<>());
        map.put(field, value);
    }

    private String hget(String key, String field) {
        Map<String, String> map = this.keyMap.getOrDefault(key, Collections.emptyMap());
        return map.get(field);
    }

    public Object exec(String cmdStr) {
        return this.exec(cmdStr, true);
    }

    public Object exec(String cmdStr, boolean append) {

        try {
            Object result = '1';
            String[] cmdParams = cmdStr.split(" ");
            String cmd = cmdParams[0];
            if (cmd.equals("SET")) {
                this.set(cmdParams[1], cmdParams[2]);
            } else if (cmd.equals("GET")) {
                result = this.get(cmdParams[1]);
                append = false;
            } else if (cmd.equals("LPUSH")) {
                this.lpush(cmdParams[1], Arrays.copyOfRange(cmdParams, 2, cmdParams.length));
            } else if (cmd.equals("RPUSH")) {
                this.rpush(cmdParams[1], Arrays.copyOfRange(cmdParams, 2, cmdParams.length));
            } else if (cmd.equals("LRANGE")) {
                result = this.lrange(cmdParams[1], Integer.parseInt(cmdParams[2]), Integer.parseInt(cmdParams[3]));
                append = false;
            } else if (cmd.equals("SADD")) {
                this.sadd(cmdParams[1], Arrays.copyOfRange(cmdParams, 2, cmdParams.length));
            } else if (cmd.equals("SREM")) {
                this.srem(cmdParams[1], Arrays.copyOfRange(cmdParams, 2, cmdParams.length));
            } else if (cmd.equals("SMEMBERS")) {
                result = this.smembers(cmdParams[1]);
                append = false;
            } else if (cmd.equals("HSET")) {
                this.hset(cmdParams[1], cmdParams[2], cmdParams[3]);
            } else if (cmd.equals("HGET")) {
                result = hget(cmdParams[1], cmdParams[2]);
                append = false;
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
        synchronized(this.fileWriter) {
            this.fileWriter.append(cmdStr + "\n");
            this.fileWriter.flush();
            this.linesCount.addAndGet(1);
            if (this.linesCount.get() >= this.logCapacity) {
                this.saveSnapShot();
                this.resetLogs();
                this.linesCount.set(0);
            }
        }
    }

    // to reset the bin logs file
    // I need to close the current fileWriter handler, open a new one by append=false, and reopen again.
    private void resetLogs() throws IOException {
        this.fileWriter.close();
        (new FileWriter(this.logsFileName, false)).close();
        this.fileWriter = new FileWriter(this.logsFileName, true);
    }

    public void readFromLogs() {
    
        try (BufferedReader br = new BufferedReader(new FileReader(this.logsFileName))) {
            String line;
            // Read each line until the end of the file
            while ((line = br.readLine()) != null) {
                this.exec(line, false);
                this.linesCount.addAndGet(1);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void flushObjectToFile(String subName, String obj) {
        try {
            FileWriter fileWriter = new FileWriter(this.snapshotFileName + "-" + subName, false);
            fileWriter.append(obj);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ex) {
            System.err.println("failed when saveing keyValye");
            ex.printStackTrace(System.err);
        }
    }

    private String readObjectFromFile(String subName) {
        try {
            String content = Files.readString(Paths.get(this.snapshotFileName + "-" + subName));
            return content;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    public void saveSnapShot() {

        {
            this.flushObjectToFile("keyValue", JSON.toJSONString(this.keyValue));
        }

        {
            this.flushObjectToFile("keyList", JSON.toJSONString(this.keyList));
        }

        {
            this.flushObjectToFile("keySet", JSON.toJSONString(this.keySet));
        }

        {
            this.flushObjectToFile("keyMap", JSON.toJSONString(this.keyMap));
        }

    }

    public void readFromSnapShot() {
        
        {
            String content = this.readObjectFromFile("keyValue");
            if (content != null) {
                this.keyValue = JSON.parseObject(content, new TypeReference<Map<String,String>>(){});
            }

        }

        {
            String content = this.readObjectFromFile("keyList");
            if (content != null) {
                this.keyList = JSON.parseObject(content, new TypeReference<Map<String,List<String>>>() {});
            }
        }

        {
            String content = this.readObjectFromFile("keySet");
            if (content != null) {
                this.keySet = JSON.parseObject(content, new TypeReference<Map<String,Set<String>>>(){});
            }
        }

        {
            String content = this.readObjectFromFile("keyMap");
            if (content != null) {
                this.keyMap = JSON.parseObject(content, new TypeReference<Map<String,Map<String,String>>>(){});
            }
        }

    }

}
