/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;

public class HANARowStoreDialect
extends AbstractHANADialect {
    @Override
    public String getCreateTableString() {
        return "create row table";
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new GlobalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String getCreateIdTableCommand() {
                return "create global temporary row table";
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "on commit delete rows";
            }
        }, AfterUseAction.CLEAN);
    }

    @Override
    protected boolean supportsAsciiStringTypes() {
        return true;
    }

    @Override
    protected Boolean useUnicodeStringTypesDefault() {
        return Boolean.FALSE;
    }
}

