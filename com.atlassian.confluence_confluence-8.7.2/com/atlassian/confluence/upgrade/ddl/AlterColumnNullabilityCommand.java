/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;
import com.atlassian.confluence.upgrade.ddl.NullChoice;

public class AlterColumnNullabilityCommand
implements AlterTableCommand {
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private final String columnName;
    private final String oldDataType;
    private final NullChoice nullChoice;

    public AlterColumnNullabilityCommand(HibernateDatabaseCapabilities databaseCapabilities, String columnName, String oldDataType, NullChoice nullChoice) {
        this.databaseCapabilities = databaseCapabilities;
        this.columnName = columnName;
        this.oldDataType = oldDataType;
        this.nullChoice = nullChoice;
    }

    @Override
    public String getCommandName() {
        if (this.databaseCapabilities.isMySql() || this.databaseCapabilities.isOracle()) {
            return "modify";
        }
        return "alter column";
    }

    @Override
    public String getCommandParameters() {
        StringBuilder sb = new StringBuilder(this.columnName).append(" ");
        if (this.databaseCapabilities.isMySql() || this.databaseCapabilities.isOracle()) {
            sb.append(this.oldDataType).append(" ").append(this.nullChoice.toSqlFragment());
        } else if (this.databaseCapabilities.isSqlServer()) {
            sb.append(this.oldDataType).append(" ").append(this.nullChoice.toSqlFragment());
        } else if (this.databaseCapabilities.isHSQL()) {
            sb.append("set ").append(this.nullChoice.toSqlFragment());
        } else {
            sb.append(this.nullChoice == NullChoice.NOT_NULL ? "set not null" : "drop not null");
        }
        return sb.toString();
    }
}

