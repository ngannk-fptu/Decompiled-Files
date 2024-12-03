/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

final class SQLIdentifier {
    private String serverName = "";
    private String databaseName = "";
    private String schemaName = "";
    private String objectName = "";

    SQLIdentifier() {
    }

    final String getServerName() {
        return this.serverName;
    }

    final void setServerName(String name) {
        this.serverName = name;
    }

    final String getDatabaseName() {
        return this.databaseName;
    }

    final void setDatabaseName(String name) {
        this.databaseName = name;
    }

    final String getSchemaName() {
        return this.schemaName;
    }

    final void setSchemaName(String name) {
        this.schemaName = name;
    }

    final String getObjectName() {
        return this.objectName;
    }

    final void setObjectName(String name) {
        this.objectName = name;
    }

    final String asEscapedString() {
        StringBuilder fullName = new StringBuilder(256);
        if (this.serverName.length() > 0) {
            fullName.append("[").append(this.serverName).append("].");
        }
        if (this.databaseName.length() > 0) {
            fullName.append("[").append(this.databaseName).append("].");
        } else assert (0 == this.serverName.length());
        if (this.schemaName.length() > 0) {
            fullName.append("[").append(this.schemaName).append("].");
        } else if (this.databaseName.length() > 0) {
            fullName.append('.');
        }
        fullName.append("[").append(this.objectName).append("]");
        return fullName.toString();
    }
}

