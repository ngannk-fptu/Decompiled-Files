/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.id.enhanced;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.AssertionFailure;
import org.hibernate.LockMode;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.InitCommand;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.ExportableColumn;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.DatabaseStructure;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.mapping.Table;
import org.hibernate.type.LongType;
import org.jboss.logging.Logger;

public class TableStructure
implements DatabaseStructure {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)TableStructure.class.getName());
    private final QualifiedName logicalQualifiedTableName;
    private final Identifier logicalValueColumnNameIdentifier;
    private final int initialValue;
    private final int incrementSize;
    private final Class numberType;
    private QualifiedName physicalTableName;
    @Deprecated
    private String formattedTableNameForLegacyGetter;
    private String valueColumnNameText;
    private String selectQuery;
    private String updateQuery;
    private boolean applyIncrementSizeToSourceValues;
    private int accessCounter;

    public TableStructure(JdbcEnvironment jdbcEnvironment, QualifiedName qualifiedTableName, Identifier valueColumnNameIdentifier, int initialValue, int incrementSize, Class numberType) {
        this.logicalQualifiedTableName = qualifiedTableName;
        this.logicalValueColumnNameIdentifier = valueColumnNameIdentifier;
        this.initialValue = initialValue;
        this.incrementSize = incrementSize;
        this.numberType = numberType;
    }

    @Override
    @Deprecated
    public String getName() {
        return this.formattedTableNameForLegacyGetter;
    }

    @Override
    public QualifiedName getPhysicalName() {
        return this.physicalTableName;
    }

    @Override
    public int getInitialValue() {
        return this.initialValue;
    }

    @Override
    public int getIncrementSize() {
        return this.incrementSize;
    }

    @Override
    public int getTimesAccessed() {
        return this.accessCounter;
    }

    @Override
    public String[] getAllSqlForTests() {
        return new String[]{this.selectQuery, this.updateQuery};
    }

    @Override
    public void prepare(Optimizer optimizer) {
        this.applyIncrementSizeToSourceValues = optimizer.applyIncrementSizeToSourceValues();
    }

    private IntegralDataTypeHolder makeValue() {
        return IdentifierGeneratorHelper.getIntegralDataTypeHolder(this.numberType);
    }

    @Override
    public AccessCallback buildCallback(final SharedSessionContractImplementor session) {
        final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry().getService(JdbcServices.class).getSqlStatementLogger();
        if (this.selectQuery == null || this.updateQuery == null) {
            throw new AssertionFailure("SequenceStyleGenerator's TableStructure was not properly initialized");
        }
        final SessionEventListenerManager statsCollector = session.getEventListenerManager();
        return new AccessCallback(){

            @Override
            public IntegralDataTypeHolder getNextValue() {
                return session.getTransactionCoordinator().createIsolationDelegate().delegateWork(new AbstractReturningWork<IntegralDataTypeHolder>(){

                    @Override
                    public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
                        int rows;
                        IntegralDataTypeHolder value = TableStructure.this.makeValue();
                        do {
                            try (PreparedStatement selectStatement = TableStructure.this.prepareStatement(connection, TableStructure.this.selectQuery, statementLogger, statsCollector);){
                                ResultSet selectRS = TableStructure.this.executeQuery(selectStatement, statsCollector);
                                if (!selectRS.next()) {
                                    String err = "could not read a hi value - you need to populate the table: " + TableStructure.this.physicalTableName;
                                    LOG.error(err);
                                    throw new IdentifierGenerationException(err);
                                }
                                value.initialize(selectRS, 1L);
                                selectRS.close();
                            }
                            catch (SQLException sqle) {
                                LOG.error("could not read a hi value", sqle);
                                throw sqle;
                            }
                            try (PreparedStatement updatePS = TableStructure.this.prepareStatement(connection, TableStructure.this.updateQuery, statementLogger, statsCollector);){
                                int increment = TableStructure.this.applyIncrementSizeToSourceValues ? TableStructure.this.incrementSize : 1;
                                IntegralDataTypeHolder updateValue = value.copy().add(increment);
                                updateValue.bind(updatePS, 1);
                                value.bind(updatePS, 2);
                                rows = TableStructure.this.executeUpdate(updatePS, statsCollector);
                            }
                            catch (SQLException e) {
                                LOG.unableToUpdateQueryHiValue(TableStructure.this.physicalTableName.render(), e);
                                throw e;
                            }
                        } while (rows == 0);
                        TableStructure.this.accessCounter++;
                        return value;
                    }
                }, true);
            }

            @Override
            public String getTenantIdentifier() {
                return session.getTenantIdentifier();
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PreparedStatement prepareStatement(Connection connection, String sql, SqlStatementLogger statementLogger, SessionEventListenerManager statsCollector) throws SQLException {
        statementLogger.logStatement(sql, FormatStyle.BASIC.getFormatter());
        try {
            statsCollector.jdbcPrepareStatementStart();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            return preparedStatement;
        }
        finally {
            statsCollector.jdbcPrepareStatementEnd();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int executeUpdate(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
        try {
            statsCollector.jdbcExecuteStatementStart();
            int n = ps.executeUpdate();
            return n;
        }
        finally {
            statsCollector.jdbcExecuteStatementEnd();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ResultSet executeQuery(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
        try {
            statsCollector.jdbcExecuteStatementStart();
            ResultSet resultSet = ps.executeQuery();
            return resultSet;
        }
        finally {
            statsCollector.jdbcExecuteStatementEnd();
        }
    }

    @Override
    public boolean isPhysicalSequence() {
        return false;
    }

    @Override
    public void registerExportables(Database database) {
        JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
        Dialect dialect = jdbcEnvironment.getDialect();
        Namespace namespace = database.locateNamespace(this.logicalQualifiedTableName.getCatalogName(), this.logicalQualifiedTableName.getSchemaName());
        Table table = namespace.locateTable(this.logicalQualifiedTableName.getObjectName());
        boolean tableCreated = false;
        if (table == null) {
            table = namespace.createTable(this.logicalQualifiedTableName.getObjectName(), false);
            tableCreated = true;
        }
        this.physicalTableName = table.getQualifiedTableName();
        this.formattedTableNameForLegacyGetter = jdbcEnvironment.getQualifiedObjectNameFormatter().format(this.physicalTableName, dialect);
        this.valueColumnNameText = this.logicalValueColumnNameIdentifier.render(dialect);
        if (tableCreated) {
            ExportableColumn valueColumn = new ExportableColumn(database, table, this.valueColumnNameText, LongType.INSTANCE);
            table.addColumn(valueColumn);
            table.addInitCommand(context -> new InitCommand("insert into " + context.format(this.physicalTableName) + " values ( " + this.initialValue + " )"));
        }
    }

    @Override
    public void initialize(SqlStringGenerationContext context) {
        Dialect dialect = context.getDialect();
        String formattedPhysicalTableName = context.format(this.physicalTableName);
        this.selectQuery = "select " + this.valueColumnNameText + " as id_val from " + dialect.appendLockHint(LockMode.PESSIMISTIC_WRITE, formattedPhysicalTableName) + dialect.getForUpdateString();
        this.updateQuery = "update " + formattedPhysicalTableName + " set " + this.valueColumnNameText + "= ? where " + this.valueColumnNameText + "=?";
    }
}

