/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.DatabaseMetaData;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.DecodeCaseFragment;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorSAPDBDatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;

public class SAPDBDialect
extends Dialect {
    public SAPDBDialect() {
        this.registerColumnType(-7, "boolean");
        this.registerColumnType(-5, "fixed(19,0)");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "fixed(3,0)");
        this.registerColumnType(4, "int");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(6, "float");
        this.registerColumnType(8, "double precision");
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(-3, "long byte");
        this.registerColumnType(2, "fixed($p,$s)");
        this.registerColumnType(2005, "long varchar");
        this.registerColumnType(2004, "long byte");
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("log", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE));
        this.registerFunction("power", new StandardSQLFunction("power"));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE));
        this.registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        this.registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan2", new StandardSQLFunction("atan2", StandardBasicTypes.DOUBLE));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("trunc", new StandardSQLFunction("trunc"));
        this.registerFunction("ceil", new StandardSQLFunction("ceil"));
        this.registerFunction("floor", new StandardSQLFunction("floor"));
        this.registerFunction("greatest", new StandardSQLFunction("greatest"));
        this.registerFunction("least", new StandardSQLFunction("least"));
        this.registerFunction("time", new StandardSQLFunction("time", StandardBasicTypes.TIME));
        this.registerFunction("timestamp", new StandardSQLFunction("timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("date", new StandardSQLFunction("date", StandardBasicTypes.DATE));
        this.registerFunction("microsecond", new StandardSQLFunction("microsecond", StandardBasicTypes.INTEGER));
        this.registerFunction("second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "second(?1)"));
        this.registerFunction("minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "minute(?1)"));
        this.registerFunction("hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "hour(?1)"));
        this.registerFunction("day", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "day(?1)"));
        this.registerFunction("month", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "month(?1)"));
        this.registerFunction("year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "year(?1)"));
        this.registerFunction("extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1(?3)"));
        this.registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));
        this.registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING));
        this.registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("weekofyear", new StandardSQLFunction("weekofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));
        this.registerFunction("translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING));
        this.registerFunction("lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING));
        this.registerFunction("rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING));
        this.registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("initcap", new StandardSQLFunction("initcap", StandardBasicTypes.STRING));
        this.registerFunction("lower", new StandardSQLFunction("lower", StandardBasicTypes.STRING));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim", StandardBasicTypes.STRING));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING));
        this.registerFunction("lfill", new StandardSQLFunction("ltrim", StandardBasicTypes.STRING));
        this.registerFunction("rfill", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING));
        this.registerFunction("soundex", new StandardSQLFunction("soundex", StandardBasicTypes.STRING));
        this.registerFunction("upper", new StandardSQLFunction("upper", StandardBasicTypes.STRING));
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.STRING));
        this.registerFunction("index", new StandardSQLFunction("index", StandardBasicTypes.INTEGER));
        this.registerFunction("value", new StandardSQLFunction("value"));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "||", ")"));
        this.registerFunction("substring", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("locate", new StandardSQLFunction("index", StandardBasicTypes.INTEGER));
        this.registerFunction("coalesce", new StandardSQLFunction("value"));
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String getAddColumnString() {
        return "add";
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        StringBuilder res = new StringBuilder(30).append(" foreign key ").append(constraintName).append(" (").append(String.join((CharSequence)", ", foreignKey)).append(") references ").append(referencedTable);
        if (!referencesPrimaryKey) {
            res.append(" (").append(String.join((CharSequence)", ", primaryKey)).append(')');
        }
        return res.toString();
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String foreignKeyDefinition) {
        return foreignKeyDefinition;
    }

    @Override
    public String getAddPrimaryKeyConstraintString(String constraintName) {
        return " primary key ";
    }

    @Override
    public String getNullColumnString() {
        return " null";
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + this.getSelectSequenceNextValString(sequenceName) + " from dual";
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return sequenceName + ".nextval";
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName;
    }

    @Override
    public String getQuerySequencesString() {
        return "select * from domain.sequences";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return SequenceInformationExtractorSAPDBDatabaseImpl.INSTANCE;
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public CaseFragment createCaseFragment() {
        return new DecodeCaseFragment();
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String generateIdTableName(String baseName) {
                return "temp." + super.generateIdTableName(baseName);
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "ignore rollback";
            }
        }, AfterUseAction.DROP, null);
    }

    @Override
    public boolean supportsJdbcConnectionLobCreation(DatabaseMetaData databaseMetaData) {
        return false;
    }
}

