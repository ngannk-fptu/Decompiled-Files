/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.JDBCDynaClass;

public class RowSetDynaClass
extends JDBCDynaClass
implements DynaClass,
Serializable {
    protected int limit = -1;
    protected List<DynaBean> rows = new ArrayList<DynaBean>();

    public RowSetDynaClass(ResultSet resultSet) throws SQLException {
        this(resultSet, true, -1);
    }

    public RowSetDynaClass(ResultSet resultSet, int limit) throws SQLException {
        this(resultSet, true, limit);
    }

    public RowSetDynaClass(ResultSet resultSet, boolean lowerCase) throws SQLException {
        this(resultSet, lowerCase, -1);
    }

    public RowSetDynaClass(ResultSet resultSet, boolean lowerCase, int limit) throws SQLException {
        this(resultSet, lowerCase, limit, false);
    }

    public RowSetDynaClass(ResultSet resultSet, boolean lowerCase, boolean useColumnLabel) throws SQLException {
        this(resultSet, lowerCase, -1, useColumnLabel);
    }

    public RowSetDynaClass(ResultSet resultSet, boolean lowerCase, int limit, boolean useColumnLabel) throws SQLException {
        if (resultSet == null) {
            throw new NullPointerException();
        }
        this.lowerCase = lowerCase;
        this.limit = limit;
        this.setUseColumnLabel(useColumnLabel);
        this.introspect(resultSet);
        this.copy(resultSet);
    }

    public List<DynaBean> getRows() {
        return this.rows;
    }

    protected void copy(ResultSet resultSet) throws SQLException {
        int cnt = 0;
        while (resultSet.next() && (this.limit < 0 || cnt++ < this.limit)) {
            DynaBean bean = this.createDynaBean();
            for (DynaProperty propertie : this.properties) {
                String name = propertie.getName();
                Object value = this.getObject(resultSet, name);
                bean.set(name, value);
            }
            this.rows.add(bean);
        }
    }

    protected DynaBean createDynaBean() {
        return new BasicDynaBean(this);
    }
}

