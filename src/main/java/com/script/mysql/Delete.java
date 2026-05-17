package src.main.java.com.script.mysql;

import java.util.Arrays;

public class Delete extends SqlBase {
    
    // delete from table where ----
    public Delete(String query) throws Exception {
        
        if (query.toLowerCase().trim().startsWith(DELETE) == false) {
            throw new Exception("delete from should be included");
        }

        String whereStr = query.split(WHERE)[1];
        String[] wheres = whereStr.split("and");

        if (wheres == null) {
            throw new Exception("where should be included");
        }

        this.setWhereClauses(Arrays.stream(wheres).map( item -> { return new WhereClause(item.trim()); } ).toList());

    }

}
