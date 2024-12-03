/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;
import groovy.sql.GroovyResultSetProxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;

public class GroovyResultSetExtension
extends GroovyObjectSupport {
    private boolean updated = false;
    private final ResultSet resultSet;

    protected ResultSet getResultSet() throws SQLException {
        return this.resultSet;
    }

    public GroovyResultSetExtension(ResultSet set) {
        this.resultSet = set;
    }

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder("[");
            ResultSetMetaData metaData = this.resultSet.getMetaData();
            int count = metaData.getColumnCount();
            for (int i = 1; i <= count; ++i) {
                sb.append(metaData.getColumnName(i));
                sb.append(":");
                Object object = this.resultSet.getObject(i);
                if (object != null) {
                    sb.append(object.toString());
                } else {
                    sb.append("[null]");
                }
                if (i >= count) continue;
                sb.append(", ");
            }
            sb.append("]");
            return sb.toString();
        }
        catch (SQLException e) {
            return super.toString();
        }
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return InvokerHelper.invokeMethod(this.getResultSet(), name, args);
        }
        catch (SQLException se) {
            throw new InvokerInvocationException(se);
        }
    }

    @Override
    public Object getProperty(String columnName) {
        try {
            return this.getResultSet().getObject(columnName);
        }
        catch (SQLException e) {
            throw new MissingPropertyException(columnName, GroovyResultSetProxy.class, e);
        }
    }

    @Override
    public void setProperty(String columnName, Object newValue) {
        try {
            this.getResultSet().updateObject(columnName, newValue);
            this.updated = true;
        }
        catch (SQLException e) {
            throw new MissingPropertyException(columnName, GroovyResultSetProxy.class, e);
        }
    }

    public Object getAt(int index) throws SQLException {
        index = this.normalizeIndex(index);
        return this.getResultSet().getObject(index);
    }

    public void putAt(int index, Object newValue) throws SQLException {
        index = this.normalizeIndex(index);
        this.getResultSet().updateObject(index, newValue);
    }

    public void add(Map values) throws SQLException {
        this.getResultSet().moveToInsertRow();
        for (Map.Entry entry : values.entrySet()) {
            this.getResultSet().updateObject(entry.getKey().toString(), entry.getValue());
        }
        this.getResultSet().insertRow();
    }

    protected int normalizeIndex(int index) throws SQLException {
        if (index < 0) {
            int columnCount = this.getResultSet().getMetaData().getColumnCount();
            while ((index += columnCount) < 0) {
            }
        }
        return index + 1;
    }

    public void eachRow(Closure closure) throws SQLException {
        while (this.next()) {
            closure.call((Object)this);
        }
    }

    public boolean next() throws SQLException {
        if (this.updated) {
            this.getResultSet().updateRow();
            this.updated = false;
        }
        return this.getResultSet().next();
    }

    public boolean previous() throws SQLException {
        if (this.updated) {
            this.getResultSet().updateRow();
            this.updated = false;
        }
        return this.getResultSet().previous();
    }
}

