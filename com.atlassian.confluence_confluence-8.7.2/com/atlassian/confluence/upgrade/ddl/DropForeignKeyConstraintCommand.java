/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;

public class DropForeignKeyConstraintCommand
implements AlterTableCommand {
    private final boolean isMySQL;
    private final String constraintName;

    public DropForeignKeyConstraintCommand(boolean isMySQL, String constraintName) {
        this.isMySQL = isMySQL;
        this.constraintName = constraintName;
    }

    @Override
    public String getCommandName() {
        if (this.isMySQL) {
            return "DROP FOREIGN KEY";
        }
        return "DROP CONSTRAINT";
    }

    @Override
    public String getCommandParameters() {
        return this.constraintName;
    }
}

