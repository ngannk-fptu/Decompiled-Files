/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.SybaseMaxValueIncrementer;

public class SybaseAnywhereMaxValueIncrementer
extends SybaseMaxValueIncrementer {
    public SybaseAnywhereMaxValueIncrementer() {
    }

    public SybaseAnywhereMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName) {
        super(dataSource, incrementerName, columnName);
    }

    @Override
    protected String getIncrementStatement() {
        return "insert into " + this.getIncrementerName() + " values(DEFAULT)";
    }
}

