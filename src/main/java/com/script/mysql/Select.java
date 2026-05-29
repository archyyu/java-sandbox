package com.script.mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Select extends SqlBase {

    private List<String> columns;

    // select id, name, price from product where id = 2 and price < 4;
    public Select(String sqlStr) throws Exception {

        sqlStr = sqlStr.toLowerCase().trim();

        if (sqlStr.startsWith(SELECT) == false) {
            throw new Exception("select is not included");
        }
 

        sqlStr = sqlStr.substring(SELECT.length(), sqlStr.length());
        String[] stringList = sqlStr.split("from");
        String[] columns = stringList[0].trim().split(",");
        String[] tableWheres = stringList[1].split("where");
        String table = tableWheres[0].trim();

        String[] wheres = null;
        if (tableWheres.length > 1) {
            wheres = tableWheres[1].split("and");
        }

        this.initSelectClause(columns, table, wheres);

    }

    @Override
    public String toString() {
        return "Select [columns=" + columns + ", whereClauses=" + whereClauses + ", getTable()=" + getTable() + "]";
    }

    public Select(String[] columns, String table, String[] wheres) {
        this.initSelectClause(columns, table, wheres);
    }

    private void initSelectClause(String[] columns, String table, String[] wheres) {
        this.columns = Arrays.stream(columns).map( item -> {return item.trim();}).collect(Collectors.toList());
        this.setTable(table);
        List<WhereClause> whereClauses = new ArrayList<>();
        if (wheres != null) {
            for(String where : wheres) {
                WhereClause whereClause = new WhereClause(where);
                whereClauses.add(whereClause);
            }
        }

        this.setWhereClauses(whereClauses);
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
