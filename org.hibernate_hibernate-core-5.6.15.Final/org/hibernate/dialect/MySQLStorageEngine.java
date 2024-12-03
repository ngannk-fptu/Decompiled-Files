/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

public interface MySQLStorageEngine {
    public boolean supportsCascadeDelete();

    public String getTableTypeString(String var1);

    public boolean hasSelfReferentialForeignKeyBug();

    public boolean dropConstraints();
}

