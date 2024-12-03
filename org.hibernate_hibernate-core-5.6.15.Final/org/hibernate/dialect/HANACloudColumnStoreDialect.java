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

public class HANACloudColumnStoreDialect
extends AbstractHANADialect {
    public HANACloudColumnStoreDialect() {
        this.registerColumnType(1, "nvarchar(1)");
        this.registerColumnType(12, 5000L, "nvarchar($l)");
        this.registerColumnType(-1, 5000L, "nvarchar($l)");
        this.registerColumnType(-1, "nclob");
        this.registerColumnType(12, "nclob");
        this.registerColumnType(2005, "nclob");
        this.registerHibernateType(2005, StandardBasicTypes.MATERIALIZED_NCLOB.getName());
        this.registerHibernateType(-15, StandardBasicTypes.NSTRING.getName());
        this.registerHibernateType(1, StandardBasicTypes.CHARACTER.getName());
        this.registerHibernateType(1, 1L, StandardBasicTypes.CHARACTER.getName());
        this.registerHibernateType(1, 5000L, StandardBasicTypes.NSTRING.getName());
        this.registerHibernateType(12, StandardBasicTypes.NSTRING.getName());
        this.registerHibernateType(-1, StandardBasicTypes.NTEXT.getName());
        this.registerHanaCloudKeywords();
        this.registerFunction("score", new StandardSQLFunction("score", StandardBasicTypes.DOUBLE));
        this.registerFunction("contains", new VarArgsSQLFunction(StandardBasicTypes.BOOLEAN, "contains(", ",", ") /*"));
        this.registerFunction("contains_rhs", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "*/"));
        this.registerFunction("not_contains", new VarArgsSQLFunction(StandardBasicTypes.BOOLEAN, "not contains(", ",", ") /*"));
    }

    private void registerHanaCloudKeywords() {
        this.registerKeyword("array");
        this.registerKeyword("at");
        this.registerKeyword("authorization");
        this.registerKeyword("between");
        this.registerKeyword("by");
        this.registerKeyword("collate");
        this.registerKeyword("empty");
        this.registerKeyword("filter");
        this.registerKeyword("grouping");
        this.registerKeyword("no");
        this.registerKeyword("not");
        this.registerKeyword("of");
        this.registerKeyword("over");
        this.registerKeyword("recursive");
        this.registerKeyword("row");
        this.registerKeyword("table");
        this.registerKeyword("to");
        this.registerKeyword("window");
        this.registerKeyword("within");
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
        return false;
    }

    @Override
    protected Boolean useUnicodeStringTypesDefault() {
        return Boolean.TRUE;
    }

    @Override
    public boolean isUseUnicodeStringTypes() {
        return true;
    }
}

