/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MySQLStorageEngine;

public class InnoDBStorageEngine
implements MySQLStorageEngine {
    public static final MySQLStorageEngine INSTANCE = new InnoDBStorageEngine();

    @Override
    public boolean supportsCascadeDelete() {
        return true;
    }

    @Override
    public String getTableTypeString(String engineKeyword) {
        return String.format(" %s=InnoDB", engineKeyword);
    }

    @Override
    public boolean hasSelfReferentialForeignKeyBug() {
        return true;
    }

    @Override
    public boolean dropConstraints() {
        return true;
    }
}

