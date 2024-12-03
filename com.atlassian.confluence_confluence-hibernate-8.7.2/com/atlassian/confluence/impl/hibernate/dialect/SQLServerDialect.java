/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.dialect.function.SQLFunction
 *  org.hibernate.dialect.function.SQLFunctionTemplate
 *  org.hibernate.dialect.function.StandardSQLFunction
 *  org.hibernate.type.StandardBasicTypes
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.hibernate.dialect;

import com.atlassian.confluence.impl.hibernate.dialect.SQLServerIntlDialectV5;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class SQLServerDialect
extends SQLServerIntlDialectV5 {
    private static final int MAX_LENGTH = 8000;

    public SQLServerDialect() {
        this.registerColumnType(-7, "tinyint");
        this.registerColumnType(16, "tinyint");
        this.registerColumnType(-5, "numeric(19,0)");
        this.registerColumnType(12, "nvarchar(MAX)");
        this.registerColumnType(12, 8000L, "nvarchar($l)");
        this.registerColumnType(2004, "image");
        this.registerFunction("replace", (SQLFunction)new StandardSQLFunction("replace", (Type)StandardBasicTypes.STRING));
        this.registerFunction("length", (SQLFunction)new SQLFunctionTemplate((Type)StandardBasicTypes.INTEGER, "len(convert(nvarchar(max), ?1))"));
    }
}

