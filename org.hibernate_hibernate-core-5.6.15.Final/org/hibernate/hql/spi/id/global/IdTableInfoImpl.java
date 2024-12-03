/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.global;

import org.hibernate.hql.spi.id.IdTableInfo;

class IdTableInfoImpl
implements IdTableInfo {
    private final String idTableName;

    public IdTableInfoImpl(String idTableName) {
        this.idTableName = idTableName;
    }

    @Override
    public String getQualifiedIdTableName() {
        return this.idTableName;
    }
}

