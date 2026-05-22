package com.script.mysql;

public class WhereClause {
    
    private String column;

    private SqlComparator sqlComparator;

    private String value;

    public WhereClause(String str) {
 
        for(SqlComparator item : SqlComparator.values()) {
            if (str.contains(item.getOperator())) {
                this.sqlComparator = item;
                this.column = str.split(item.getOperator())[0].trim();
                this.value = str.split(item.getOperator())[1].trim();
                return;
            }
        }
    
    }



    public boolean trueOrNot(String value) {
        return this.sqlComparator.compare(value, this.value);
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public SqlComparator getSqlComparator() {
        return sqlComparator;
    }

    public void setSqlComparator(SqlComparator sqlComparator) {
        this.sqlComparator = sqlComparator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "WhereClause [column=" + column + ", sqlComparator=" + sqlComparator + ", value=" + value + "]";
    }

    

}
