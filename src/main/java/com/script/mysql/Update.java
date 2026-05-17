package src.main.java.com.script.mysql;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Update extends SqlBase{

    private List<String> columns;
    private List<String> values;

    public Update(String[] columns, String table, String[] values, String[] wheres) {
        this.initUpdateClause(columns, table, values, wheres);
    }

    private void initUpdateClause(String[] columns, String table, String[] values, String[] wheres) {
        this.columns = Arrays.stream(columns).map(item -> { return item.trim(); }).collect(Collectors.toList());
        this.values = Arrays.stream(values).map(item -> { return item.trim(); }).collect(Collectors.toList());
        this.setTable(table);

        if (wheres != null) {
            this.setWhereClauses(Arrays.stream(wheres).map( item -> { return new WhereClause(item.trim()); } ).collect(Collectors.toList()));
        }
    }

    //UPDATE table_name SET column1 = 'new_value1', column2 = 'new_value2' WHERE condition;
    public Update(String query) throws Exception{

        if (query.toLowerCase().trim().startsWith(UPDATE) == false) {
            throw new Exception("update is not included");
        }

        query = query.toLowerCase().substring(UPDATE.length(), query.length());

        String[] list = query.split(SET);
        String table = list[0].trim();
        this.setTable(table);

        String[] setsAndWHere = list[1].split(WHERE);
        String[] sets = setsAndWHere[0].split(",");


        for(String set : sets) {
            String[] columnAndValue = set.split("=");
            this.columns.add(columnAndValue[0].trim());
            this.values.add(columnAndValue[1].trim());
        }

        String[] wheres = setsAndWHere[1].split("and");
        if (wheres != null) {
            this.setWhereClauses(Arrays.stream(wheres).map( item -> { return new WhereClause(item.trim()); } ).collect(Collectors.toList()));
        }
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Update [columns=" + columns + ", values=" + values + "]";
    }

    
    
}
