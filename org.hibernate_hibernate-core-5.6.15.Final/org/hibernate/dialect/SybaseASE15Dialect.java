/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.SybaseDialect;
import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.TinyIntTypeDescriptor;

public class SybaseASE15Dialect
extends SybaseDialect {
    public SybaseASE15Dialect() {
        this.registerColumnType(-4, "image");
        this.registerColumnType(-1, "text");
        this.registerColumnType(-5, "bigint");
        this.registerColumnType(91, "date");
        this.registerColumnType(3, "numeric($p,$s)");
        this.registerColumnType(92, "time");
        this.registerColumnType(7, "real");
        this.registerColumnType(16, "tinyint");
        this.registerFunction("second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datepart(second, ?1)"));
        this.registerFunction("minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datepart(minute, ?1)"));
        this.registerFunction("hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datepart(hour, ?1)"));
        this.registerFunction("extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datepart(?1, ?3)"));
        this.registerFunction("mod", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1 % ?2"));
        this.registerFunction("bit_length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datalength(?1) * 8"));
        this.registerFunction("trim", new AnsiTrimEmulationFunction("ltrim", "rtrim", "str_replace"));
        this.registerFunction("atan2", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "atn2(?1, ?2)"));
        this.registerFunction("atn2", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "atn2(?1, ?2)"));
        this.registerFunction("biginttohex", new SQLFunctionTemplate(StandardBasicTypes.STRING, "biginttohext(?1)"));
        this.registerFunction("char_length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "char_length(?1)"));
        this.registerFunction("charindex", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "charindex(?1, ?2)"));
        this.registerFunction("coalesce", new VarArgsSQLFunction("coalesce(", ",", ")"));
        this.registerFunction("col_length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "col_length(?1, ?2)"));
        this.registerFunction("col_name", new SQLFunctionTemplate(StandardBasicTypes.STRING, "col_name(?1, ?2)"));
        this.registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE));
        this.registerFunction("data_pages", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "data_pages(?1, ?2)"));
        this.registerFunction("data_pages", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "data_pages(?1, ?2, ?3)"));
        this.registerFunction("data_pages", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "data_pages(?1, ?2, ?3, ?4)"));
        this.registerFunction("datalength", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datalength(?1)"));
        this.registerFunction("dateadd", new SQLFunctionTemplate(StandardBasicTypes.TIMESTAMP, "dateadd(?1, ?2, ?3)"));
        this.registerFunction("datediff", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datediff(?1, ?2, ?3)"));
        this.registerFunction("datepart", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datepart(?1, ?2)"));
        this.registerFunction("datetime", new SQLFunctionTemplate(StandardBasicTypes.TIMESTAMP, "datetime"));
        this.registerFunction("db_id", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "db_id(?1)"));
        this.registerFunction("difference", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "difference(?1,?2)"));
        this.registerFunction("db_name", new SQLFunctionTemplate(StandardBasicTypes.STRING, "db_name(?1)"));
        this.registerFunction("has_role", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "has_role(?1, ?2)"));
        this.registerFunction("hextobigint", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "hextobigint(?1)"));
        this.registerFunction("hextoint", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "hextoint(?1)"));
        this.registerFunction("host_id", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "host_id"));
        this.registerFunction("host_name", new SQLFunctionTemplate(StandardBasicTypes.STRING, "host_name"));
        this.registerFunction("inttohex", new SQLFunctionTemplate(StandardBasicTypes.STRING, "inttohex(?1)"));
        this.registerFunction("is_quiesced", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "is_quiesced(?1)"));
        this.registerFunction("is_sec_service_on", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "is_sec_service_on(?1)"));
        this.registerFunction("object_id", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "object_id(?1)"));
        this.registerFunction("object_name", new SQLFunctionTemplate(StandardBasicTypes.STRING, "object_name(?1)"));
        this.registerFunction("pagesize", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "pagesize(?1)"));
        this.registerFunction("pagesize", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "pagesize(?1, ?2)"));
        this.registerFunction("pagesize", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "pagesize(?1, ?2, ?3)"));
        this.registerFunction("partition_id", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "partition_id(?1, ?2)"));
        this.registerFunction("partition_id", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "partition_id(?1, ?2, ?3)"));
        this.registerFunction("partition_name", new SQLFunctionTemplate(StandardBasicTypes.STRING, "partition_name(?1, ?2)"));
        this.registerFunction("partition_name", new SQLFunctionTemplate(StandardBasicTypes.STRING, "partition_name(?1, ?2, ?3)"));
        this.registerFunction("patindex", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "patindex"));
        this.registerFunction("proc_role", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "proc_role"));
        this.registerFunction("role_name", new SQLFunctionTemplate(StandardBasicTypes.STRING, "role_name"));
        this.registerFunction("row_count", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "row_count"));
        this.registerFunction("rand2", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "rand2(?1)"));
        this.registerFunction("rand2", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "rand2"));
        this.registerFunction("replicate", new SQLFunctionTemplate(StandardBasicTypes.STRING, "replicate(?1,?2)"));
        this.registerFunction("role_contain", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "role_contain"));
        this.registerFunction("role_id", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "role_id"));
        this.registerFunction("reserved_pages", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "reserved_pages"));
        this.registerFunction("right", new SQLFunctionTemplate(StandardBasicTypes.STRING, "right"));
        this.registerFunction("show_role", new SQLFunctionTemplate(StandardBasicTypes.STRING, "show_role"));
        this.registerFunction("show_sec_services", new SQLFunctionTemplate(StandardBasicTypes.STRING, "show_sec_services"));
        this.registerFunction("sortkey", new VarArgsSQLFunction(StandardBasicTypes.BINARY, "sortkey(", ",", ")"));
        this.registerFunction("soundex", new SQLFunctionTemplate(StandardBasicTypes.STRING, "sounded"));
        this.registerFunction("stddev", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "stddev"));
        this.registerFunction("stddev_pop", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "stddev_pop"));
        this.registerFunction("stddev_samp", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "stddev_samp"));
        this.registerFunction("stuff", new SQLFunctionTemplate(StandardBasicTypes.STRING, "stuff"));
        this.registerFunction("substring", new VarArgsSQLFunction(StandardBasicTypes.STRING, "substring(", ",", ")"));
        this.registerFunction("suser_id", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "suser_id"));
        this.registerFunction("suser_name", new SQLFunctionTemplate(StandardBasicTypes.STRING, "suser_name"));
        this.registerFunction("tempdb_id", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "tempdb_id"));
        this.registerFunction("textvalid", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "textvalid"));
        this.registerFunction("to_unichar", new SQLFunctionTemplate(StandardBasicTypes.STRING, "to_unichar(?1)"));
        this.registerFunction("tran_dumptable_status", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "ran_dumptable_status(?1)"));
        this.registerFunction("uhighsurr", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "uhighsurr"));
        this.registerFunction("ulowsurr", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "ulowsurr"));
        this.registerFunction("uscalar", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "uscalar"));
        this.registerFunction("used_pages", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "used_pages"));
        this.registerFunction("user_id", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "user_id"));
        this.registerFunction("user_name", new SQLFunctionTemplate(StandardBasicTypes.STRING, "user_name"));
        this.registerFunction("valid_name", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "valid_name"));
        this.registerFunction("valid_user", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "valid_user"));
        this.registerFunction("variance", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "variance"));
        this.registerFunction("var_pop", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "var_pop"));
        this.registerFunction("var_samp", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "var_samp"));
        this.registerFunction("sysdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP));
        this.registerSybaseKeywords();
    }

    private void registerSybaseKeywords() {
        this.registerKeyword("add");
        this.registerKeyword("all");
        this.registerKeyword("alter");
        this.registerKeyword("and");
        this.registerKeyword("any");
        this.registerKeyword("arith_overflow");
        this.registerKeyword("as");
        this.registerKeyword("asc");
        this.registerKeyword("at");
        this.registerKeyword("authorization");
        this.registerKeyword("avg");
        this.registerKeyword("begin");
        this.registerKeyword("between");
        this.registerKeyword("break");
        this.registerKeyword("browse");
        this.registerKeyword("bulk");
        this.registerKeyword("by");
        this.registerKeyword("cascade");
        this.registerKeyword("case");
        this.registerKeyword("char_convert");
        this.registerKeyword("check");
        this.registerKeyword("checkpoint");
        this.registerKeyword("close");
        this.registerKeyword("clustered");
        this.registerKeyword("coalesce");
        this.registerKeyword("commit");
        this.registerKeyword("compute");
        this.registerKeyword("confirm");
        this.registerKeyword("connect");
        this.registerKeyword("constraint");
        this.registerKeyword("continue");
        this.registerKeyword("controlrow");
        this.registerKeyword("convert");
        this.registerKeyword("count");
        this.registerKeyword("count_big");
        this.registerKeyword("create");
        this.registerKeyword("current");
        this.registerKeyword("cursor");
        this.registerKeyword("database");
        this.registerKeyword("dbcc");
        this.registerKeyword("deallocate");
        this.registerKeyword("declare");
        this.registerKeyword("decrypt");
        this.registerKeyword("default");
        this.registerKeyword("delete");
        this.registerKeyword("desc");
        this.registerKeyword("determnistic");
        this.registerKeyword("disk");
        this.registerKeyword("distinct");
        this.registerKeyword("drop");
        this.registerKeyword("dummy");
        this.registerKeyword("dump");
        this.registerKeyword("else");
        this.registerKeyword("encrypt");
        this.registerKeyword("end");
        this.registerKeyword("endtran");
        this.registerKeyword("errlvl");
        this.registerKeyword("errordata");
        this.registerKeyword("errorexit");
        this.registerKeyword("escape");
        this.registerKeyword("except");
        this.registerKeyword("exclusive");
        this.registerKeyword("exec");
        this.registerKeyword("execute");
        this.registerKeyword("exist");
        this.registerKeyword("exit");
        this.registerKeyword("exp_row_size");
        this.registerKeyword("external");
        this.registerKeyword("fetch");
        this.registerKeyword("fillfactor");
        this.registerKeyword("for");
        this.registerKeyword("foreign");
        this.registerKeyword("from");
        this.registerKeyword("goto");
        this.registerKeyword("grant");
        this.registerKeyword("group");
        this.registerKeyword("having");
        this.registerKeyword("holdlock");
        this.registerKeyword("identity");
        this.registerKeyword("identity_gap");
        this.registerKeyword("identity_start");
        this.registerKeyword("if");
        this.registerKeyword("in");
        this.registerKeyword("index");
        this.registerKeyword("inout");
        this.registerKeyword("insensitive");
        this.registerKeyword("insert");
        this.registerKeyword("install");
        this.registerKeyword("intersect");
        this.registerKeyword("into");
        this.registerKeyword("is");
        this.registerKeyword("isolation");
        this.registerKeyword("jar");
        this.registerKeyword("join");
        this.registerKeyword("key");
        this.registerKeyword("kill");
        this.registerKeyword("level");
        this.registerKeyword("like");
        this.registerKeyword("lineno");
        this.registerKeyword("load");
        this.registerKeyword("lock");
        this.registerKeyword("materialized");
        this.registerKeyword("max");
        this.registerKeyword("max_rows_per_page");
        this.registerKeyword("min");
        this.registerKeyword("mirror");
        this.registerKeyword("mirrorexit");
        this.registerKeyword("modify");
        this.registerKeyword("national");
        this.registerKeyword("new");
        this.registerKeyword("noholdlock");
        this.registerKeyword("nonclustered");
        this.registerKeyword("nonscrollable");
        this.registerKeyword("non_sensitive");
        this.registerKeyword("not");
        this.registerKeyword("null");
        this.registerKeyword("nullif");
        this.registerKeyword("numeric_truncation");
        this.registerKeyword("of");
        this.registerKeyword("off");
        this.registerKeyword("offsets");
        this.registerKeyword("on");
        this.registerKeyword("once");
        this.registerKeyword("online");
        this.registerKeyword("only");
        this.registerKeyword("open");
        this.registerKeyword("option");
        this.registerKeyword("or");
        this.registerKeyword("order");
        this.registerKeyword("out");
        this.registerKeyword("output");
        this.registerKeyword("over");
        this.registerKeyword("artition");
        this.registerKeyword("perm");
        this.registerKeyword("permanent");
        this.registerKeyword("plan");
        this.registerKeyword("prepare");
        this.registerKeyword("primary");
        this.registerKeyword("print");
        this.registerKeyword("privileges");
        this.registerKeyword("proc");
        this.registerKeyword("procedure");
        this.registerKeyword("processexit");
        this.registerKeyword("proxy_table");
        this.registerKeyword("public");
        this.registerKeyword("quiesce");
        this.registerKeyword("raiserror");
        this.registerKeyword("read");
        this.registerKeyword("readpast");
        this.registerKeyword("readtext");
        this.registerKeyword("reconfigure");
        this.registerKeyword("references");
        this.registerKeyword("remove");
        this.registerKeyword("reorg");
        this.registerKeyword("replace");
        this.registerKeyword("replication");
        this.registerKeyword("reservepagegap");
        this.registerKeyword("return");
        this.registerKeyword("returns");
        this.registerKeyword("revoke");
        this.registerKeyword("role");
        this.registerKeyword("rollback");
        this.registerKeyword("rowcount");
        this.registerKeyword("rows");
        this.registerKeyword("rule");
        this.registerKeyword("save");
        this.registerKeyword("schema");
        this.registerKeyword("scroll");
        this.registerKeyword("scrollable");
        this.registerKeyword("select");
        this.registerKeyword("semi_sensitive");
        this.registerKeyword("set");
        this.registerKeyword("setuser");
        this.registerKeyword("shared");
        this.registerKeyword("shutdown");
        this.registerKeyword("some");
        this.registerKeyword("statistics");
        this.registerKeyword("stringsize");
        this.registerKeyword("stripe");
        this.registerKeyword("sum");
        this.registerKeyword("syb_identity");
        this.registerKeyword("syb_restree");
        this.registerKeyword("syb_terminate");
        this.registerKeyword("top");
        this.registerKeyword("table");
        this.registerKeyword("temp");
        this.registerKeyword("temporary");
        this.registerKeyword("textsize");
        this.registerKeyword("to");
        this.registerKeyword("tracefile");
        this.registerKeyword("tran");
        this.registerKeyword("transaction");
        this.registerKeyword("trigger");
        this.registerKeyword("truncate");
        this.registerKeyword("tsequal");
        this.registerKeyword("union");
        this.registerKeyword("unique");
        this.registerKeyword("unpartition");
        this.registerKeyword("update");
        this.registerKeyword("use");
        this.registerKeyword("user");
        this.registerKeyword("user_option");
        this.registerKeyword("using");
        this.registerKeyword("values");
        this.registerKeyword("varying");
        this.registerKeyword("view");
        this.registerKeyword("waitfor");
        this.registerKeyword("when");
        this.registerKeyword("where");
        this.registerKeyword("while");
        this.registerKeyword("with");
        this.registerKeyword("work");
        this.registerKeyword("writetext");
        this.registerKeyword("xmlextract");
        this.registerKeyword("xmlparse");
        this.registerKeyword("xmltest");
        this.registerKeyword("xmlvalidate");
    }

    @Override
    public boolean supportsCascadeDelete() {
        return false;
    }

    @Override
    public int getMaxAliasLength() {
        return 30;
    }

    @Override
    public boolean areStringComparisonsCaseInsensitive() {
        return true;
    }

    @Override
    public String getCurrentTimestampSQLFunctionName() {
        return "getdate()";
    }

    @Override
    public boolean supportsExpectedLobUsagePattern() {
        return false;
    }

    @Override
    public String getCrossJoinSeparator() {
        return ", ";
    }

    @Override
    protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        return sqlCode == 16 ? TinyIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride(sqlCode);
    }

    @Override
    public boolean supportsLockTimeouts() {
        return false;
    }

    @Override
    public boolean supportsPartitionBy() {
        return false;
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.BOTH;
    }
}

