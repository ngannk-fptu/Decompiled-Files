/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.PostgreSQL93Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class PostgreSQL94Dialect
extends PostgreSQL93Dialect {
    public PostgreSQL94Dialect() {
        this.registerFunction("make_interval", new StandardSQLFunction("make_interval", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("make_timestamp", new StandardSQLFunction("make_timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("make_timestamptz", new StandardSQLFunction("make_timestamptz", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("make_date", new StandardSQLFunction("make_date", StandardBasicTypes.DATE));
        this.registerFunction("make_time", new StandardSQLFunction("make_time", StandardBasicTypes.TIME));
    }
}

