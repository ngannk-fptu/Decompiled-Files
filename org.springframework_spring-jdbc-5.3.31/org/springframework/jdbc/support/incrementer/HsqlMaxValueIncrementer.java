/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractIdentityColumnMaxValueIncrementer;

public class HsqlMaxValueIncrementer
extends AbstractIdentityColumnMaxValueIncrementer {
    public HsqlMaxValueIncrementer() {
    }

    public HsqlMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName) {
        super(dataSource, incrementerName, columnName);
    }

    @Override
    protected String getIncrementStatement() {
        return "insert into " + this.getIncrementerName() + " values(null)";
    }

    @Override
    protected String getIdentityStatement() {
        return "select max(identity()) from " + this.getIncrementerName();
    }
}

