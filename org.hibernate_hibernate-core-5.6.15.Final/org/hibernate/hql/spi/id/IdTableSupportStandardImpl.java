/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id;

import org.hibernate.hql.spi.id.IdTableSupport;

public class IdTableSupportStandardImpl
implements IdTableSupport {
    public static final IdTableSupportStandardImpl INSTANCE = new IdTableSupportStandardImpl();

    @Override
    public String generateIdTableName(String baseName) {
        return "HT_" + baseName;
    }

    @Override
    public String getCreateIdTableCommand() {
        return "create table";
    }

    @Override
    public String getCreateIdTableStatementOptions() {
        return null;
    }

    @Override
    public String getDropIdTableCommand() {
        return "drop table";
    }

    @Override
    public String getTruncateIdTableCommand() {
        return "delete from";
    }
}

