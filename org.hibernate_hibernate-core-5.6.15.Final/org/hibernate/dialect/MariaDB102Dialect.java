/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MariaDB10Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MariaDB102Dialect
extends MariaDB10Dialect {
    public MariaDB102Dialect() {
        this.registerColumnType(2000, "json");
        this.registerFunction("json_valid", new StandardSQLFunction("json_valid", StandardBasicTypes.NUMERIC_BOOLEAN));
    }

    @Override
    public boolean supportsColumnCheck() {
        return true;
    }
}

