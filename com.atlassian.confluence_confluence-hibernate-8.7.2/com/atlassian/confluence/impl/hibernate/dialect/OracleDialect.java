/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.hibernate.dialect;

import com.atlassian.confluence.impl.hibernate.dialect.OracleIntlDialectV5;

public class OracleDialect
extends OracleIntlDialectV5 {
    public OracleDialect() {
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "date");
        this.registerColumnType(93, "date");
        this.registerColumnType(1, "char(1)");
    }
}

