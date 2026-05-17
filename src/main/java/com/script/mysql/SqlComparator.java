package src.main.java.com.script.mysql;

public enum SqlComparator {

    GREATEQUAL(">="){
        @Override
        public boolean compare(String a, String b) {
            return Float.parseFloat(a) >= Float.parseFloat(b);
        }
    },
    LESSEQUAL("<=") {
        @Override
        public boolean compare(String a, String b) {
            return Float.parseFloat(a) <= Float.parseFloat(b);
        }
    },
    EQUAL("=") {
        @Override
        public boolean compare(String a, String b) {
            return Float.parseFloat(a) == Float.parseFloat(b);
        }
    },
    GREAT(">") {
        @Override
        public boolean compare(String a, String b) {
            return Float.parseFloat(a) > Float.parseFloat(b);
        }
    },
    LESS("<") {
        @Override
        public boolean compare(String a, String b) {
            return Float.parseFloat(a) < Float.parseFloat(b);
        }
    };

    private String opeString;

    private SqlComparator(String str) {
        this.opeString = str;
    }

    public String getOperator() {
        return this.opeString;
    }

    public abstract boolean compare(String a, String b);

}