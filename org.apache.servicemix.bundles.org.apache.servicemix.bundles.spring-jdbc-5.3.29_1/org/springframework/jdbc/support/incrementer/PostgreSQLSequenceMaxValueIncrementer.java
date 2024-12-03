/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.PostgresSequenceMaxValueIncrementer;

@Deprecated
public class PostgreSQLSequenceMaxValueIncrementer
extends PostgresSequenceMaxValueIncrementer {
    public PostgreSQLSequenceMaxValueIncrementer() {
    }

    public PostgreSQLSequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }
}

