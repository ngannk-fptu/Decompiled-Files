/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
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
import org.hibernate.id.enhanced.LegacyHiLoAlgorithmOptimizer;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

@Deprecated
public class MultipleHiLoPerTableGenerator
implements PersistentIdentifierGenerator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(MultipleHiLoPerTableGenerator.class);
    public static final String ID_TABLE = "table";
    public static final String PK_COLUMN_NAME = "primary_key_column";
    public static final String PK_VALUE_NAME = "primary_key_value";
    public static final String VALUE_COLUMN_NAME = "value_column";
    public static final String PK_LENGTH_NAME = "primary_key_length";
    private static final int DEFAULT_PK_LENGTH = 255;
    public static final String DEFAULT_TABLE = "hibernate_sequences";
    private static final String DEFAULT_PK_COLUMN = "sequence_name";
    private static final String DEFAULT_VALUE_COLUMN = "sequence_next_hi_value";
    private QualifiedName qualifiedTableName;
    private QualifiedName physicalTableName;
    @Deprecated
    private String formattedTableNameForLegacyGetter;
    private String segmentColumnName;
    private String segmentName;
    private String valueColumnName;
    private String query;
    private String insert;
    private String update;
    public static final String MAX_LO = "max_lo";
    private int maxLo;
    private LegacyHiLoAlgorithmOptimizer hiloOptimizer;
    private Class returnClass;
    private int keySize;

    @Override
    public synchronized Serializable generate(final SharedSessionContractImplementor session, Object obj) {
        DeprecationLogger.DEPRECATION_LOGGER.deprecatedTableGenerator(this.getClass().getName());
        final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry().getService(JdbcServices.class).getSqlStatementLogger();
        final SessionEventListenerManager statsCollector = session.getEventListenerManager();
        final AbstractReturningWork<IntegralDataTypeHolder> work = new AbstractReturningWork<IntegralDataTypeHolder>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
                int rows;
                IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder(MultipleHiLoPerTableGenerator.this.returnClass);
                do {
                    try (PreparedStatement queryPreparedStatement = MultipleHiLoPerTableGenerator.this.prepareStatement(connection, MultipleHiLoPerTableGenerator.this.query, statementLogger, statsCollector);){
                        ResultSet rs = MultipleHiLoPerTableGenerator.this.executeQuery(queryPreparedStatement, statsCollector);
                        boolean isInitialized = rs.next();
                        if (!isInitialized) {
                            value.initialize(0L);
                            try (PreparedStatement insertPreparedStatement = MultipleHiLoPerTableGenerator.this.prepareStatement(connection, MultipleHiLoPerTableGenerator.this.insert, statementLogger, statsCollector);){
                                value.bind(insertPreparedStatement, 1);
                                MultipleHiLoPerTableGenerator.this.executeUpdate(insertPreparedStatement, statsCollector);
                            }
                        } else {
                            value.initialize(rs, 0L);
                        }
                        rs.close();
                    }
                    try (PreparedStatement updatePreparedStatement = MultipleHiLoPerTableGenerator.this.prepareStatement(connection, MultipleHiLoPerTableGenerator.this.update, statementLogger, statsCollector);){
                        value.copy().increment().bind(updatePreparedStatement, 1);
                        value.bind(updatePreparedStatement, 2);
                        rows = MultipleHiLoPerTableGenerator.this.executeUpdate(updatePreparedStatement, statsCollector);
                    }
                } while (rows == 0);
                return value;
            }
        };
        if (this.maxLo < 1) {
            IntegralDataTypeHolder value = null;
            while (value == null || value.lt(1L)) {
                value = session.getTransactionCoordinator().createIsolationDelegate().delegateWork(work, true);
            }
            return value.makeValue();
        }
        return this.hiloOptimizer.generate(new AccessCallback(){

            @Override
            public IntegralDataTypeHolder getNextValue() {
                return (IntegralDataTypeHolder)session.getTransactionCoordinator().createIsolationDelegate().delegateWork(work, true);
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
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        this.returnClass = type.getReturnedClass();
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        this.qualifiedTableName = this.determineGeneratorTableName(params, jdbcEnvironment);
        this.segmentColumnName = this.determineSegmentColumnName(params, jdbcEnvironment);
        this.keySize = ConfigurationHelper.getInt(PK_LENGTH_NAME, params, 255);
        this.segmentName = ConfigurationHelper.getString(PK_VALUE_NAME, params, params.getProperty("target_table"));
        this.valueColumnName = this.determineValueColumnName(params, jdbcEnvironment);
        this.maxLo = ConfigurationHelper.getInt(MAX_LO, params, Short.MAX_VALUE);
        if (this.maxLo >= 1) {
            this.hiloOptimizer = new LegacyHiLoAlgorithmOptimizer(this.returnClass, this.maxLo);
        }
    }

    protected QualifiedName determineGeneratorTableName(Properties params, JdbcEnvironment jdbcEnvironment) {
        String tableName = ConfigurationHelper.getString(ID_TABLE, params, DEFAULT_TABLE);
        if (tableName.contains(".")) {
            return QualifiedNameParser.INSTANCE.parse(tableName);
        }
        Identifier catalog = jdbcEnvironment.getIdentifierHelper().toIdentifier(ConfigurationHelper.getString("catalog", params));
        Identifier schema = jdbcEnvironment.getIdentifierHelper().toIdentifier(ConfigurationHelper.getString("schema", params));
        return new QualifiedNameParser.NameParts(catalog, schema, jdbcEnvironment.getIdentifierHelper().toIdentifier(tableName));
    }

    protected String determineSegmentColumnName(Properties params, JdbcEnvironment jdbcEnvironment) {
        String name = ConfigurationHelper.getString(PK_COLUMN_NAME, params, DEFAULT_PK_COLUMN);
        return jdbcEnvironment.getIdentifierHelper().toIdentifier(name).render(jdbcEnvironment.getDialect());
    }

    protected String determineValueColumnName(Properties params, JdbcEnvironment jdbcEnvironment) {
        String name = ConfigurationHelper.getString(VALUE_COLUMN_NAME, params, DEFAULT_VALUE_COLUMN);
        return jdbcEnvironment.getIdentifierHelper().toIdentifier(name).render(jdbcEnvironment.getDialect());
    }

    @Override
    public void registerExportables(Database database) {
        Namespace namespace = database.locateNamespace(this.qualifiedTableName.getCatalogName(), this.qualifiedTableName.getSchemaName());
        Table table = namespace.locateTable(this.qualifiedTableName.getObjectName());
        if (table == null) {
            table = namespace.createTable(this.qualifiedTableName.getObjectName(), false);
            table.setPrimaryKey(new PrimaryKey(table));
            ExportableColumn pkColumn = new ExportableColumn(database, table, this.segmentColumnName, StringType.INSTANCE, database.getDialect().getTypeName(12, this.keySize, 0, 0));
            pkColumn.setNullable(false);
            table.addColumn(pkColumn);
            table.getPrimaryKey().addColumn(pkColumn);
            ExportableColumn valueColumn = new ExportableColumn(database, table, this.valueColumnName, LongType.INSTANCE);
            table.addColumn(valueColumn);
        }
        this.physicalTableName = table.getQualifiedTableName();
        JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
        Dialect dialect = jdbcEnvironment.getDialect();
        this.formattedTableNameForLegacyGetter = jdbcEnvironment.getQualifiedObjectNameFormatter().format(this.physicalTableName, dialect);
    }

    @Override
    public void initialize(SqlStringGenerationContext context) {
        String formattedPhysicalTableName = context.format(this.physicalTableName);
        this.query = "select " + this.valueColumnName + " from " + context.getDialect().appendLockHint(LockMode.PESSIMISTIC_WRITE, formattedPhysicalTableName) + " where " + this.segmentColumnName + " = '" + this.segmentName + "'" + context.getDialect().getForUpdateString();
        this.update = "update " + formattedPhysicalTableName + " set " + this.valueColumnName + " = ? where " + this.valueColumnName + " = ? and " + this.segmentColumnName + " = '" + this.segmentName + "'";
        this.insert = "insert into " + formattedPhysicalTableName + "(" + this.segmentColumnName + ", " + this.valueColumnName + ") values('" + this.segmentName + "', ?)";
    }

    @Override
    @Deprecated
    public Object generatorKey() {
        return this.formattedTableNameForLegacyGetter;
    }

    static /* synthetic */ CoreMessageLogger access$600() {
        return LOG;
    }

    static /* synthetic */ QualifiedName access$800(MultipleHiLoPerTableGenerator x0) {
        return x0.physicalTableName;
    }
}

