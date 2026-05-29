package com.script.mysql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        this.fileWriter = new FileWriter(this.databaseName, true);
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

    public void readFromLogs(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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

    public void saveSnapShot(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {

            oos.writeObject(this.databaseName);
            oos.writeObject(tableDict.size());
            for(Table table : tableDict.values()) {
                try {
                    table.writeObject(oos);
                } catch (Exception ex) {

                }
            }

        } catch (IOException e) {
            System.err.println("Error saving snapshot: " + e.getMessage());
        }
    }

    public void readFromSnapShot(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            
            this.databaseName = (String)ois.readObject();
            int tableSize = (Integer)ois.readObject();
            for(int i=0;i<tableSize;i++) {

                try {
                    Table table = new Table(ois);
                    this.tableDict.put(table.getTableName(), table);
                } catch (Exception ex) {

                }

            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading snapshot: " + e.getMessage());
        }
    }

}
