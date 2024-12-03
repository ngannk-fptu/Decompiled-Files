/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.PostgresUUIDType;

public class PostgreSQL82Dialect
extends PostgreSQL81Dialect {
    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        typeContributions.contributeType(PostgresUUIDType.INSTANCE);
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String getCreateIdTableCommand() {
                return "create temporary  table";
            }

            @Override
            public String getDropIdTableCommand() {
                return "drop table";
            }
        }, AfterUseAction.DROP, null);
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence if exists " + sequenceName;
    }

    @Override
    public boolean supportsValuesList() {
        return true;
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return true;
    }
}

