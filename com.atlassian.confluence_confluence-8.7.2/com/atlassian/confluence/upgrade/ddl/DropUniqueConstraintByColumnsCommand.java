/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;
import org.apache.commons.lang3.ArrayUtils;

public class DropUniqueConstraintByColumnsCommand
implements AlterTableCommand {
    private final String[] columnNames;

    public DropUniqueConstraintByColumnsCommand(String[] columnNames) {
        this.columnNames = (String[])ArrayUtils.clone((Object[])columnNames);
    }

    @Override
    public String getCommandName() {
        return "drop unique";
    }

    @Override
    public String getCommandParameters() {
        return "(" + String.join((CharSequence)",", this.columnNames) + ")";
    }
}

