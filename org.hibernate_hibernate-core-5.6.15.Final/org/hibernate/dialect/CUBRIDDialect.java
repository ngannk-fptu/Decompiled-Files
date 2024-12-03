/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.CUBRIDIdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.pagination.CUBRIDLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorCUBRIDDatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;

public class CUBRIDDialect
extends Dialect {
    public CUBRIDDialect() {
        this.registerColumnType(-5, "bigint");
        this.registerColumnType(-7, "bit(8)");
        this.registerColumnType(2004, "bit varying(65535)");
        this.registerColumnType(16, "bit(8)");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(2005, "string");
        this.registerColumnType(91, "date");
        this.registerColumnType(3, "decimal");
        this.registerColumnType(8, "double");
        this.registerColumnType(6, "float");
        this.registerColumnType(4, "int");
        this.registerColumnType(2, "numeric($p,$s)");
        this.registerColumnType(7, "double");
        this.registerColumnType(5, "short");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(-6, "short");
        this.registerColumnType(-3, 2000L, "bit varying($l)");
        this.registerColumnType(12, "string");
        this.registerColumnType(12, 2000L, "varchar($l)");
        this.registerColumnType(12, 255L, "varchar($l)");
        this.getDefaultProperties().setProperty("hibernate.jdbc.use_streams_for_binary", "true");
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("bin", new StandardSQLFunction("bin", StandardBasicTypes.STRING));
        this.registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG));
        this.registerFunction("character_length", new StandardSQLFunction("character_length", StandardBasicTypes.LONG));
        this.registerFunction("lengthb", new StandardSQLFunction("lengthb", StandardBasicTypes.LONG));
        this.registerFunction("lengthh", new StandardSQLFunction("lengthh", StandardBasicTypes.LONG));
        this.registerFunction("lcase", new StandardSQLFunction("lcase"));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim"));
        this.registerFunction("reverse", new StandardSQLFunction("reverse"));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim"));
        this.registerFunction("trim", new StandardSQLFunction("trim"));
        this.registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING));
        this.registerFunction("ucase", new StandardSQLFunction("ucase"));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("log2", new StandardSQLFunction("log2", StandardBasicTypes.DOUBLE));
        this.registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE));
        this.registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE));
        this.registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE));
        this.registerFunction("random", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        this.registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        this.registerFunction("ceil", new StandardSQLFunction("ceil", StandardBasicTypes.INTEGER));
        this.registerFunction("floor", new StandardSQLFunction("floor", StandardBasicTypes.INTEGER));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("datediff", new StandardSQLFunction("datediff", StandardBasicTypes.INTEGER));
        this.registerFunction("timediff", new StandardSQLFunction("timediff", StandardBasicTypes.TIME));
        this.registerFunction("date", new StandardSQLFunction("date", StandardBasicTypes.DATE));
        this.registerFunction("curdate", new NoArgSQLFunction("curdate", StandardBasicTypes.DATE));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.registerFunction("sys_date", new NoArgSQLFunction("sys_date", StandardBasicTypes.DATE, false));
        this.registerFunction("sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false));
        this.registerFunction("time", new StandardSQLFunction("time", StandardBasicTypes.TIME));
        this.registerFunction("curtime", new NoArgSQLFunction("curtime", StandardBasicTypes.TIME));
        this.registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false));
        this.registerFunction("sys_time", new NoArgSQLFunction("sys_time", StandardBasicTypes.TIME, false));
        this.registerFunction("systime", new NoArgSQLFunction("systime", StandardBasicTypes.TIME, false));
        this.registerFunction("timestamp", new StandardSQLFunction("timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("sys_timestamp", new NoArgSQLFunction("sys_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("day", new StandardSQLFunction("day", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("from_days", new StandardSQLFunction("from_days", StandardBasicTypes.DATE));
        this.registerFunction("from_unixtime", new StandardSQLFunction("from_unixtime", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE));
        this.registerFunction("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER));
        this.registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER));
        this.registerFunction("months_between", new StandardSQLFunction("months_between", StandardBasicTypes.DOUBLE));
        this.registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.INTEGER));
        this.registerFunction("second", new StandardSQLFunction("second", StandardBasicTypes.INTEGER));
        this.registerFunction("sec_to_time", new StandardSQLFunction("sec_to_time", StandardBasicTypes.TIME));
        this.registerFunction("time_to_sec", new StandardSQLFunction("time_to_sec", StandardBasicTypes.INTEGER));
        this.registerFunction("to_days", new StandardSQLFunction("to_days", StandardBasicTypes.LONG));
        this.registerFunction("unix_timestamp", new StandardSQLFunction("unix_timestamp", StandardBasicTypes.LONG));
        this.registerFunction("utc_date", new NoArgSQLFunction("utc_date", StandardBasicTypes.STRING));
        this.registerFunction("utc_time", new NoArgSQLFunction("utc_time", StandardBasicTypes.STRING));
        this.registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));
        this.registerFunction("weekday", new StandardSQLFunction("weekday", StandardBasicTypes.INTEGER));
        this.registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));
        this.registerFunction("hex", new StandardSQLFunction("hex", StandardBasicTypes.STRING));
        this.registerFunction("octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG));
        this.registerFunction("bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG));
        this.registerFunction("bit_count", new StandardSQLFunction("bit_count", StandardBasicTypes.LONG));
        this.registerFunction("md5", new StandardSQLFunction("md5", StandardBasicTypes.STRING));
        this.registerFunction("concat", new StandardSQLFunction("concat", StandardBasicTypes.STRING));
        this.registerFunction("substring", new StandardSQLFunction("substring", StandardBasicTypes.STRING));
        this.registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("length", new StandardSQLFunction("length", StandardBasicTypes.INTEGER));
        this.registerFunction("bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.INTEGER));
        this.registerFunction("coalesce", new StandardSQLFunction("coalesce"));
        this.registerFunction("nullif", new StandardSQLFunction("nullif"));
        this.registerFunction("mod", new StandardSQLFunction("mod"));
        this.registerFunction("power", new StandardSQLFunction("power"));
        this.registerFunction("stddev", new StandardSQLFunction("stddev"));
        this.registerFunction("variance", new StandardSQLFunction("variance"));
        this.registerFunction("trunc", new StandardSQLFunction("trunc"));
        this.registerFunction("nvl", new StandardSQLFunction("nvl"));
        this.registerFunction("nvl2", new StandardSQLFunction("nvl2"));
        this.registerFunction("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));
        this.registerFunction("to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING));
        this.registerFunction("to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("instr", new StandardSQLFunction("instr", StandardBasicTypes.INTEGER));
        this.registerFunction("instrb", new StandardSQLFunction("instrb", StandardBasicTypes.INTEGER));
        this.registerFunction("lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING));
        this.registerFunction("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));
        this.registerFunction("rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING));
        this.registerFunction("translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING));
        this.registerFunction("add_months", new StandardSQLFunction("add_months", StandardBasicTypes.DATE));
        this.registerFunction("user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false));
        this.registerFunction("rownum", new NoArgSQLFunction("rownum", StandardBasicTypes.LONG, false));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
        this.registerKeyword("TYPE");
        this.registerKeyword("YEAR");
        this.registerKeyword("MONTH");
        this.registerKeyword("ALIAS");
        this.registerKeyword("VALUE");
        this.registerKeyword("FIRST");
        this.registerKeyword("ROLE");
        this.registerKeyword("CLASS");
        this.registerKeyword("BIT");
        this.registerKeyword("TIME");
        this.registerKeyword("QUERY");
        this.registerKeyword("DATE");
        this.registerKeyword("USER");
        this.registerKeyword("ACTION");
        this.registerKeyword("SYS_USER");
        this.registerKeyword("ZONE");
        this.registerKeyword("LANGUAGE");
        this.registerKeyword("DICTIONARY");
        this.registerKeyword("DATA");
        this.registerKeyword("TEST");
        this.registerKeyword("SUPERCLASS");
        this.registerKeyword("SECTION");
        this.registerKeyword("LOWER");
        this.registerKeyword("LIST");
        this.registerKeyword("OID");
        this.registerKeyword("DAY");
        this.registerKeyword("IF");
        this.registerKeyword("ATTRIBUTE");
        this.registerKeyword("STRING");
        this.registerKeyword("SEARCH");
    }

    @Override
    public boolean supportsColumnCheck() {
        return false;
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    public String getAddColumnString() {
        return "add";
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + sequenceName + ".next_value from table({1}) as T(X)";
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create serial " + sequenceName;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop serial " + sequenceName;
    }

    @Override
    public String getDropForeignKeyString() {
        return " drop foreign key ";
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsExistsInSelect() {
        return false;
    }

    @Override
    public String getQuerySequencesString() {
        return "select * from db_serial";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return SequenceInformationExtractorCUBRIDDatabaseImpl.INSTANCE;
    }

    @Override
    public char openQuote() {
        return '[';
    }

    @Override
    public char closeQuote() {
        return ']';
    }

    @Override
    public String getForUpdateString() {
        return " ";
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select now()";
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @Override
    public boolean supportsEmptyInList() {
        return false;
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public boolean supportsTupleDistinctCounts() {
        return false;
    }

    @Override
    public LimitHandler getLimitHandler() {
        return CUBRIDLimitHandler.INSTANCE;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new CUBRIDIdentityColumnSupport();
    }

    @Override
    public boolean supportsPartitionBy() {
        return true;
    }
}

