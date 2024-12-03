/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.local;

import org.hibernate.hql.spi.id.IdTableInfo;

public class IdTableInfoImpl
implements IdTableInfo {
    private final String idTableName;
    private final String creationStatement;
    private final String dropStatement;

    public IdTableInfoImpl(String idTableName, String creationStatement, String dropStatement) {
        this.idTableName = idTableName;
        this.creationStatement = creationStatement;
        this.dropStatement = dropStatement;
    }

    @Override
    public String getQualifiedIdTableName() {
        return this.idTableName;
    }

    public String getIdTableCreationStatement() {
        return this.creationStatement;
    }

    public String getIdTableDropStatement() {
        return this.dropStatement;
    }
}

