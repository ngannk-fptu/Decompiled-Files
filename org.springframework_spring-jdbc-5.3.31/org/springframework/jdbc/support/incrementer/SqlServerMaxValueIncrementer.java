/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractIdentityColumnMaxValueIncrementer;

public class SqlServerMaxValueIncrementer
extends AbstractIdentityColumnMaxValueIncrementer {
    public SqlServerMaxValueIncrementer() {
    }

    public SqlServerMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName) {
        super(dataSource, incrementerName, columnName);
    }

    @Override
    protected String getIncrementStatement() {
        return "insert into " + this.getIncrementerName() + " default values";
    }

    @Override
    protected String getIdentityStatement() {
        return "select @@identity";
    }
}

