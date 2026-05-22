package com.script.mysql;

import java.util.stream.IntStream;
import java.util.List;

//SOURCES SqlBase.java
//SOURCES SqlComparator.java
//SOURCES WhereClause.java
//SOURCES Select.java
//SOURCES Table.java
//SOURCES Insert.java
//SOURCES Update.java
//SOURCES Database.java
//SOURCES Create.java
//SOURCES Delete.java

public class SqlTest {
    // this is a simple momery mysql-like database, I use this for practise
    // right now, it only support select and insert, will also support update
    // jbang is really a great tool to do small project.
    // for the persistence, maybe we could implement a simple one
    // one for snapshot, one for WLF
    
    // for the index part, it is quite interesting
    // if it is just for hash Index, it is simple, I just need a hashMap to index
    // 

    // I know this toy project is not perfect, and I am trying to improve it
    // 1: try to use jbang, it is new for me, but i find it great
    // 2: try to utilize new features as much as possible
    // 3: try to for parse the sql query, I will use the tree algorithm to replace the current one
    // 4: it is really a good pratise to study mysql and other like database, will do the persistence
    // 5: also will recall the book of clean code, and apply the principles on this small project
    
    public static void main(String[] args) {

        try {

            // testSelect();
            // testInsert();
            // testUpdate();
            // testTable();
            testDatabase();
            // testCreate();
            // testRecoverFromLogs();

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static void testCreate() throws Exception {

        String create = "create table product (id, name, price)";
        Create create1 = new Create(create);

        System.err.println(create1);

    }

    public static void testRecoverFromLogs() throws Exception {
        Database database = new Database("shop");
        database.readFromLogs("shop");

        String select = "select id, name, price from product where id<=2";
        Object result = database.exec(select);

        System.err.println(result);

    }

    public static void testDatabase() throws Exception{
        
        Database database = new Database("shop");
        String create = "create table product (id, name, price)";

        database.exec(create);

        IntStream.range(0, 10).forEach( i -> {
            String query = "insert into product (id, name, price) values (" + i + ", Alice, " + i + "0000)";
            try {
                database.exec(query);
            } catch (Exception ex) {

            }
        });

        String select = "select id, name, price from product where id<=2";
        Object result = database.exec(select);

        System.err.println(result);

    }

    public static void testSelect() throws Exception {

        String query = "select id, name, price from product where id = 2 and price < 3";

        Select select = new Select(query);

        System.err.println(select.getTable());
        select.getColumns().forEach( item -> {
            System.err.println(item.trim());
        });
        
        select.getWhereClauses().forEach( item -> {
            System.err.println(item);
        });
    }

    public static void testInsert() throws Exception {

 
        String query = "INSERT INTO employees (id, name, price) VALUES (1, Alice, 50000)";
        Insert insert = new Insert(query);

    }

    public static void testUpdate() throws Exception {

        String query = "UPDATE table_name SET column1 = new_value1, column2 = new_value2 WHERE id = 3";
        Update update = new Update(query);

    }

    public static void testTable() throws Exception {
        
        Table table = new Table("product", new String[] {"id", "name", "price"});

        IntStream.range(0, 10).forEach( i -> {
            String query = "INSERT INTO product (id, name, price) VALUES (" + i + ", Alice, " + i + "0000)";
            try {
                Insert insert = new Insert(query);
                table.insert(insert);
            } catch (Exception ex) {

            }
        });
  
        String query = "select id, name, price from product where id<=2";

        Select select = new Select(query);

        List<List<String>> data = table.select(select);
        System.err.println(data);

        String updateQuery = "update product set price=100 where id<=2";
        Update update = new Update(updateQuery);
        table.update(update);
        
        System.err.println("after update");
        
        data = table.select(select);
        System.err.println(data);


    }

}
