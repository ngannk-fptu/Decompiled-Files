/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;

public class DropColumnCommand
implements AlterTableCommand {
    private final String columnName;

    public DropColumnCommand(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String getCommandName() {
        return "DROP COLUMN";
    }

    @Override
    public String getCommandParameters() {
        return this.columnName;
    }
}

