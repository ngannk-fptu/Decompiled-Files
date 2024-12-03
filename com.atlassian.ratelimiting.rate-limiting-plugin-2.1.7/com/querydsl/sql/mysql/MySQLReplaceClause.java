/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.mysql;

import com.querydsl.core.QueryFlag;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLInsertClause;
import java.sql.Connection;

public class MySQLReplaceClause
extends SQLInsertClause {
    private static final String REPLACE_INTO = "replace into ";

    public MySQLReplaceClause(Connection connection, SQLTemplates templates, RelationalPath<?> entity) {
        super(connection, templates, entity);
        this.addFlag(QueryFlag.Position.START_OVERRIDE, REPLACE_INTO);
    }

    public MySQLReplaceClause(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
        this.addFlag(QueryFlag.Position.START_OVERRIDE, REPLACE_INTO);
    }
}

