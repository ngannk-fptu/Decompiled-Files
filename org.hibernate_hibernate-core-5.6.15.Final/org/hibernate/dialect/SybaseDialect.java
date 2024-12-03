/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.hibernate.dialect.AbstractTransactSQLDialect;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class SybaseDialect
extends AbstractTransactSQLDialect {
    private static final int PARAM_LIST_SIZE_LIMIT = 250000;

    @Override
    public int getInExpressionCountLimit() {
        return 250000;
    }

    @Override
    protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        switch (sqlCode) {
            case 2004: {
                return BlobTypeDescriptor.PRIMITIVE_ARRAY_BINDING;
            }
            case 2005: {
                return ClobTypeDescriptor.STREAM_BINDING_EXTRACTING;
            }
        }
        return super.getSqlTypeDescriptorOverride(sqlCode);
    }

    @Override
    public String getNullColumnString() {
        return " null";
    }

    @Override
    public boolean canCreateSchema() {
        return false;
    }

    @Override
    public String getCurrentSchemaCommand() {
        return "select db_name()";
    }

    @Override
    public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData) throws SQLException {
        if (dbMetaData == null) {
            builder.setUnquotedCaseStrategy(IdentifierCaseStrategy.MIXED);
            builder.setQuotedCaseStrategy(IdentifierCaseStrategy.MIXED);
        }
        return super.buildIdentifierHelper(builder, dbMetaData);
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.CATALOG;
    }
}

