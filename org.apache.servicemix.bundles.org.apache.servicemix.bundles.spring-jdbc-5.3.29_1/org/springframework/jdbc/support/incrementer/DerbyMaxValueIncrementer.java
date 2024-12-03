/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractIdentityColumnMaxValueIncrementer;

public class DerbyMaxValueIncrementer
extends AbstractIdentityColumnMaxValueIncrementer {
    private static final String DEFAULT_DUMMY_NAME = "dummy";
    private String dummyName = "dummy";

    public DerbyMaxValueIncrementer() {
    }

    public DerbyMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName) {
        super(dataSource, incrementerName, columnName);
        this.dummyName = DEFAULT_DUMMY_NAME;
    }

    public DerbyMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName, String dummyName) {
        super(dataSource, incrementerName, columnName);
        this.dummyName = dummyName;
    }

    public void setDummyName(String dummyName) {
        this.dummyName = dummyName;
    }

    public String getDummyName() {
        return this.dummyName;
    }

    @Override
    protected String getIncrementStatement() {
        return "insert into " + this.getIncrementerName() + " (" + this.getDummyName() + ") values(null)";
    }

    @Override
    protected String getIdentityStatement() {
        return "select IDENTITY_VAL_LOCAL() from " + this.getIncrementerName();
    }
}

