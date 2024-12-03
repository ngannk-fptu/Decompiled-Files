/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

@Deprecated
public class SchemaUpdateCommand {
    private final String sql;
    private final boolean quiet;

    public SchemaUpdateCommand(String sql, boolean quiet) {
        this.sql = sql;
        this.quiet = quiet;
    }

    public String getSql() {
        return this.sql;
    }

    public boolean isQuiet() {
        return this.quiet;
    }
}

