package com.script.mysql;

import java.util.List;
import java.util.stream.Collectors;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Table implements Serializable {

    private static final long serialVersionUID = 1L;

    // private
    
    private String tableName;

    private List<String> columns;

    private List<List<String>> data;

    public Table(String name, String[] columns) {
        this.tableName = name;
        this.columns = Arrays.stream(columns).map( item -> {return item.trim();}).collect(Collectors.toList());
        this.data = new ArrayList<>();
    }

    public Table(String name, List<String> columns) {
        this.tableName = name;
        this.columns = columns;
        this.data = new ArrayList<>();
    }

    public String getTableName() {
        return this.tableName;
    }

    public Table(Create create) {
        this(create.getTable(), create.getColumns());
    }

    public int delete(Delete delete) {
        if (delete == null) {
            return 0;
        }
        if (this.getTableName().equals(delete.getTable()) == false) {
            return 0;
        }

        List<List<String>> list = this.data.stream().filter( item -> this.filterByWhere(item, delete.getWhereClauses())).toList();
        this.data.removeAll(list);

        return list.size();
    }

    public int update(Update update) {

        if (update == null) {
            return 0;
        }
        if (update.getTable().equals(this.getTableName()) == false) {
            return 0;
        }

        if (this.columns.containsAll(update.getColumns()) == false) {
            return 0;
        }

        List<List<String>> selectData = this.data.stream().filter(item -> this.filterByWhere(item, update.getWhereClauses())).collect(Collectors.toList());

        List<Integer> targetIndexes = this.targetIndexes(update.getColumns());

        for(List<String> row : selectData) {
            for(int i=0;i<targetIndexes.size();i++) {
                row.set(targetIndexes.get(i), update.getValues().get(i));
            }
        }

        return selectData.size();

    }

    public int insert(Insert insert) {

        if (insert == null) {
            return 0;
        }
        if (insert.getTable().equals(this.tableName) == false) {
            return 0;
        }

        if (!this.columns.containsAll(insert.getColumns())) {
            return 0;
        }

        this.data.add(insert.getValues());

        return 1;

    }

    public void writeObject(ObjectOutputStream oos) throws Exception{
        oos.writeObject(this.tableName);
        oos.writeObject(this.columns);
        oos.writeObject(this.data);
    }

    public Table(ObjectInputStream oos) throws Exception{
        this.tableName = (String)oos.readObject();
        this.columns = (List<String>)oos.readObject();
        this.data = (List<List<String>>)oos.readObject();
    }

    public void printData() {

        this.data.forEach( row -> {

            row.forEach( item -> {
                System.err.print(item + " ");
            } );

            System.err.println();

        });

    }

    public int length() {
        return this.data.size();
    }

    public List<List<String>> select(Select selectClause) {

        if (selectClause == null) {
            return null;
        }
        if (this.tableName.equals(selectClause.getTable()) == false) {
            return null;
        }

        List<Integer> targetIndexes = this.targetIndexes(selectClause.getColumns());

        List<List<String>> list = this.data.stream().filter( item -> this.filterByWhere(item, selectClause.getWhereClauses()))
            .map( row -> {
                List<String> items = new ArrayList<>();
                targetIndexes.forEach( index -> {
                    items.add(row.get(index));
                });
                return items;
            }).collect(Collectors.toList()) ;

        return list;
    }

    // now, only support the and, will support or later
    public boolean filterByWhere(List<String> row, List<WhereClause> whereClauses) {

        if (whereClauses == null || whereClauses.isEmpty()) {
            return true;
        }

        //anyone is false, return false;
        for(WhereClause whereClause : whereClauses) {
            int index = this.columns.indexOf(whereClause.getColumn().trim());
            if (!whereClause.trueOrNot(row.get(index))) {
                return false;
            }
        };

        //if all are true, return true;
        return true;

    }


    public List<Integer> targetIndexes(List<String> columns) {

        List<Integer> result = new ArrayList<>();
        columns.forEach( column -> {
            result.add(this.columns.indexOf(column));
        });
        return result;

    }

}
