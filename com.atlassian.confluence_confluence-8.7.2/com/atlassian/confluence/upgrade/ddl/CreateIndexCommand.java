/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.upgrade.ddl.DdlCommand;

public class CreateIndexCommand
implements DdlCommand {
    private String indexName;
    private String tableName;
    private String[] columnNames;
    private boolean isUnique;

    public CreateIndexCommand(String indexName, String tableName, String ... columnNames) {
        this.indexName = indexName;
        this.tableName = tableName;
        this.columnNames = columnNames;
    }

    public CreateIndexCommand(String indexName, String tableName, boolean isUnique, String ... columnNames) {
        this(indexName, tableName, columnNames);
        this.isUnique = isUnique;
    }

    @Override
    public String getStatement() {
        StringBuilder sb = new StringBuilder("create");
        if (this.isUnique) {
            sb.append(" unique ");
        }
        sb.append(" index ");
        sb.append(this.indexName).append(" on ").append(this.tableName).append(" (");
        boolean firstColumn = true;
        for (String name : this.columnNames) {
            if (!firstColumn) {
                sb.append(", ");
            } else {
                firstColumn = false;
            }
            sb.append(name);
        }
        return sb.append(")").toString();
    }
}

