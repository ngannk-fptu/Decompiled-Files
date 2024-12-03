/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;

public class Db2LuwMaxValueIncrementer
extends AbstractSequenceMaxValueIncrementer {
    public Db2LuwMaxValueIncrementer() {
    }

    public Db2LuwMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }

    @Override
    protected String getSequenceQuery() {
        return "values nextval for " + this.getIncrementerName();
    }
}

