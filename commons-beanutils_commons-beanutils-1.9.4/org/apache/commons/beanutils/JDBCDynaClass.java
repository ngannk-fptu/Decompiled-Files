/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

abstract class JDBCDynaClass
implements DynaClass,
Serializable {
    protected boolean lowerCase = true;
    private boolean useColumnLabel;
    protected DynaProperty[] properties = null;
    protected Map<String, DynaProperty> propertiesMap = new HashMap<String, DynaProperty>();
    private Map<String, String> columnNameXref;

    JDBCDynaClass() {
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public DynaProperty getDynaProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        return this.propertiesMap.get(name);
    }

    @Override
    public DynaProperty[] getDynaProperties() {
        return this.properties;
    }

    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        throw new UnsupportedOperationException("newInstance() not supported");
    }

    public void setUseColumnLabel(boolean useColumnLabel) {
        this.useColumnLabel = useColumnLabel;
    }

    protected Class<?> loadClass(String className) throws SQLException {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = this.getClass().getClassLoader();
            }
            return Class.forName(className, false, cl);
        }
        catch (Exception e) {
            throw new SQLException("Cannot load column class '" + className + "': " + e);
        }
    }

    protected DynaProperty createDynaProperty(ResultSetMetaData metadata, int i) throws SQLException {
        String name;
        String columnName = null;
        if (this.useColumnLabel) {
            columnName = metadata.getColumnLabel(i);
        }
        if (columnName == null || columnName.trim().length() == 0) {
            columnName = metadata.getColumnName(i);
        }
        String string = name = this.lowerCase ? columnName.toLowerCase() : columnName;
        if (!name.equals(columnName)) {
            if (this.columnNameXref == null) {
                this.columnNameXref = new HashMap<String, String>();
            }
            this.columnNameXref.put(name, columnName);
        }
        String className = null;
        try {
            int sqlType = metadata.getColumnType(i);
            switch (sqlType) {
                case 91: {
                    return new DynaProperty(name, Date.class);
                }
                case 93: {
                    return new DynaProperty(name, Timestamp.class);
                }
                case 92: {
                    return new DynaProperty(name, Time.class);
                }
            }
            className = metadata.getColumnClassName(i);
        }
        catch (SQLException sqlType) {
            // empty catch block
        }
        Class clazz = Object.class;
        if (className != null) {
            clazz = this.loadClass(className);
        }
        return new DynaProperty(name, clazz);
    }

    protected void introspect(ResultSet resultSet) throws SQLException {
        ArrayList<DynaProperty> list = new ArrayList<DynaProperty>();
        ResultSetMetaData metadata = resultSet.getMetaData();
        int n = metadata.getColumnCount();
        for (int i = 1; i <= n; ++i) {
            DynaProperty dynaProperty = this.createDynaProperty(metadata, i);
            if (dynaProperty == null) continue;
            list.add(dynaProperty);
        }
        for (DynaProperty propertie : this.properties = list.toArray(new DynaProperty[list.size()])) {
            this.propertiesMap.put(propertie.getName(), propertie);
        }
    }

    protected Object getObject(ResultSet resultSet, String name) throws SQLException {
        DynaProperty property = this.getDynaProperty(name);
        if (property == null) {
            throw new IllegalArgumentException("Invalid name '" + name + "'");
        }
        String columnName = this.getColumnName(name);
        Class<?> type = property.getType();
        if (type.equals(Date.class)) {
            return resultSet.getDate(columnName);
        }
        if (type.equals(Timestamp.class)) {
            return resultSet.getTimestamp(columnName);
        }
        if (type.equals(Time.class)) {
            return resultSet.getTime(columnName);
        }
        return resultSet.getObject(columnName);
    }

    protected String getColumnName(String name) {
        if (this.columnNameXref != null && this.columnNameXref.containsKey(name)) {
            return this.columnNameXref.get(name);
        }
        return name;
    }
}

