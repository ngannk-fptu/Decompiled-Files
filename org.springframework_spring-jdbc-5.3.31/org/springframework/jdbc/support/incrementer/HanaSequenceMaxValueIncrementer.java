/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;

public class HanaSequenceMaxValueIncrementer
extends AbstractSequenceMaxValueIncrementer {
    public HanaSequenceMaxValueIncrementer() {
    }

    public HanaSequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }

    @Override
    protected String getSequenceQuery() {
        return "select " + this.getIncrementerName() + ".nextval from dummy";
    }
}

