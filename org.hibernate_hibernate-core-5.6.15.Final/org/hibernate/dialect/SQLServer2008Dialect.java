/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.NullPrecedence;
import org.hibernate.dialect.SQLServer2005Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class SQLServer2008Dialect
extends SQLServer2005Dialect {
    private static final int NVARCHAR_MAX_LENGTH = 4000;

    public SQLServer2008Dialect() {
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "datetime2");
        this.registerColumnType(-9, 4000L, "nvarchar($l)");
        this.registerColumnType(-9, "nvarchar(MAX)");
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
    }

    @Override
    public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
        StringBuilder orderByElement = new StringBuilder();
        if (nulls != null && !NullPrecedence.NONE.equals((Object)nulls)) {
            orderByElement.append("case when ").append(expression).append(" is null then ");
            if (NullPrecedence.FIRST.equals((Object)nulls)) {
                orderByElement.append("0 else 1");
            } else {
                orderByElement.append("1 else 0");
            }
            orderByElement.append(" end, ");
        }
        orderByElement.append(super.renderOrderByElement(expression, collation, order, NullPrecedence.NONE));
        return orderByElement.toString();
    }

    @Override
    public boolean supportsValuesList() {
        return true;
    }
}

