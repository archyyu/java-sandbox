package com.script.mysql;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import io.micrometer.common.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Table implements Serializable {

    private static final long serialVersionUID = 1L;

    // private
    
    private String tableName;

    private List<String> columns;

    private List<List<String>> data;
    private ReentrantReadWriteLock dataLock = new ReentrantReadWriteLock();

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
        this.dataLock.writeLock().lock();
        try {
            this.data.removeAll(list);
        } finally {
            this.dataLock.writeLock().unlock();
        }

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

        this.dataLock.writeLock().lock();
        try {
            for(List<String> row : selectData) {
                for(int i=0;i<targetIndexes.size();i++) {
                    row.set(targetIndexes.get(i), update.getValues().get(i));
                }
            }
        } finally {
            this.dataLock.writeLock().unlock();
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

        this.dataLock.writeLock().lock();
        try {
            this.data.add(insert.getValues());
        } finally {
            this.dataLock.writeLock().unlock();
        }


        return insert.getValues().size();

    }

    public String compactToText() {
        Map<String, String> map = new HashMap<>();

        map.put("tableName", tableName);
        map.put("columns", JSON.toJSONString(this.columns));
        map.put("data", JSON.toJSONString(this.data));

        return JSON.toJSONString(map);
    }

    public Table(String text) {
        if (StringUtils.isEmpty(text)) {
            return ;
        }

        Map<String, String> data = JSON.parseObject(text, new TypeReference<Map<String, String>>() {});

        this.tableName = data.get("tableName");
        this.columns = JSON.parseArray(data.get("columns"), String.class);
        this.data = JSON.parseObject(data.get("data"), new TypeReference<List<List<String>>>() {});
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

        this.dataLock.readLock().lock();
        try {
            List<List<String>> list = this.data.stream().filter( item -> this.filterByWhere(item, selectClause.getWhereClauses()))
                .map( row -> {
                    List<String> items = new ArrayList<>();
                    targetIndexes.forEach( index -> {
                        items.add(row.get(index));
                    });
                    return items;
                }).collect(Collectors.toList()) ;

            return list;
        } finally {
            this.dataLock.readLock().unlock();
        }
        
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
