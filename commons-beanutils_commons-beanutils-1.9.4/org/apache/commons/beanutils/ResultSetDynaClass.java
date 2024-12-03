/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.JDBCDynaClass;
import org.apache.commons.beanutils.ResultSetIterator;

public class ResultSetDynaClass
extends JDBCDynaClass
implements DynaClass {
    protected ResultSet resultSet = null;

    public ResultSetDynaClass(ResultSet resultSet) throws SQLException {
        this(resultSet, true);
    }

    public ResultSetDynaClass(ResultSet resultSet, boolean lowerCase) throws SQLException {
        this(resultSet, lowerCase, false);
    }

    public ResultSetDynaClass(ResultSet resultSet, boolean lowerCase, boolean useColumnLabel) throws SQLException {
        if (resultSet == null) {
            throw new NullPointerException();
        }
        this.resultSet = resultSet;
        this.lowerCase = lowerCase;
        this.setUseColumnLabel(useColumnLabel);
        this.introspect(resultSet);
    }

    public Iterator<DynaBean> iterator() {
        return new ResultSetIterator(this);
    }

    public Object getObjectFromResultSet(String name) throws SQLException {
        return this.getObject(this.getResultSet(), name);
    }

    ResultSet getResultSet() {
        return this.resultSet;
    }

    @Override
    protected Class<?> loadClass(String className) throws SQLException {
        try {
            return this.getClass().getClassLoader().loadClass(className);
        }
        catch (Exception e) {
            throw new SQLException("Cannot load column class '" + className + "': " + e);
        }
    }
}

