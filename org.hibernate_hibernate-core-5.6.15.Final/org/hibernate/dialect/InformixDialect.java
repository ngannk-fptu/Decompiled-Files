/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.SQLException;
import java.util.Locale;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.NvlFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.InformixIdentityColumnSupport;
import org.hibernate.dialect.pagination.FirstLimitHandler;
import org.hibernate.dialect.pagination.LegacyFirstLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.unique.InformixUniqueDelegate;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorInformixDatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;

public class InformixDialect
extends Dialect {
    private final UniqueDelegate uniqueDelegate;
    private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter(){

        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            int i;
            String constraintName = null;
            int errorCode = JdbcExceptionHelper.extractErrorCode(sqle);
            if (errorCode == -268) {
                constraintName = this.extractUsingTemplate("Unique constraint (", ") violated.", sqle.getMessage());
            } else if (errorCode == -691) {
                constraintName = this.extractUsingTemplate("Missing key in referenced table for referential constraint (", ").", sqle.getMessage());
            } else if (errorCode == -692) {
                constraintName = this.extractUsingTemplate("Key value for constraint (", ") is still being referenced.", sqle.getMessage());
            }
            if (constraintName != null && (i = constraintName.indexOf(46)) != -1) {
                constraintName = constraintName.substring(i + 1);
            }
            return constraintName;
        }
    };

    public InformixDialect() {
        this.registerColumnType(-5, "int8");
        this.registerColumnType(-2, "byte");
        this.registerColumnType(-7, "smallint");
        this.registerColumnType(1, "char($l)");
        this.registerColumnType(91, "date");
        this.registerColumnType(3, "decimal");
        this.registerColumnType(8, "float");
        this.registerColumnType(6, "smallfloat");
        this.registerColumnType(4, "integer");
        this.registerColumnType(-4, "blob");
        this.registerColumnType(-1, "clob");
        this.registerColumnType(2, "decimal");
        this.registerColumnType(7, "smallfloat");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(93, "datetime year to fraction(5)");
        this.registerColumnType(92, "datetime hour to second");
        this.registerColumnType(-6, "smallint");
        this.registerColumnType(-3, "byte");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(12, 255L, "varchar($l)");
        this.registerColumnType(12, 32739L, "lvarchar($l)");
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "||", ")"));
        this.registerFunction("substring", new SQLFunctionTemplate(StandardBasicTypes.STRING, "substring(?1 FROM ?2 FOR ?3)"));
        this.registerFunction("substr", new SQLFunctionTemplate(StandardBasicTypes.STRING, "substr(?1, ?2, ?3)"));
        this.registerFunction("coalesce", new NvlFunction());
        this.registerFunction("nvl", new NvlFunction());
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("current_date", new NoArgSQLFunction("today", StandardBasicTypes.DATE, false));
        this.uniqueDelegate = new InformixUniqueDelegate(this);
    }

    @Override
    public String getAddColumnString() {
        return "add";
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        StringBuilder result = new StringBuilder(30).append(" add constraint ").append(" foreign key (").append(String.join((CharSequence)", ", foreignKey)).append(") references ").append(referencedTable);
        if (!referencesPrimaryKey) {
            result.append(" (").append(String.join((CharSequence)", ", primaryKey)).append(')');
        }
        result.append(" constraint ").append(constraintName);
        return result.toString();
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String foreignKeyDefinition) {
        return new StringBuilder(30).append(" add constraint ").append(foreignKeyDefinition).append(" constraint ").append(constraintName).toString();
    }

    @Override
    public String getAddPrimaryKeyConstraintString(String constraintName) {
        return " add constraint primary key constraint " + constraintName + " ";
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
    public String getSequenceNextValString(String sequenceName) {
        return "select " + this.getSelectSequenceNextValString(sequenceName) + " from informix.systables where tabid=1";
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return sequenceName + ".nextval";
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    public String getQuerySequencesString() {
        return "select systables.tabname as sequence_name, syssequences.* from syssequences join systables on syssequences.tabid = systables.tabid where tabtype = 'Q'";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return SequenceInformationExtractorInformixDatabaseImpl.INSTANCE;
    }

    @Override
    public LimitHandler getLimitHandler() {
        if (this.isLegacyLimitHandlerBehaviorEnabled()) {
            return LegacyFirstLimitHandler.INSTANCE;
        }
        return FirstLimitHandler.INSTANCE;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean useMaxForLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return false;
    }

    @Override
    public String getLimitString(String querySelect, int offset, int limit) {
        if (offset > 0) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        return new StringBuilder(querySelect.length() + 8).append(querySelect).insert(querySelect.toLowerCase(Locale.ROOT).indexOf("select") + 6, " first " + limit).toString();
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }

    @Override
    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
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
        return "select distinct current timestamp from informix.systables";
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String getCreateIdTableCommand() {
                return "create temp table";
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "with no log";
            }
        }, AfterUseAction.CLEAN, null);
    }

    @Override
    public UniqueDelegate getUniqueDelegate() {
        return this.uniqueDelegate;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new InformixIdentityColumnSupport();
    }

    @Override
    public String toBooleanValueString(boolean bool) {
        return bool ? "'t'" : "'f'";
    }
}

