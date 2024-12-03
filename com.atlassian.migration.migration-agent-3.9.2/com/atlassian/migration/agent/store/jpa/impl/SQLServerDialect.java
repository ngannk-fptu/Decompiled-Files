/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.dialect.SQLServer2005Dialect
 *  org.hibernate.dialect.function.SQLFunction
 *  org.hibernate.dialect.function.StandardSQLFunction
 *  org.hibernate.type.StandardBasicTypes
 *  org.hibernate.type.Type
 */
package com.atlassian.migration.agent.store.jpa.impl;

import org.hibernate.dialect.SQLServer2005Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class SQLServerDialect
extends SQLServer2005Dialect {
    private static final int MAX_LENGTH = 4000;

    public SQLServerDialect() {
        this.registerColumnType(-7, "tinyint");
        this.registerColumnType(16, "tinyint");
        this.registerColumnType(12, "nvarchar(MAX)");
        this.registerColumnType(12, 4000L, "nvarchar($l)");
        this.registerColumnType(2004, "image");
        this.registerFunction("replace", (SQLFunction)new StandardSQLFunction("replace", (Type)StandardBasicTypes.STRING));
        this.registerColumnType(2005, "nvarchar(MAX)");
        this.registerColumnType(-5, "numeric(38,0)");
    }
}

