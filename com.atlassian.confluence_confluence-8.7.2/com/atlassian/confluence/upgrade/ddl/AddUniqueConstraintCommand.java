/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

public class AddUniqueConstraintCommand
implements AlterTableCommand {
    private String constraintName;
    private Collection<String> columns;

    public AddUniqueConstraintCommand(String constraintName, Collection<String> columns) {
        if (constraintName.length() > 128) {
            throw new IllegalArgumentException("Constraint name is too long for DB2 (max 128 characters)");
        }
        this.constraintName = constraintName;
        this.columns = columns;
    }

    @Override
    public String getCommandName() {
        return "add constraint";
    }

    @Override
    public String getCommandParameters() {
        return this.constraintName + " unique (" + StringUtils.join(this.columns, (String)", ") + ")";
    }
}

