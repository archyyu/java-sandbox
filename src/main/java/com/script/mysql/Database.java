package com.script.mysql;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {
    
    private FileWriter fileWriter;

    private String databaseName;

    private Map<String, Table> tableDict = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Database(String name) throws Exception{
        this.databaseName = name;
        this.fileWriter = new FileWriter(this.databaseName + "binlog", true);
        this.readFromLogs();
    }

    public Object exec(String query) throws Exception{
        return this.exec(query, true);
    }

    private Object exec(String query, boolean append) throws Exception {
        try {
            Object result = null;
            if (query.toLowerCase().startsWith(SqlBase.CREATE)) {
                Create create = new Create(query);
                result = this.createTable(create);
            } else if (query.startsWith(SqlBase.INSERT)) {
                Insert insert = new Insert(query);
                System.err.println(insert);
                result = this.insertTable(insert);
            } else if (query.startsWith(SqlBase.SELECT)) {
                Select select = new Select(query);
                this.logger.info("select:" + select.toString());
                result = this.selectTable(select);
                append = false;
            } else if (query.startsWith(SqlBase.UPDATE)) {
                Update update = new Update(query);
                result = this.updateTable(update);
            } else if (query.startsWith(SqlBase.DELETE)) {
                Delete dalete = new Delete(query);
                result = this.deteleTable(dalete);
            }
            else {
                return null;
            }

            if (append) {
                this.appendLog(query);
            }

            return result;
        } catch (Exception ex) {
            logger.info("exec", ex);
            return null;
        }
    }



    public String getDatabaseName() {
        return this.databaseName;
    }

    public Table getTable(String tableName) {
        return this.tableDict.get(tableName);
    }

    public boolean createTable(Create create) {

        if (create == null) {
            return false;
        }
        if (tableDict.containsKey(create.getTable())) {
            return false;
        }

        Table table = new Table(create);
        this.tableDict.put(create.getTable(), table);
        return true;

    }

    public int updateTable(Update update) {

        if (update == null) {
            return 0;
        }
        Table table = this.getTable(update.getTable());
        if (table == null) {
            return 0;
        }
        return table.update(update);
    
    }

    public int deteleTable(Delete delete) {
        if (delete == null) {
            return 0;
        }
        Table table = this.getTable(delete.getTable());
        if (table == null) {
            return 0;
        }
        return table.delete(delete);
    }

    public int insertTable(Insert insert) {

        if (insert == null) {
            return 0;
        }
        Table table = this.getTable(insert.getTable());
        if (table == null) {
            return 0;
        }
        return table.insert(insert);

    }

    public List<List<String>> selectTable(Select select) {
        if (select == null) {
            return new ArrayList<>();
        }
        Table table = this.getTable(select.getTable());
        if (table == null) {
            return new ArrayList<>();
        }

        return table.select(select);
    }

    private void appendLog(String cmdStr) throws IOException {
        if (this.fileWriter == null) {
            return ;
        }
        this.fileWriter.append(cmdStr + "\n");
        this.fileWriter.flush();
    }

    public void readFromLogs() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.databaseName + "binlog"))) {
            String line;
            // Read each line until the end of the file
            while ((line = br.readLine()) != null) {
                try {
                    this.exec(line, false);
                } catch (Exception ex) {
                     System.err.println(line + ", Error exec line: " + ex.getMessage());
                }

            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void flushObjectToFile(String subName, String obj) throws IOException {

        FileWriter fileWriter = new FileWriter(this.databaseName + "-" + subName, false);
        fileWriter.append(obj);
        fileWriter.flush();
        fileWriter.close();
        
    }

    private String readObjectFromFile(String subName) throws IOException {
        
        String content = Files.readString(Paths.get(this.databaseName + "-" + subName));
        return content;
        
    }

    public void saveSnapShot() {
        try {

            Map<String, String> dbBase = new HashMap<>();
            dbBase.put("databaseName", this.databaseName);
            dbBase.put("tables", JSON.toJSONString(this.tableDict.keySet()));

            this.flushObjectToFile("base", JSON.toJSONString(dbBase));

            for(Table table : this.tableDict.values()) {
                this.flushObjectToFile(table.getTableName(), table.compactToText());                
            }

            
        } catch (IOException e) {
            System.err.println("Error saving snapshot: " + e.getMessage());
        }
    }

    public void readFromSnapShot() {
        try {
            
            String dbBase = this.readObjectFromFile("base");
            Map<String, String> baseInfo = JSON.parseObject(dbBase, new TypeReference<Map<String, String>>() {});

            this.databaseName = baseInfo.get("databasename");
            Set<String> tableNameList = JSON.parseArray(baseInfo.get("tables"), String.class).stream().collect(Collectors.toSet());;

            tableNameList.forEach( tableName -> {
                
                try {
                    String tableData = this.readObjectFromFile(tableName);
                    Table table = new Table(tableData);
                    this.tableDict.put(tableName, table);
                } catch (IOException e) {

                    e.printStackTrace();
                }
                
            });

        } catch (IOException e) {
            System.err.println("Error reading snapshot: " + e.getMessage());
        }
    }

}
