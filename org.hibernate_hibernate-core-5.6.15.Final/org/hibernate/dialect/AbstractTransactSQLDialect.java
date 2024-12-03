/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.CharIndexFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.AbstractTransactSQLIdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.type.StandardBasicTypes;

abstract class AbstractTransactSQLDialect
extends Dialect {
    public AbstractTransactSQLDialect() {
        this.registerColumnType(-2, "binary($l)");
        this.registerColumnType(-7, "tinyint");
        this.registerColumnType(-5, "numeric(19,0)");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "smallint");
        this.registerColumnType(4, "int");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(6, "float");
        this.registerColumnType(8, "double precision");
        this.registerColumnType(91, "datetime");
        this.registerColumnType(92, "datetime");
        this.registerColumnType(93, "datetime");
        this.registerColumnType(-3, "varbinary($l)");
        this.registerColumnType(2, "numeric($p,$s)");
        this.registerColumnType(2004, "image");
        this.registerColumnType(2005, "text");
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("char", new StandardSQLFunction("char", StandardBasicTypes.CHARACTER));
        this.registerFunction("len", new StandardSQLFunction("len", StandardBasicTypes.LONG));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("str", new StandardSQLFunction("str", StandardBasicTypes.STRING));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim"));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim"));
        this.registerFunction("reverse", new StandardSQLFunction("reverse"));
        this.registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING));
        this.registerFunction("user", new NoArgSQLFunction("user", StandardBasicTypes.STRING));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("current_time", new NoArgSQLFunction("getdate", StandardBasicTypes.TIME));
        this.registerFunction("current_date", new NoArgSQLFunction("getdate", StandardBasicTypes.DATE));
        this.registerFunction("getdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("getutcdate", new NoArgSQLFunction("getutcdate", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("day", new StandardSQLFunction("day", StandardBasicTypes.INTEGER));
        this.registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER));
        this.registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));
        this.registerFunction("datename", new StandardSQLFunction("datename", StandardBasicTypes.STRING));
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));
        this.registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE));
        this.registerFunction("square", new StandardSQLFunction("square"));
        this.registerFunction("rand", new StandardSQLFunction("rand", StandardBasicTypes.FLOAT));
        this.registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        this.registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("ceiling", new StandardSQLFunction("ceiling"));
        this.registerFunction("floor", new StandardSQLFunction("floor"));
        this.registerFunction("isnull", new StandardSQLFunction("isnull"));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "+", ")"));
        this.registerFunction("length", new StandardSQLFunction("len", StandardBasicTypes.INTEGER));
        this.registerFunction("trim", new SQLFunctionTemplate(StandardBasicTypes.STRING, "ltrim(rtrim(?1))"));
        this.registerFunction("locate", new CharIndexFunction());
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "0");
    }

    @Override
    public String getAddColumnString() {
        return "add";
    }

    @Override
    public String getNullColumnString() {
        return "";
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }

    @Override
    public String getForUpdateString() {
        return "";
    }

    @Override
    public String appendLockHint(LockOptions lockOptions, String tableName) {
        return lockOptions.getLockMode().greaterThan(LockMode.READ) ? tableName + " holdlock" : tableName;
    }

    @Override
    public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
        Iterator<Map.Entry<String, LockMode>> itr = aliasedLockOptions.getAliasLockIterator();
        StringBuilder buffer = new StringBuilder(sql);
        while (itr.hasNext()) {
            Map.Entry<String, LockMode> entry = itr.next();
            LockMode lockMode = entry.getValue();
            if (!lockMode.greaterThan(LockMode.READ)) continue;
            String alias = entry.getKey();
            int start = -1;
            int end = -1;
            if (sql.endsWith(" " + alias)) {
                start = buffer.length() - alias.length();
                end = start + alias.length();
            } else {
                int position = buffer.indexOf(" " + alias + " ");
                if (position <= -1) {
                    position = buffer.indexOf(" " + alias + ",");
                }
                if (position > -1) {
                    start = position + 1;
                    end = start + alias.length();
                }
            }
            if (start <= -1) continue;
            String lockHint = this.appendLockHint(aliasedLockOptions, alias);
            buffer.replace(start, end, lockHint);
        }
        return buffer.toString();
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
        return col;
    }

    @Override
    public ResultSet getResultSet(CallableStatement ps) throws SQLException {
        boolean isResultSet = ps.execute();
        while (!isResultSet && ps.getUpdateCount() != -1) {
            isResultSet = ps.getMoreResults();
        }
        return ps.getResultSet();
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select getdate()";
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String generateIdTableName(String baseName) {
                return "#" + baseName;
            }
        }, AfterUseAction.DROP, TempTableDdlTransactionHandling.NONE);
    }

    @Override
    public String getSelectGUIDString() {
        return "select newid()";
    }

    @Override
    public boolean supportsEmptyInList() {
        return false;
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public boolean supportsExistsInSelect() {
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
    public boolean supportsTupleDistinctCounts() {
        return false;
    }

    @Override
    public boolean supportsTuplesInSubqueries() {
        return false;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new AbstractTransactSQLIdentityColumnSupport();
    }

    @Override
    public boolean supportsPartitionBy() {
        return true;
    }
}

