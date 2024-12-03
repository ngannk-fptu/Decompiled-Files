/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;

public class OracleSequenceMaxValueIncrementer
extends AbstractSequenceMaxValueIncrementer {
    public OracleSequenceMaxValueIncrementer() {
    }

    public OracleSequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }

    @Override
    protected String getSequenceQuery() {
        return "select " + this.getIncrementerName() + ".nextval from dual";
    }
}

