/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MySQLStorageEngine;

public class MyISAMStorageEngine
implements MySQLStorageEngine {
    public static final MySQLStorageEngine INSTANCE = new MyISAMStorageEngine();

    @Override
    public boolean supportsCascadeDelete() {
        return false;
    }

    @Override
    public String getTableTypeString(String engineKeyword) {
        return String.format(" %s=MyISAM", engineKeyword);
    }

    @Override
    public boolean hasSelfReferentialForeignKeyBug() {
        return false;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }
}

