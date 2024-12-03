/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.dialect;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import org.hibernate.MappingException;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.function.AnsiTrimFunction;
import org.hibernate.dialect.function.DerbyConcatFunction;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.DerbyCaseFragment;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorDerbyDatabaseImpl;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.jboss.logging.Logger;

@Deprecated
public class DerbyDialect
extends DB2Dialect {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)DerbyDialect.class.getName());
    private int driverVersionMajor;
    private int driverVersionMinor;
    private final LimitHandler limitHandler;

    public DerbyDialect() {
        if (this.getClass() == DerbyDialect.class) {
            LOG.deprecatedDerbyDialect();
        }
        this.registerFunction("concat", new DerbyConcatFunction());
        this.registerFunction("trim", new AnsiTrimFunction());
        this.registerColumnType(2004, "blob");
        this.registerDerbyKeywords();
        this.determineDriverVersion();
        if (this.driverVersionMajor > 10 || this.driverVersionMajor == 10 && this.driverVersionMinor >= 7) {
            this.registerColumnType(16, "boolean");
        }
        this.limitHandler = new DerbyLimitHandler();
    }

    private void determineDriverVersion() {
        try {
            Class sysinfoClass = ReflectHelper.classForName("org.apache.derby.tools.sysinfo", this.getClass());
            Method majorVersionGetter = sysinfoClass.getMethod("getMajorVersion", ReflectHelper.NO_PARAM_SIGNATURE);
            Method minorVersionGetter = sysinfoClass.getMethod("getMinorVersion", ReflectHelper.NO_PARAM_SIGNATURE);
            this.driverVersionMajor = (Integer)majorVersionGetter.invoke(null, ReflectHelper.NO_PARAMS);
            this.driverVersionMinor = (Integer)minorVersionGetter.invoke(null, ReflectHelper.NO_PARAMS);
        }
        catch (Exception e) {
            LOG.unableToLoadDerbyDriver(e.getMessage());
            this.driverVersionMajor = -1;
            this.driverVersionMinor = -1;
        }
    }

    private boolean isTenPointFiveReleaseOrNewer() {
        return this.driverVersionMajor > 10 || this.driverVersionMajor == 10 && this.driverVersionMinor >= 5;
    }

    @Override
    public String getCrossJoinSeparator() {
        return ", ";
    }

    @Override
    public CaseFragment createCaseFragment() {
        return new DerbyCaseFragment();
    }

    @Override
    public boolean dropConstraints() {
        return true;
    }

    @Override
    public boolean supportsSequences() {
        return this.driverVersionMajor > 10 || this.driverVersionMajor == 10 && this.driverVersionMinor >= 6;
    }

    @Override
    public String getQuerySequencesString() {
        if (this.supportsSequences()) {
            return "select sys.sysschemas.schemaname as sequence_schema, sys.syssequences.* from sys.syssequences left join sys.sysschemas on sys.syssequences.schemaid = sys.sysschemas.schemaid";
        }
        return null;
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        if (this.getQuerySequencesString() == null) {
            return SequenceInformationExtractorNoOpImpl.INSTANCE;
        }
        return SequenceInformationExtractorDerbyDatabaseImpl.INSTANCE;
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        if (this.supportsSequences()) {
            return "values next value for " + sequenceName;
        }
        throw new MappingException("Derby does not support sequence prior to release 10.6.1.0");
    }

    @Override
    public boolean supportsLimit() {
        return this.isTenPointFiveReleaseOrNewer();
    }

    @Override
    public boolean supportsCommentOn() {
        return false;
    }

    @Override
    public boolean supportsLimitOffset() {
        return this.isTenPointFiveReleaseOrNewer();
    }

    @Override
    public String getForUpdateString() {
        return " for update with rs";
    }

    @Override
    public String getWriteLockString(int timeout) {
        return " for update with rs";
    }

    @Override
    public String getReadLockString(int timeout) {
        return " for read only with rs";
    }

    @Override
    public LimitHandler getLimitHandler() {
        return this.limitHandler;
    }

    @Override
    public boolean supportsTuplesInSubqueries() {
        return false;
    }

    @Override
    public String getLimitString(String query, int offset, int limit) {
        StringBuilder sb = new StringBuilder(query.length() + 50);
        String normalizedSelect = query.toLowerCase(Locale.ROOT).trim();
        int forUpdateIndex = normalizedSelect.lastIndexOf("for update");
        if (this.hasForUpdateClause(forUpdateIndex)) {
            sb.append(query.substring(0, forUpdateIndex - 1));
        } else if (this.hasWithClause(normalizedSelect)) {
            sb.append(query.substring(0, this.getWithIndex(query) - 1));
        } else {
            sb.append(query);
        }
        if (offset == 0) {
            sb.append(" fetch first ");
        } else {
            sb.append(" offset ").append(offset).append(" rows fetch next ");
        }
        sb.append(limit).append(" rows only");
        if (this.hasForUpdateClause(forUpdateIndex)) {
            sb.append(' ');
            sb.append(query.substring(forUpdateIndex));
        } else if (this.hasWithClause(normalizedSelect)) {
            sb.append(' ').append(query.substring(this.getWithIndex(query)));
        }
        return sb.toString();
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }

    private boolean hasForUpdateClause(int forUpdateIndex) {
        return forUpdateIndex >= 0;
    }

    private boolean hasWithClause(String normalizedSelect) {
        return normalizedSelect.startsWith("with ", normalizedSelect.length() - 7);
    }

    private int getWithIndex(String querySelect) {
        int i = querySelect.lastIndexOf("with ");
        if (i < 0) {
            i = querySelect.lastIndexOf("WITH ");
        }
        return i;
    }

    @Override
    public boolean supportsLobValueChangePropogation() {
        return false;
    }

    @Override
    public boolean supportsUnboundedLobLocatorMaterialization() {
        return false;
    }

    @Override
    public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData) throws SQLException {
        builder.applyIdentifierCasing(dbMetaData);
        builder.applyReservedWords(dbMetaData);
        builder.applyReservedWords(this.getKeywords());
        builder.setNameQualifierSupport(this.getNameQualifierSupport());
        return builder.build();
    }

    protected void registerDerbyKeywords() {
        this.registerKeyword("ADD");
        this.registerKeyword("ALL");
        this.registerKeyword("ALLOCATE");
        this.registerKeyword("ALTER");
        this.registerKeyword("AND");
        this.registerKeyword("ANY");
        this.registerKeyword("ARE");
        this.registerKeyword("AS");
        this.registerKeyword("ASC");
        this.registerKeyword("ASSERTION");
        this.registerKeyword("AT");
        this.registerKeyword("AUTHORIZATION");
        this.registerKeyword("AVG");
        this.registerKeyword("BEGIN");
        this.registerKeyword("BETWEEN");
        this.registerKeyword("BIT");
        this.registerKeyword("BOOLEAN");
        this.registerKeyword("BOTH");
        this.registerKeyword("BY");
        this.registerKeyword("CALL");
        this.registerKeyword("CASCADE");
        this.registerKeyword("CASCADED");
        this.registerKeyword("CASE");
        this.registerKeyword("CAST");
        this.registerKeyword("CHAR");
        this.registerKeyword("CHARACTER");
        this.registerKeyword("CHECK");
        this.registerKeyword("CLOSE");
        this.registerKeyword("COLLATE");
        this.registerKeyword("COLLATION");
        this.registerKeyword("COLUMN");
        this.registerKeyword("COMMIT");
        this.registerKeyword("CONNECT");
        this.registerKeyword("CONNECTION");
        this.registerKeyword("CONSTRAINT");
        this.registerKeyword("CONSTRAINTS");
        this.registerKeyword("CONTINUE");
        this.registerKeyword("CONVERT");
        this.registerKeyword("CORRESPONDING");
        this.registerKeyword("COUNT");
        this.registerKeyword("CREATE");
        this.registerKeyword("CURRENT");
        this.registerKeyword("CURRENT_DATE");
        this.registerKeyword("CURRENT_TIME");
        this.registerKeyword("CURRENT_TIMESTAMP");
        this.registerKeyword("CURRENT_USER");
        this.registerKeyword("CURSOR");
        this.registerKeyword("DEALLOCATE");
        this.registerKeyword("DEC");
        this.registerKeyword("DECIMAL");
        this.registerKeyword("DECLARE");
        this.registerKeyword("DEFERRABLE");
        this.registerKeyword("DEFERRED");
        this.registerKeyword("DELETE");
        this.registerKeyword("DESC");
        this.registerKeyword("DESCRIBE");
        this.registerKeyword("DIAGNOSTICS");
        this.registerKeyword("DISCONNECT");
        this.registerKeyword("DISTINCT");
        this.registerKeyword("DOUBLE");
        this.registerKeyword("DROP");
        this.registerKeyword("ELSE");
        this.registerKeyword("END");
        this.registerKeyword("ENDEXEC");
        this.registerKeyword("ESCAPE");
        this.registerKeyword("EXCEPT");
        this.registerKeyword("EXCEPTION");
        this.registerKeyword("EXEC");
        this.registerKeyword("EXECUTE");
        this.registerKeyword("EXISTS");
        this.registerKeyword("EXPLAIN");
        this.registerKeyword("EXTERNAL");
        this.registerKeyword("FALSE");
        this.registerKeyword("FETCH");
        this.registerKeyword("FIRST");
        this.registerKeyword("FLOAT");
        this.registerKeyword("FOR");
        this.registerKeyword("FOREIGN");
        this.registerKeyword("FOUND");
        this.registerKeyword("FROM");
        this.registerKeyword("FULL");
        this.registerKeyword("FUNCTION");
        this.registerKeyword("GET");
        this.registerKeyword("GET_CURRENT_CONNECTION");
        this.registerKeyword("GLOBAL");
        this.registerKeyword("GO");
        this.registerKeyword("GOTO");
        this.registerKeyword("GRANT");
        this.registerKeyword("GROUP");
        this.registerKeyword("HAVING");
        this.registerKeyword("HOUR");
        this.registerKeyword("IDENTITY");
        this.registerKeyword("IMMEDIATE");
        this.registerKeyword("IN");
        this.registerKeyword("INDICATOR");
        this.registerKeyword("INITIALLY");
        this.registerKeyword("INNER");
        this.registerKeyword("INOUT");
        this.registerKeyword("INPUT");
        this.registerKeyword("INSENSITIVE");
        this.registerKeyword("INSERT");
        this.registerKeyword("INT");
        this.registerKeyword("INTEGER");
        this.registerKeyword("INTERSECT");
        this.registerKeyword("INTO");
        this.registerKeyword("IS");
        this.registerKeyword("ISOLATION");
        this.registerKeyword("JOIN");
        this.registerKeyword("KEY");
        this.registerKeyword("LAST");
        this.registerKeyword("LEFT");
        this.registerKeyword("LIKE");
        this.registerKeyword("LONGINT");
        this.registerKeyword("LOWER");
        this.registerKeyword("LTRIM");
        this.registerKeyword("MATCH");
        this.registerKeyword("MAX");
        this.registerKeyword("MIN");
        this.registerKeyword("MINUTE");
        this.registerKeyword("NATIONAL");
        this.registerKeyword("NATURAL");
        this.registerKeyword("NCHAR");
        this.registerKeyword("NVARCHAR");
        this.registerKeyword("NEXT");
        this.registerKeyword("NO");
        this.registerKeyword("NOT");
        this.registerKeyword("NULL");
        this.registerKeyword("NULLIF");
        this.registerKeyword("NUMERIC");
        this.registerKeyword("OF");
        this.registerKeyword("ON");
        this.registerKeyword("ONLY");
        this.registerKeyword("OPEN");
        this.registerKeyword("OPTION");
        this.registerKeyword("OR");
        this.registerKeyword("ORDER");
        this.registerKeyword("OUT");
        this.registerKeyword("OUTER");
        this.registerKeyword("OUTPUT");
        this.registerKeyword("OVERLAPS");
        this.registerKeyword("PAD");
        this.registerKeyword("PARTIAL");
        this.registerKeyword("PREPARE");
        this.registerKeyword("PRESERVE");
        this.registerKeyword("PRIMARY");
        this.registerKeyword("PRIOR");
        this.registerKeyword("PRIVILEGES");
        this.registerKeyword("PROCEDURE");
        this.registerKeyword("PUBLIC");
        this.registerKeyword("READ");
        this.registerKeyword("REAL");
        this.registerKeyword("REFERENCES");
        this.registerKeyword("RELATIVE");
        this.registerKeyword("RESTRICT");
        this.registerKeyword("REVOKE");
        this.registerKeyword("RIGHT");
        this.registerKeyword("ROLLBACK");
        this.registerKeyword("ROWS");
        this.registerKeyword("RTRIM");
        this.registerKeyword("SCHEMA");
        this.registerKeyword("SCROLL");
        this.registerKeyword("SECOND");
        this.registerKeyword("SELECT");
        this.registerKeyword("SESSION_USER");
        this.registerKeyword("SET");
        this.registerKeyword("SMALLINT");
        this.registerKeyword("SOME");
        this.registerKeyword("SPACE");
        this.registerKeyword("SQL");
        this.registerKeyword("SQLCODE");
        this.registerKeyword("SQLERROR");
        this.registerKeyword("SQLSTATE");
        this.registerKeyword("SUBSTR");
        this.registerKeyword("SUBSTRING");
        this.registerKeyword("SUM");
        this.registerKeyword("SYSTEM_USER");
        this.registerKeyword("TABLE");
        this.registerKeyword("TEMPORARY");
        this.registerKeyword("TIMEZONE_HOUR");
        this.registerKeyword("TIMEZONE_MINUTE");
        this.registerKeyword("TO");
        this.registerKeyword("TRAILING");
        this.registerKeyword("TRANSACTION");
        this.registerKeyword("TRANSLATE");
        this.registerKeyword("TRANSLATION");
        this.registerKeyword("TRUE");
        this.registerKeyword("UNION");
        this.registerKeyword("UNIQUE");
        this.registerKeyword("UNKNOWN");
        this.registerKeyword("UPDATE");
        this.registerKeyword("UPPER");
        this.registerKeyword("USER");
        this.registerKeyword("USING");
        this.registerKeyword("VALUES");
        this.registerKeyword("VARCHAR");
        this.registerKeyword("VARYING");
        this.registerKeyword("VIEW");
        this.registerKeyword("WHENEVER");
        this.registerKeyword("WHERE");
        this.registerKeyword("WITH");
        this.registerKeyword("WORK");
        this.registerKeyword("WRITE");
        this.registerKeyword("XML");
        this.registerKeyword("XMLEXISTS");
        this.registerKeyword("XMLPARSE");
        this.registerKeyword("XMLSERIALIZE");
        this.registerKeyword("YEAR");
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String generateIdTableName(String baseName) {
                return "session." + super.generateIdTableName(baseName);
            }

            @Override
            public String getCreateIdTableCommand() {
                return "declare global temporary table";
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "not logged";
            }
        }, AfterUseAction.CLEAN, null);
    }

    @Override
    public boolean supportsPartitionBy() {
        return false;
    }

    private final class DerbyLimitHandler
    extends AbstractLimitHandler {
        private DerbyLimitHandler() {
        }

        @Override
        public String processSql(String sql, RowSelection selection) {
            StringBuilder sb = new StringBuilder(sql.length() + 50);
            String normalizedSelect = sql.toLowerCase(Locale.ROOT).trim();
            int forUpdateIndex = normalizedSelect.lastIndexOf("for update");
            if (DerbyDialect.this.hasForUpdateClause(forUpdateIndex)) {
                sb.append(sql.substring(0, forUpdateIndex - 1));
            } else if (DerbyDialect.this.hasWithClause(normalizedSelect)) {
                sb.append(sql.substring(0, DerbyDialect.this.getWithIndex(sql) - 1));
            } else {
                sb.append(sql);
            }
            if (LimitHelper.hasFirstRow(selection)) {
                sb.append(" offset ").append(selection.getFirstRow()).append(" rows fetch next ");
            } else {
                sb.append(" fetch first ");
            }
            sb.append(this.getMaxOrLimit(selection)).append(" rows only");
            if (DerbyDialect.this.hasForUpdateClause(forUpdateIndex)) {
                sb.append(' ');
                sb.append(sql.substring(forUpdateIndex));
            } else if (DerbyDialect.this.hasWithClause(normalizedSelect)) {
                sb.append(' ').append(sql.substring(DerbyDialect.this.getWithIndex(sql)));
            }
            return sb.toString();
        }

        @Override
        public boolean supportsLimit() {
            return DerbyDialect.this.isTenPointFiveReleaseOrNewer();
        }

        @Override
        public boolean supportsLimitOffset() {
            return DerbyDialect.this.isTenPointFiveReleaseOrNewer();
        }

        @Override
        public boolean supportsVariableLimit() {
            return false;
        }
    }
}

