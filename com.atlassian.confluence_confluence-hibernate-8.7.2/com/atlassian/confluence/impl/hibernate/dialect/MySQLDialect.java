/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.dialect.MySQL57Dialect
 */
package com.atlassian.confluence.impl.hibernate.dialect;

import org.hibernate.dialect.MySQL57Dialect;

public class MySQLDialect
extends MySQL57Dialect {
    protected void registerVarcharTypes() {
        this.registerColumnType(-1, "longtext");
        this.registerColumnType(12, "longtext");
        this.registerColumnType(12, 0xFFFFFFL, "mediumtext");
        this.registerColumnType(12, 65535L, "text");
        this.registerColumnType(12, 255L, "varchar($l)");
        this.registerColumnType(2005, 0xFFFFFFL, "mediumtext");
        this.registerColumnType(2005, 65535L, "text");
    }
}

