/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public interface GroovyResultSet
extends GroovyObject,
ResultSet {
    public Object getAt(int var1) throws SQLException;

    public Object getAt(String var1);

    public void putAt(int var1, Object var2) throws SQLException;

    public void putAt(String var1, Object var2);

    public void add(Map var1) throws SQLException;

    public void eachRow(Closure var1) throws SQLException;
}

