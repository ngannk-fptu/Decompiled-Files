/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.Db2LuwMaxValueIncrementer;

@Deprecated
public class DB2SequenceMaxValueIncrementer
extends Db2LuwMaxValueIncrementer {
    public DB2SequenceMaxValueIncrementer() {
    }

    public DB2SequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }
}

