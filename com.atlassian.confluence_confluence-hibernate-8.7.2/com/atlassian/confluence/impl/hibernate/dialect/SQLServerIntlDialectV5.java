/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.dialect.SQLServer2005Dialect
 */
package com.atlassian.confluence.impl.hibernate.dialect;

import org.hibernate.dialect.SQLServer2005Dialect;

@Deprecated
public class SQLServerIntlDialectV5
extends SQLServer2005Dialect {
    private static final int MAX_LENGTH = 8000;

    public SQLServerIntlDialectV5() {
        this.registerColumnType(1, "nchar(1)");
        this.registerColumnType(12, "nvarchar(MAX)");
        this.registerColumnType(12, 8000L, "nvarchar($l)");
        this.registerColumnType(2005, "ntext");
    }
}

