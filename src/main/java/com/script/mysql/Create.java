package src.main.java.com.script.mysql;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Create extends SqlBase {
    
    private List<String> columns;

    public Create(String table, String[] columns) {
        this.setTable(table);
        this.columns = Arrays.stream(columns).map( item -> { return item.trim(); }).collect(Collectors.toList());
    }

    public Create(String query) throws Exception{

        if (query.toLowerCase().trim().startsWith(CREATE) == false) {
            throw new Exception("create must be included");
        }

        String[] arrays = query.split("\\(");

        String table = arrays[0].trim().split(" ")[2];
        String[] columns = this.extractFromParentheses(query).split(",");

        this.setTable(table);
        this.columns = Arrays.stream(columns).map( item -> { return item.trim(); }).collect(Collectors.toList());

    }

    public List<String> getColumns() {
        return this.columns;
    }

    @Override
    public String toString() {
        return "Create [columns=" + columns + ", getTable()=" + getTable() + "]";
    }

    

}
