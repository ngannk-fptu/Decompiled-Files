/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;

@Deprecated
public class DB2MainframeSequenceMaxValueIncrementer
extends AbstractSequenceMaxValueIncrementer {
    public DB2MainframeSequenceMaxValueIncrementer() {
    }

    public DB2MainframeSequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }

    @Override
    protected String getSequenceQuery() {
        return "select next value for " + this.getIncrementerName() + " from sysibm.sysdummy1";
    }
}

