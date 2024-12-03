/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;

public class HsqlSequenceMaxValueIncrementer
extends AbstractSequenceMaxValueIncrementer {
    public HsqlSequenceMaxValueIncrementer() {
    }

    public HsqlSequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }

    @Override
    protected String getSequenceQuery() {
        return "call next value for " + this.getIncrementerName();
    }
}

