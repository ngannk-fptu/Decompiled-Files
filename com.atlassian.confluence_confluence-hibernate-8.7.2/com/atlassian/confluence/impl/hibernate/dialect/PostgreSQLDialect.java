/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.dialect.PostgresPlusDialect
 *  org.hibernate.dialect.function.SQLFunction
 *  org.hibernate.dialect.function.StandardSQLFunction
 */
package com.atlassian.confluence.impl.hibernate.dialect;

import org.hibernate.dialect.PostgresPlusDialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;

public class PostgreSQLDialect
extends PostgresPlusDialect {
    public PostgreSQLDialect() {
        this.registerColumnType(2004, "bytea");
        this.registerColumnType(2014, "timestamptz");
        this.registerFunction("coalesce", (SQLFunction)new StandardSQLFunction("coalesce"));
        this.registerColumnType(2005, "text");
    }
}

