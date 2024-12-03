/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.QualifiedNameImpl;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.TeradataDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.Teradata14IdentityColumnSupport;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.sql.ForUpdateFragment;
import org.hibernate.tool.schema.internal.StandardIndexExporter;
import org.hibernate.tool.schema.spi.Exporter;
import org.hibernate.type.StandardBasicTypes;

public class Teradata14Dialect
extends TeradataDialect {
    StandardIndexExporter TeraIndexExporter = null;
    private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter(){

        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            int i;
            String constraintName = null;
            int errorCode = sqle.getErrorCode();
            if (errorCode == 27003) {
                constraintName = this.extractUsingTemplate("Unique constraint (", ") violated.", sqle.getMessage());
            } else if (errorCode == 2700) {
                constraintName = this.extractUsingTemplate("Referential constraint", "violation:", sqle.getMessage());
            } else if (errorCode == 5317) {
                constraintName = this.extractUsingTemplate("Check constraint (", ") violated.", sqle.getMessage());
            }
            if (constraintName != null && (i = constraintName.indexOf(46)) != -1) {
                constraintName = constraintName.substring(i + 1);
            }
            return constraintName;
        }
    };

    public Teradata14Dialect() {
        this.registerColumnType(-5, "BIGINT");
        this.registerColumnType(-2, "VARBYTE(100)");
        this.registerColumnType(-4, "VARBYTE(32000)");
        this.registerColumnType(-1, "VARCHAR(32000)");
        this.getDefaultProperties().setProperty("hibernate.jdbc.use_streams_for_binary", "true");
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
        this.registerFunction("current_time", new SQLFunctionTemplate(StandardBasicTypes.TIME, "current_time"));
        this.registerFunction("current_date", new SQLFunctionTemplate(StandardBasicTypes.DATE, "current_date"));
        this.TeraIndexExporter = new TeradataIndexExporter(this);
    }

    @Override
    public String getAddColumnString() {
        return "Add";
    }

    @Override
    public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
        int p;
        float f = precision > 0 ? (float)scale / (float)precision : 0.0f;
        int n = p = precision > 38 ? 38 : precision;
        int s = precision > 38 ? (int)(38.0 * (double)f) : (scale > 38 ? 38 : scale);
        return super.getTypeName(code, length, p, s);
    }

    @Override
    public boolean areStringComparisonsCaseInsensitive() {
        return false;
    }

    @Override
    public boolean supportsExpectedLobUsagePattern() {
        return true;
    }

    @Override
    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
    }

    @Override
    public boolean supportsTupleDistinctCounts() {
        return false;
    }

    @Override
    public boolean supportsExistsInSelect() {
        return false;
    }

    @Override
    public boolean supportsUnboundedLobLocatorMaterialization() {
        return false;
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
        statement.registerOutParameter(col, 2006);
        return ++col;
    }

    @Override
    public ResultSet getResultSet(CallableStatement cs) throws SQLException {
        boolean isResultSet = cs.execute();
        while (!isResultSet && cs.getUpdateCount() != -1) {
            isResultSet = cs.getMoreResults();
        }
        return cs.getResultSet();
    }

    @Override
    public String getWriteLockString(int timeout) {
        String sMsg = " Locking row for write ";
        if (timeout == 0) {
            return sMsg + " nowait ";
        }
        return sMsg;
    }

    @Override
    public String getReadLockString(int timeout) {
        String sMsg = " Locking row for read  ";
        if (timeout == 0) {
            return sMsg + " nowait ";
        }
        return sMsg;
    }

    public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
        return new ForUpdateFragment(this, aliasedLockOptions, keyColumnNames).toFragmentString() + " " + sql;
    }

    @Override
    public boolean useFollowOnLocking(QueryParameters parameters) {
        return true;
    }

    @Override
    public boolean supportsLockTimeouts() {
        return false;
    }

    @Override
    public Exporter<Index> getIndexExporter() {
        return this.TeraIndexExporter;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new Teradata14IdentityColumnSupport();
    }

    private static class TeradataIndexExporter
    extends StandardIndexExporter
    implements Exporter<Index> {
        public TeradataIndexExporter(Dialect dialect) {
            super(dialect);
        }

        @Override
        public String[] getSqlCreateStrings(Index index, Metadata metadata, SqlStringGenerationContext context) {
            JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();
            String tableName = context.format(index.getTable().getQualifiedTableName());
            String indexNameForCreation = Dialect.getDialect().qualifyIndexName() ? context.format(new QualifiedNameImpl(index.getTable().getQualifiedTableName().getCatalogName(), index.getTable().getQualifiedTableName().getSchemaName(), jdbcEnvironment.getIdentifierHelper().toIdentifier(index.getName()))) : index.getName();
            StringBuilder colBuf = new StringBuilder("");
            boolean first = true;
            Iterator<Column> columnItr = index.getColumnIterator();
            while (columnItr.hasNext()) {
                Column column = columnItr.next();
                if (first) {
                    first = false;
                } else {
                    colBuf.append(", ");
                }
                colBuf.append(column.getQuotedName(jdbcEnvironment.getDialect()));
            }
            colBuf.append(")");
            StringBuilder buf = new StringBuilder().append("create index ").append(indexNameForCreation).append('(').append((CharSequence)colBuf).append(" on ").append(tableName);
            return new String[]{buf.toString()};
        }
    }
}

