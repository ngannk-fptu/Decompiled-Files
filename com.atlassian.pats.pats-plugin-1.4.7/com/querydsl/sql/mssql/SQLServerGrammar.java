/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.mssql;

import com.querydsl.sql.mssql.SQLServerTableHints;

final class SQLServerGrammar {
    private SQLServerGrammar() {
    }

    static String tableHints(SQLServerTableHints ... tableHints) {
        StringBuilder hints = new StringBuilder(" with ").append("(");
        for (int i = 0; i < tableHints.length; ++i) {
            if (i > 0) {
                hints.append(", ");
            }
            hints.append(tableHints[i].name());
        }
        hints.append(")");
        return hints.toString();
    }
}

