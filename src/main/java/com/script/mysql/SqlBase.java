package src.main.java.com.script.mysql;

import java.util.List;

public class SqlBase {
    
    public final static String AND = "and";

    public final static String FROM = "from";

    public final static String WHERE = "where";

    public final static String SET = "set";

    public final static String INSERT = "insert into";

    public final static String SELECT = "select";

    public final static String UPDATE = "update";

    public final static String DELETE = "delete from";

    public final static String CREATE = "create table";

    protected List<WhereClause> whereClauses;

    private String table;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<WhereClause> getWhereClauses() {
        return whereClauses;
    }

    public void setWhereClauses(List<WhereClause> whereClauses) {
        this.whereClauses = whereClauses;
    }
    
    public String extractFromParentheses(String str) {

        int start = str.indexOf('(') + 1; // Find the index of '(' and move to the next character
        int end = str.indexOf(')', start); // Find the index of ')' starting from 'start'

        if (start > 0 && end > 0) {
            return str.substring(start, end);
        } else {
            return null;
        }
    }

}
