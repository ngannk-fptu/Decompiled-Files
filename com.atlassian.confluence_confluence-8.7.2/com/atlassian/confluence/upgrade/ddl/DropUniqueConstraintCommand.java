/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;

public class DropUniqueConstraintCommand
implements AlterTableCommand {
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private final String constraintName;
    private final String addIfExists;

    public DropUniqueConstraintCommand(HibernateDatabaseCapabilities databaseCapabilities, String constraintName) {
        this(databaseCapabilities, constraintName, false);
    }

    public DropUniqueConstraintCommand(HibernateDatabaseCapabilities databaseCapabilities, String constraintName, boolean addIfExists) {
        this.databaseCapabilities = databaseCapabilities;
        this.constraintName = constraintName;
        this.addIfExists = addIfExists ? " if exists" : "";
    }

    @Override
    public String getCommandName() {
        if (this.databaseCapabilities.isMySql()) {
            return "drop index" + this.addIfExists;
        }
        return "drop constraint" + this.addIfExists;
    }

    @Override
    public String getCommandParameters() {
        return this.constraintName;
    }

    public String getConstraintName() {
        return this.constraintName;
    }
}

