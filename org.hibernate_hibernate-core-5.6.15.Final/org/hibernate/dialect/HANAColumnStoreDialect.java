/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.type.StandardBasicTypes;

public class HANAColumnStoreDialect
extends AbstractHANADialect {
    public HANAColumnStoreDialect() {
        this.registerFunction("score", new StandardSQLFunction("score", StandardBasicTypes.DOUBLE));
        this.registerFunction("snippets", new StandardSQLFunction("snippets"));
        this.registerFunction("highlighted", new StandardSQLFunction("highlighted"));
        this.registerFunction("contains", new VarArgsSQLFunction(StandardBasicTypes.BOOLEAN, "contains(", ",", ") /*"));
        this.registerFunction("contains_rhs", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "*/"));
        this.registerFunction("not_contains", new VarArgsSQLFunction(StandardBasicTypes.BOOLEAN, "not contains(", ",", ") /*"));
    }

    @Override
    public String getCreateTableString() {
        return "create column table";
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new GlobalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String getCreateIdTableCommand() {
                return "create global temporary column table";
            }

            @Override
            public String getTruncateIdTableCommand() {
                return "truncate table";
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

