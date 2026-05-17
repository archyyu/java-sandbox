package src.main.java.com.script.mysql;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Insert extends SqlBase{

    private List<String> columns;
    private List<String> values;

    public Insert(String[] columns, String table, String[] values) {
        this.initInsertClause(columns, table, values);
    }

    //INSERT INTO employees (id, name, salary) VALUES (1, 'Alice', 50000);
    public Insert(String query) throws Exception {

        if (query.trim().toLowerCase().startsWith(INSERT) == false) {
            throw new Exception("the sql should start with " + INSERT);
        }

        query = query.substring( INSERT.length(), query.length());
        String[] list = query.split("values");
        if (list.length < 2) {
            throw new Exception("the insert clause should have values");
        }

        //parse the table
        String table = list[0].trim().split("\\(")[0].trim();
        String[] columns = this.extractFromParentheses(list[0].trim()).split(",");
        String[] values = this.extractFromParentheses(list[1].trim()).split(",");
        
        if (columns.length != values.length) {
            throw new Exception("the insert clause should have equal columns and values");
        }

        this.initInsertClause(columns, table, values);

    }

    private void initInsertClause(String[] columns, String table, String[] values) {
        this.columns = Arrays.stream(columns).map(item -> {return item.trim();}).collect(Collectors.toList());
        this.setTable(table);
        this.values = Arrays.stream(values).map(item -> {return item.trim();}).collect(Collectors.toList());
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
        return "Insert [columns=" + columns + ", values=" + values + ", table=" + this.getTable() + "]";
    }

    

}
