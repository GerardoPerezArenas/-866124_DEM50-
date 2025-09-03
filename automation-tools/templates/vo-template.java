package {{PACKAGE}};

import java.sql.Date;

/*
    {{COMMENTS}}
*/

public class {{CLASS_NAME}} {
    
    private Integer id;
    private String numExp;
    
{{FIELDS}}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumExp() {
        return numExp;
    }

    public void setNumExp(String numExp) {
        this.numExp = numExp;
    }

{{GETTERS_SETTERS}}
    
}