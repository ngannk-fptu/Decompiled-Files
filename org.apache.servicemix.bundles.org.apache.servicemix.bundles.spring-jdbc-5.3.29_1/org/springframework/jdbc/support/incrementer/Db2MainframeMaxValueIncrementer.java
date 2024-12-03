/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;

public class Db2MainframeMaxValueIncrementer
extends AbstractSequenceMaxValueIncrementer {
    public Db2MainframeMaxValueIncrementer() {
    }

    public Db2MainframeMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }

    @Override
    protected String getSequenceQuery() {
        return "select next value for " + this.getIncrementerName() + " from sysibm.sysdummy1";
    }
}

