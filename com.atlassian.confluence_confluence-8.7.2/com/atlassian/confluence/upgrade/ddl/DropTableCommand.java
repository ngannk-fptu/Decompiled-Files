/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.upgrade.ddl.DdlCommand;

public class DropTableCommand
implements DdlCommand {
    private final String tableName;

    public DropTableCommand(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getStatement() {
        return "drop table " + this.tableName;
    }
}

