/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;

public class H2SequenceMaxValueIncrementer
extends AbstractSequenceMaxValueIncrementer {
    public H2SequenceMaxValueIncrementer() {
    }

    public H2SequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }

    @Override
    protected String getSequenceQuery() {
        return "values next value for " + this.getIncrementerName();
    }
}

