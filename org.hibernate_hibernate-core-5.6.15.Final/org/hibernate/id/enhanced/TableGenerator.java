/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.id.enhanced;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.InitCommand;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.ExportableColumn;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class TableGenerator
implements PersistentIdentifierGenerator {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)TableGenerator.class.getName());
    public static final String CONFIG_PREFER_SEGMENT_PER_ENTITY = "prefer_entity_table_as_segment_value";
    public static final String TABLE_PARAM = "table_name";
    public static final String DEF_TABLE = "hibernate_sequences";
    public static final String VALUE_COLUMN_PARAM = "value_column_name";
    public static final String DEF_VALUE_COLUMN = "next_val";
    public static final String SEGMENT_COLUMN_PARAM = "segment_column_name";
    public static final String DEF_SEGMENT_COLUMN = "sequence_name";
    public static final String SEGMENT_VALUE_PARAM = "segment_value";
    public static final String DEF_SEGMENT_VALUE = "default";
    public static final String SEGMENT_LENGTH_PARAM = "segment_value_length";
    public static final int DEF_SEGMENT_LENGTH = 255;
    public static final String INITIAL_PARAM = "initial_value";
    public static final int DEFAULT_INITIAL_VALUE = 1;
    public static final String INCREMENT_PARAM = "increment_size";
    public static final int DEFAULT_INCREMENT_SIZE = 1;
    public static final String OPT_PARAM = "optimizer";
    private boolean storeLastUsedValue;
    private Type identifierType;
    private QualifiedName qualifiedTableName;
    private QualifiedName physicalTableName;
    private String segmentColumnName;
    private String segmentValue;
    private int segmentValueLength;
    private String valueColumnName;
    private int initialValue;
    private int incrementSize;
    private String selectQuery;
    private String insertQuery;
    private String updateQuery;
    private Optimizer optimizer;
    private long accessCount;

    @Override
    @Deprecated
    public Object generatorKey() {
        return this.qualifiedTableName.render();
    }

    public final Type getIdentifierType() {
        return this.identifierType;
    }

    public final String getTableName() {
        return this.qualifiedTableName.render();
    }

    public final String getSegmentColumnName() {
        return this.segmentColumnName;
    }

    public final String getSegmentValue() {
        return this.segmentValue;
    }

    public final int getSegmentValueLength() {
        return this.segmentValueLength;
    }

    public final String getValueColumnName() {
        return this.valueColumnName;
    }

    public final int getInitialValue() {
        return this.initialValue;
    }

    public final int getIncrementSize() {
        return this.incrementSize;
    }

    public final Optimizer getOptimizer() {
        return this.optimizer;
    }

    public final long getTableAccessCount() {
        return this.accessCount;
    }

    @Deprecated
    public String[] getAllSqlForTests() {
        return new String[]{this.selectQuery, this.insertQuery, this.updateQuery};
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        this.storeLastUsedValue = serviceRegistry.getService(ConfigurationService.class).getSetting("hibernate.id.generator.stored_last_used", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        this.identifierType = type;
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        this.qualifiedTableName = this.determineGeneratorTableName(params, jdbcEnvironment, serviceRegistry);
        this.segmentColumnName = this.determineSegmentColumnName(params, jdbcEnvironment);
        this.valueColumnName = this.determineValueColumnName(params, jdbcEnvironment);
        this.segmentValue = this.determineSegmentValue(params);
        this.segmentValueLength = this.determineSegmentColumnSize(params);
        this.initialValue = this.determineInitialValue(params);
        this.incrementSize = this.determineIncrementSize(params);
        String optimizationStrategy = ConfigurationHelper.getString(OPT_PARAM, params, OptimizerFactory.determineImplicitOptimizerName(this.incrementSize, params));
        int optimizerInitialValue = ConfigurationHelper.getInt(INITIAL_PARAM, params, -1);
        this.optimizer = OptimizerFactory.buildOptimizer(optimizationStrategy, this.identifierType.getReturnedClass(), this.incrementSize, optimizerInitialValue);
    }

    protected QualifiedName determineGeneratorTableName(Properties params, JdbcEnvironment jdbcEnvironment, ServiceRegistry serviceRegistry) {
        String tableName;
        String generatorName;
        String fallbackTableName = DEF_TABLE;
        Boolean preferGeneratorNameAsDefaultName = serviceRegistry.getService(ConfigurationService.class).getSetting("hibernate.model.generator_name_as_sequence_name", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        if (preferGeneratorNameAsDefaultName.booleanValue() && StringHelper.isNotEmpty(generatorName = params.getProperty("GENERATOR_NAME"))) {
            fallbackTableName = generatorName;
        }
        if ((tableName = ConfigurationHelper.getString(TABLE_PARAM, params, fallbackTableName)).contains(".")) {
            return QualifiedNameParser.INSTANCE.parse(tableName);
        }
        Identifier catalog = jdbcEnvironment.getIdentifierHelper().toIdentifier(ConfigurationHelper.getString("catalog", params));
        Identifier schema = jdbcEnvironment.getIdentifierHelper().toIdentifier(ConfigurationHelper.getString("schema", params));
        return new QualifiedNameParser.NameParts(catalog, schema, jdbcEnvironment.getIdentifierHelper().toIdentifier(tableName));
    }

    protected String determineSegmentColumnName(Properties params, JdbcEnvironment jdbcEnvironment) {
        String name = ConfigurationHelper.getString(SEGMENT_COLUMN_PARAM, params, DEF_SEGMENT_COLUMN);
        return jdbcEnvironment.getIdentifierHelper().toIdentifier(name).render(jdbcEnvironment.getDialect());
    }

    protected String determineValueColumnName(Properties params, JdbcEnvironment jdbcEnvironment) {
        String name = ConfigurationHelper.getString(VALUE_COLUMN_PARAM, params, DEF_VALUE_COLUMN);
        return jdbcEnvironment.getIdentifierHelper().toIdentifier(name).render(jdbcEnvironment.getDialect());
    }

    protected String determineSegmentValue(Properties params) {
        String segmentValue = params.getProperty(SEGMENT_VALUE_PARAM);
        if (StringHelper.isEmpty(segmentValue)) {
            segmentValue = this.determineDefaultSegmentValue(params);
        }
        return segmentValue;
    }

    protected String determineDefaultSegmentValue(Properties params) {
        boolean preferSegmentPerEntity = ConfigurationHelper.getBoolean(CONFIG_PREFER_SEGMENT_PER_ENTITY, params, false);
        String defaultToUse = preferSegmentPerEntity ? params.getProperty("target_table") : DEF_SEGMENT_VALUE;
        LOG.usingDefaultIdGeneratorSegmentValue(this.qualifiedTableName.render(), this.segmentColumnName, defaultToUse);
        return defaultToUse;
    }

    protected int determineSegmentColumnSize(Properties params) {
        return ConfigurationHelper.getInt(SEGMENT_LENGTH_PARAM, params, 255);
    }

    protected int determineInitialValue(Properties params) {
        return ConfigurationHelper.getInt(INITIAL_PARAM, params, 1);
    }

    protected int determineIncrementSize(Properties params) {
        return ConfigurationHelper.getInt(INCREMENT_PARAM, params, 1);
    }

    protected String buildSelectQuery(String formattedPhysicalTableName, SqlStringGenerationContext context) {
        String alias = "tbl";
        String query = "select " + StringHelper.qualify("tbl", this.valueColumnName) + " from " + formattedPhysicalTableName + ' ' + "tbl" + " where " + StringHelper.qualify("tbl", this.segmentColumnName) + "=?";
        LockOptions lockOptions = new LockOptions(LockMode.PESSIMISTIC_WRITE);
        lockOptions.setAliasSpecificLockMode("tbl", LockMode.PESSIMISTIC_WRITE);
        Map<String, String[]> updateTargetColumnsMap = Collections.singletonMap("tbl", new String[]{this.valueColumnName});
        return context.getDialect().applyLocksToSql(query, lockOptions, updateTargetColumnsMap);
    }

    protected String buildUpdateQuery(String formattedPhysicalTableName, SqlStringGenerationContext context) {
        return "update " + formattedPhysicalTableName + " set " + this.valueColumnName + "=?  where " + this.valueColumnName + "=? and " + this.segmentColumnName + "=?";
    }

    protected String buildInsertQuery(String formattedPhysicalTableName, SqlStringGenerationContext context) {
        return "insert into " + formattedPhysicalTableName + " (" + this.segmentColumnName + ", " + this.valueColumnName + ")  values (?,?)";
    }

    protected InitCommand generateInsertInitCommand(SqlStringGenerationContext context) {
        String renderedTableName = context.format(this.physicalTableName);
        int value = this.initialValue;
        if (this.storeLastUsedValue) {
            value = this.initialValue - 1;
        }
        return new InitCommand("insert into " + renderedTableName + "(" + this.segmentColumnName + ", " + this.valueColumnName + ") values ('" + this.segmentValue + "'," + value + ")");
    }

    private IntegralDataTypeHolder makeValue() {
        return IdentifierGeneratorHelper.getIntegralDataTypeHolder(this.identifierType.getReturnedClass());
    }

    @Override
    public Serializable generate(final SharedSessionContractImplementor session, Object obj) {
        final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry().getService(JdbcServices.class).getSqlStatementLogger();
        final SessionEventListenerManager statsCollector = session.getEventListenerManager();
        return this.optimizer.generate(new AccessCallback(){

            @Override
            public IntegralDataTypeHolder getNextValue() {
                return session.getTransactionCoordinator().createIsolationDelegate().delegateWork(new AbstractReturningWork<IntegralDataTypeHolder>(){

                    @Override
                    public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
                        int rows;
                        IntegralDataTypeHolder value = TableGenerator.this.makeValue();
                        do {
                            try (PreparedStatement selectPS = TableGenerator.this.prepareStatement(connection, TableGenerator.this.selectQuery, statementLogger, statsCollector);){
                                selectPS.setString(1, TableGenerator.this.segmentValue);
                                ResultSet selectRS = TableGenerator.this.executeQuery(selectPS, statsCollector);
                                if (!selectRS.next()) {
                                    long initializationValue = TableGenerator.this.storeLastUsedValue ? (long)(TableGenerator.this.initialValue - 1) : (long)TableGenerator.this.initialValue;
                                    value.initialize(initializationValue);
                                    try (PreparedStatement insertPS = TableGenerator.this.prepareStatement(connection, TableGenerator.this.insertQuery, statementLogger, statsCollector);){
                                        LOG.tracef("binding parameter [%s] - [%s]", 1, TableGenerator.this.segmentValue);
                                        insertPS.setString(1, TableGenerator.this.segmentValue);
                                        value.bind(insertPS, 2);
                                        TableGenerator.this.executeUpdate(insertPS, statsCollector);
                                    }
                                } else {
                                    int defaultValue = TableGenerator.this.storeLastUsedValue ? 0 : 1;
                                    value.initialize(selectRS, defaultValue);
                                }
                                selectRS.close();
                            }
                            catch (SQLException e) {
                                LOG.unableToReadOrInitHiValue(e);
                                throw e;
                            }
                            try (PreparedStatement updatePS = TableGenerator.this.prepareStatement(connection, TableGenerator.this.updateQuery, statementLogger, statsCollector);){
                                IntegralDataTypeHolder updateValue = value.copy();
                                if (TableGenerator.this.optimizer.applyIncrementSizeToSourceValues()) {
                                    updateValue.add(TableGenerator.this.incrementSize);
                                } else {
                                    updateValue.increment();
                                }
                                updateValue.bind(updatePS, 1);
                                value.bind(updatePS, 2);
                                updatePS.setString(3, TableGenerator.this.segmentValue);
                                rows = TableGenerator.this.executeUpdate(updatePS, statsCollector);
                            }
                            catch (SQLException e) {
                                LOG.unableToUpdateQueryHiValue(TableGenerator.this.physicalTableName.render(), e);
                                throw e;
                            }
                        } while (rows == 0);
                        TableGenerator.this.accessCount++;
                        if (TableGenerator.this.storeLastUsedValue) {
                            return value.increment();
                        }
                        return value;
                    }
                }, true);
            }

            @Override
            public String getTenantIdentifier() {
                return session.getTenantIdentifier();
            }
        });
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
    public void registerExportables(Database database) {
        Dialect dialect = database.getJdbcEnvironment().getDialect();
        Namespace namespace = database.locateNamespace(this.qualifiedTableName.getCatalogName(), this.qualifiedTableName.getSchemaName());
        Table table = namespace.locateTable(this.qualifiedTableName.getObjectName());
        if (table == null) {
            table = namespace.createTable(this.qualifiedTableName.getObjectName(), false);
            ExportableColumn segmentColumn = new ExportableColumn(database, table, this.segmentColumnName, StringType.INSTANCE, dialect.getTypeName(12, this.segmentValueLength, 0, 0));
            segmentColumn.setNullable(false);
            table.addColumn(segmentColumn);
            table.setPrimaryKey(new PrimaryKey(table));
            table.getPrimaryKey().addColumn(segmentColumn);
            ExportableColumn valueColumn = new ExportableColumn(database, table, this.valueColumnName, LongType.INSTANCE);
            table.addColumn(valueColumn);
        }
        this.physicalTableName = table.getQualifiedTableName();
        table.addInitCommand(this::generateInsertInitCommand);
    }

    @Override
    public void initialize(SqlStringGenerationContext context) {
        String formattedPhysicalTableName = context.format(this.physicalTableName);
        this.selectQuery = this.buildSelectQuery(formattedPhysicalTableName, context);
        this.updateQuery = this.buildUpdateQuery(formattedPhysicalTableName, context);
        this.insertQuery = this.buildInsertQuery(formattedPhysicalTableName, context);
    }
}

