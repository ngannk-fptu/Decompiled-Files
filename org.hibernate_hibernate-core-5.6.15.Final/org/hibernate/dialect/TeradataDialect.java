/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.hql.spi.id.IdTableSupport;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.type.StandardBasicTypes;

public class TeradataDialect
extends Dialect
implements IdTableSupport {
    private static final int PARAM_LIST_SIZE_LIMIT = 1024;

    public TeradataDialect() {
        this.registerColumnType(2, "NUMERIC($p,$s)");
        this.registerColumnType(8, "DOUBLE PRECISION");
        this.registerColumnType(-5, "NUMERIC(18,0)");
        this.registerColumnType(-7, "BYTEINT");
        this.registerColumnType(-6, "BYTEINT");
        this.registerColumnType(-3, "VARBYTE($l)");
        this.registerColumnType(-2, "BYTEINT");
        this.registerColumnType(-1, "LONG VARCHAR");
        this.registerColumnType(1, "CHAR(1)");
        this.registerColumnType(3, "DECIMAL");
        this.registerColumnType(4, "INTEGER");
        this.registerColumnType(5, "SMALLINT");
        this.registerColumnType(6, "FLOAT");
        this.registerColumnType(12, "VARCHAR($l)");
        this.registerColumnType(91, "DATE");
        this.registerColumnType(92, "TIME");
        this.registerColumnType(93, "TIMESTAMP");
        this.registerColumnType(16, "BYTEINT");
        this.registerColumnType(2004, "BLOB");
        this.registerColumnType(2005, "CLOB");
        this.registerFunction("year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(year from ?1)"));
        this.registerFunction("length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "character_length(?1)"));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "||", ")"));
        this.registerFunction("substring", new SQLFunctionTemplate(StandardBasicTypes.STRING, "substring(?1 from ?2 for ?3)"));
        this.registerFunction("locate", new SQLFunctionTemplate(StandardBasicTypes.STRING, "position(?1 in ?2)"));
        this.registerFunction("mod", new SQLFunctionTemplate(StandardBasicTypes.STRING, "?1 mod ?2"));
        this.registerFunction("str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar(255))"));
        this.registerFunction("bit_length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "octet_length(cast(?1 as char))*4"));
        this.registerFunction("current_timestamp", new SQLFunctionTemplate(StandardBasicTypes.TIMESTAMP, "current_timestamp"));
        this.registerFunction("current_time", new SQLFunctionTemplate(StandardBasicTypes.TIMESTAMP, "current_time"));
        this.registerFunction("current_date", new SQLFunctionTemplate(StandardBasicTypes.TIMESTAMP, "current_date"));
        this.registerKeyword("password");
        this.registerKeyword("type");
        this.registerKeyword("title");
        this.registerKeyword("year");
        this.registerKeyword("month");
        this.registerKeyword("summary");
        this.registerKeyword("alias");
        this.registerKeyword("value");
        this.registerKeyword("first");
        this.registerKeyword("role");
        this.registerKeyword("account");
        this.registerKeyword("class");
        this.getDefaultProperties().setProperty("hibernate.jdbc.use_streams_for_binary", "false");
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "0");
    }

    @Override
    public String getForUpdateString() {
        return "";
    }

    @Override
    public boolean supportsSequences() {
        return false;
    }

    @Override
    public String getAddColumnString() {
        return "Add Column";
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new GlobalTemporaryTableBulkIdStrategy(this, AfterUseAction.CLEAN);
    }

    @Override
    public String generateIdTableName(String baseName) {
        return IdTableSupportStandardImpl.INSTANCE.generateIdTableName(baseName);
    }

    @Override
    public String getCreateIdTableCommand() {
        return "create global temporary table";
    }

    @Override
    public String getCreateIdTableStatementOptions() {
        return " on commit preserve rows";
    }

    @Override
    public String getDropIdTableCommand() {
        return "drop table";
    }

    @Override
    public String getTruncateIdTableCommand() {
        return "delete from";
    }

    public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
        int p;
        float f = precision > 0 ? (float)scale / (float)precision : 0.0f;
        int n = p = precision > 18 ? 18 : precision;
        int s = precision > 18 ? (int)(18.0 * (double)f) : (scale > 18 ? 18 : scale);
        return super.getTypeName(code, length, p, s);
    }

    @Override
    public boolean supportsCascadeDelete() {
        return false;
    }

    @Override
    public boolean supportsCircularCascadeDeleteConstraints() {
        return false;
    }

    @Override
    public boolean areStringComparisonsCaseInsensitive() {
        return true;
    }

    @Override
    public boolean supportsEmptyInList() {
        return false;
    }

    @Override
    public String getSelectClauseNullString(int sqlType) {
        String v = "null";
        switch (sqlType) {
            case -7: 
            case -6: 
            case -5: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                v = "cast(null as decimal)";
                break;
            }
            case -1: 
            case 1: 
            case 12: {
                v = "cast(null as varchar(255))";
                break;
            }
            case 91: 
            case 92: 
            case 93: {
                v = "cast(null as timestamp)";
                break;
            }
        }
        return v;
    }

    @Override
    public String getCreateMultisetTableString() {
        return "create multiset table ";
    }

    @Override
    public boolean supportsLobValueChangePropogation() {
        return false;
    }

    @Override
    public boolean doesReadCommittedCauseWritersToBlockReaders() {
        return true;
    }

    @Override
    public boolean doesRepeatableReadCauseReadersToBlockWriters() {
        return true;
    }

    @Override
    public boolean supportsBindAsCallableArgument() {
        return false;
    }

    @Override
    public int getInExpressionCountLimit() {
        return 1024;
    }
}

