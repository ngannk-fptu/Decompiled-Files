/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.function.DB2SubstringFunction;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.type.descriptor.sql.CharTypeDescriptor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class DB297Dialect
extends DB2Dialect {
    public DB297Dialect() {
        this.registerFunction("substring", new DB2SubstringFunction());
    }

    @Override
    public String getCrossJoinSeparator() {
        return " cross join ";
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new GlobalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String generateIdTableName(String baseName) {
                return super.generateIdTableName(baseName);
            }

            @Override
            public String getCreateIdTableCommand() {
                return "create global temporary table";
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "not logged";
            }
        }, AfterUseAction.CLEAN);
    }

    @Override
    protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        switch (sqlCode) {
            case -15: {
                return CharTypeDescriptor.INSTANCE;
            }
            case 2011: {
                if (this.useInputStreamToInsertBlob()) {
                    return ClobTypeDescriptor.STREAM_BINDING;
                }
                return ClobTypeDescriptor.CLOB_BINDING;
            }
            case -9: {
                return VarcharTypeDescriptor.INSTANCE;
            }
        }
        return super.getSqlTypeDescriptorOverride(sqlCode);
    }
}

