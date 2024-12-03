/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.Dialect;

public class ProgressDialect
extends Dialect {
    public ProgressDialect() {
        this.registerColumnType(-7, "bit");
        this.registerColumnType(-5, "numeric");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "tinyint");
        this.registerColumnType(4, "integer");
        this.registerColumnType(1, "character(1)");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(6, "real");
        this.registerColumnType(8, "double precision");
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(-3, "varbinary($l)");
        this.registerColumnType(2, "numeric($p,$s)");
    }

    @Override
    public boolean hasAlterTable() {
        return false;
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }
}

