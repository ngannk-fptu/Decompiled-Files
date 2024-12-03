/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.PostgreSQL91Dialect;

public class PostgreSQL92Dialect
extends PostgreSQL91Dialect {
    public PostgreSQL92Dialect() {
        this.registerColumnType(2000, "json");
    }

    @Override
    public boolean supportsIfExistsAfterAlterTable() {
        return true;
    }
}

