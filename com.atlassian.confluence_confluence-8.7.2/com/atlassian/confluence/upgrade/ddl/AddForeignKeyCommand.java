/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;

public class AddForeignKeyCommand
implements AlterTableCommand {
    private final String constraintName;
    private final String sourceColumn;
    private final String destinationTable;
    private final String destinationColumn;

    public static AddForeignKeyCommand createCommand(String constraintName, String sourceColumn, String destinationTable, String destinationColumn) {
        return new AddForeignKeyCommand(constraintName, sourceColumn, destinationTable, destinationColumn);
    }

    private AddForeignKeyCommand(String constraintName, String sourceColumn, String destinationTable, String destinationColumn) {
        this.constraintName = constraintName;
        this.sourceColumn = sourceColumn;
        this.destinationTable = destinationTable;
        this.destinationColumn = destinationColumn;
    }

    @Override
    public String getCommandName() {
        return "add constraint ";
    }

    @Override
    public String getCommandParameters() {
        return this.constraintName + " foreign key (" + this.sourceColumn + ") references " + this.destinationTable + "(" + this.destinationColumn + ")";
    }
}

