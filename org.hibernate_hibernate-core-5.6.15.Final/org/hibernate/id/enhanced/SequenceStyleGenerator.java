/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.id.enhanced;

import java.io.Serializable;
import java.util.Objects;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceMismatchStrategy;
import org.hibernate.id.enhanced.DatabaseStructure;
import org.hibernate.id.enhanced.NoopOptimizer;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.SequenceStructure;
import org.hibernate.id.enhanced.StandardOptimizerDescriptor;
import org.hibernate.id.enhanced.TableStructure;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class SequenceStyleGenerator
implements PersistentIdentifierGenerator,
BulkInsertionCapableIdentifierGenerator {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)SequenceStyleGenerator.class.getName());
    public static final String SEQUENCE_PARAM = "sequence_name";
    public static final String DEF_SEQUENCE_NAME = "hibernate_sequence";
    public static final String INITIAL_PARAM = "initial_value";
    public static final int DEFAULT_INITIAL_VALUE = 1;
    public static final String INCREMENT_PARAM = "increment_size";
    public static final int DEFAULT_INCREMENT_SIZE = 1;
    public static final String CONFIG_PREFER_SEQUENCE_PER_ENTITY = "prefer_sequence_per_entity";
    public static final String CONFIG_SEQUENCE_PER_ENTITY_SUFFIX = "sequence_per_entity_suffix";
    public static final String DEF_SEQUENCE_SUFFIX = "_SEQ";
    public static final String OPT_PARAM = "optimizer";
    public static final String FORCE_TBL_PARAM = "force_table_use";
    public static final String VALUE_COLUMN_PARAM = "value_column";
    public static final String DEF_VALUE_COLUMN = "next_val";
    private DatabaseStructure databaseStructure;
    private Optimizer optimizer;
    private Type identifierType;

    public DatabaseStructure getDatabaseStructure() {
        return this.databaseStructure;
    }

    public Optimizer getOptimizer() {
        return this.optimizer;
    }

    public Type getIdentifierType() {
        return this.identifierType;
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        String databaseSequenceName;
        Long databaseIncrementValue;
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        ConfigurationService configurationService = serviceRegistry.getService(ConfigurationService.class);
        Dialect dialect = jdbcEnvironment.getDialect();
        this.identifierType = type;
        boolean forceTableUse = ConfigurationHelper.getBoolean(FORCE_TBL_PARAM, params, false);
        QualifiedName sequenceName = this.determineSequenceName(params, dialect, jdbcEnvironment, serviceRegistry);
        int initialValue = this.determineInitialValue(params);
        int incrementSize = this.determineIncrementSize(params);
        String optimizationStrategy = this.determineOptimizationStrategy(params, incrementSize);
        boolean isPooledOptimizer = OptimizerFactory.isPooledOptimizer(optimizationStrategy);
        SequenceMismatchStrategy sequenceMismatchStrategy = configurationService.getSetting("hibernate.id.sequence.increment_size_mismatch_strategy", SequenceMismatchStrategy::interpret, SequenceMismatchStrategy.EXCEPTION);
        if (sequenceMismatchStrategy != SequenceMismatchStrategy.NONE && isPooledOptimizer && this.isPhysicalSequence(jdbcEnvironment, forceTableUse) && (databaseIncrementValue = this.getSequenceIncrementValue(jdbcEnvironment, databaseSequenceName = sequenceName.getObjectName().getText())) != null && !databaseIncrementValue.equals(incrementSize)) {
            int dbIncrementValue = databaseIncrementValue.intValue();
            switch (sequenceMismatchStrategy) {
                case EXCEPTION: {
                    throw new MappingException(String.format("The increment size of the [%s] sequence is set to [%d] in the entity mapping while the associated database sequence increment size is [%d].", databaseSequenceName, incrementSize, dbIncrementValue));
                }
                case FIX: {
                    incrementSize = dbIncrementValue;
                }
                case LOG: {
                    LOG.sequenceIncrementSizeMismatch(databaseSequenceName, incrementSize, dbIncrementValue);
                }
            }
        }
        incrementSize = this.determineAdjustedIncrementSize(optimizationStrategy, incrementSize);
        if (dialect.supportsSequences() && !forceTableUse && !dialect.supportsPooledSequences() && isPooledOptimizer) {
            forceTableUse = true;
            LOG.forcingTableUse();
        }
        this.databaseStructure = this.buildDatabaseStructure(type, params, jdbcEnvironment, forceTableUse, sequenceName, initialValue, incrementSize);
        this.optimizer = OptimizerFactory.buildOptimizer(optimizationStrategy, this.identifierType.getReturnedClass(), incrementSize, ConfigurationHelper.getInt(INITIAL_PARAM, params, -1));
        this.databaseStructure.configure(this.optimizer);
    }

    @Override
    public void registerExportables(Database database) {
        this.databaseStructure.registerExportables(database);
    }

    @Override
    public void initialize(SqlStringGenerationContext context) {
        this.databaseStructure.initialize(context);
    }

    protected QualifiedName determineSequenceName(Properties params, Dialect dialect, JdbcEnvironment jdbcEnv, ServiceRegistry serviceRegistry) {
        String defaultSequenceName;
        String sequenceName;
        String generatorName;
        String sequencePerEntitySuffix = ConfigurationHelper.getString(CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, params, DEF_SEQUENCE_SUFFIX);
        String fallbackSequenceName = DEF_SEQUENCE_NAME;
        Boolean preferGeneratorNameAsDefaultName = serviceRegistry.getService(ConfigurationService.class).getSetting("hibernate.model.generator_name_as_sequence_name", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        if (preferGeneratorNameAsDefaultName.booleanValue() && StringHelper.isNotEmpty(generatorName = params.getProperty("GENERATOR_NAME"))) {
            fallbackSequenceName = generatorName;
        }
        if ((sequenceName = ConfigurationHelper.getString(SEQUENCE_PARAM, params, defaultSequenceName = ConfigurationHelper.getBoolean(CONFIG_PREFER_SEQUENCE_PER_ENTITY, params, false) ? params.getProperty("jpa_entity_name") + sequencePerEntitySuffix : fallbackSequenceName)).contains(".")) {
            return QualifiedNameParser.INSTANCE.parse(sequenceName);
        }
        Identifier catalog = jdbcEnv.getIdentifierHelper().toIdentifier(ConfigurationHelper.getString("catalog", params));
        Identifier schema = jdbcEnv.getIdentifierHelper().toIdentifier(ConfigurationHelper.getString("schema", params));
        return new QualifiedNameParser.NameParts(catalog, schema, jdbcEnv.getIdentifierHelper().toIdentifier(sequenceName));
    }

    protected Identifier determineValueColumnName(Properties params, JdbcEnvironment jdbcEnvironment) {
        String name = ConfigurationHelper.getString(VALUE_COLUMN_PARAM, params, DEF_VALUE_COLUMN);
        return jdbcEnvironment.getIdentifierHelper().toIdentifier(name);
    }

    protected int determineInitialValue(Properties params) {
        return ConfigurationHelper.getInt(INITIAL_PARAM, params, 1);
    }

    protected int determineIncrementSize(Properties params) {
        return ConfigurationHelper.getInt(INCREMENT_PARAM, params, 1);
    }

    protected String determineOptimizationStrategy(Properties params, int incrementSize) {
        return ConfigurationHelper.getString(OPT_PARAM, params, OptimizerFactory.determineImplicitOptimizerName(incrementSize, params));
    }

    protected int determineAdjustedIncrementSize(String optimizationStrategy, int incrementSize) {
        int resolvedIncrementSize;
        if (Math.abs(incrementSize) > 1 && StandardOptimizerDescriptor.NONE.getExternalName().equals(optimizationStrategy)) {
            if (incrementSize < -1) {
                resolvedIncrementSize = -1;
                LOG.honoringOptimizerSetting(StandardOptimizerDescriptor.NONE.getExternalName(), INCREMENT_PARAM, incrementSize, "negative", resolvedIncrementSize);
            } else {
                resolvedIncrementSize = 1;
                LOG.honoringOptimizerSetting(StandardOptimizerDescriptor.NONE.getExternalName(), INCREMENT_PARAM, incrementSize, "positive", resolvedIncrementSize);
            }
        } else {
            resolvedIncrementSize = incrementSize;
        }
        return resolvedIncrementSize;
    }

    protected DatabaseStructure buildDatabaseStructure(Type type, Properties params, JdbcEnvironment jdbcEnvironment, boolean forceTableUse, QualifiedName sequenceName, int initialValue, int incrementSize) {
        if (this.isPhysicalSequence(jdbcEnvironment, forceTableUse)) {
            return this.buildSequenceStructure(type, params, jdbcEnvironment, sequenceName, initialValue, incrementSize);
        }
        return this.buildTableStructure(type, params, jdbcEnvironment, sequenceName, initialValue, incrementSize);
    }

    protected boolean isPhysicalSequence(JdbcEnvironment jdbcEnvironment, boolean forceTableUse) {
        return jdbcEnvironment.getDialect().supportsSequences() && !forceTableUse;
    }

    protected DatabaseStructure buildSequenceStructure(Type type, Properties params, JdbcEnvironment jdbcEnvironment, QualifiedName sequenceName, int initialValue, int incrementSize) {
        return new SequenceStructure(jdbcEnvironment, sequenceName, initialValue, incrementSize, type.getReturnedClass());
    }

    protected DatabaseStructure buildTableStructure(Type type, Properties params, JdbcEnvironment jdbcEnvironment, QualifiedName sequenceName, int initialValue, int incrementSize) {
        Identifier valueColumnName = this.determineValueColumnName(params, jdbcEnvironment);
        return new TableStructure(jdbcEnvironment, sequenceName, valueColumnName, initialValue, incrementSize, type.getReturnedClass());
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return this.optimizer.generate(this.databaseStructure.buildCallback(session));
    }

    @Override
    @Deprecated
    public Object generatorKey() {
        return this.databaseStructure.getName();
    }

    @Override
    public boolean supportsBulkInsertionIdentifierGeneration() {
        return NoopOptimizer.class.isInstance(this.getOptimizer()) && this.getDatabaseStructure().isPhysicalSequence();
    }

    @Override
    public String determineBulkInsertionIdentifierGenerationSelectFragment(SqlStringGenerationContext context) {
        return context.getDialect().getSelectSequenceNextValString(context.format(this.getDatabaseStructure().getPhysicalName()));
    }

    private Long getSequenceIncrementValue(JdbcEnvironment jdbcEnvironment, String sequenceName) {
        return jdbcEnvironment.getExtractedDatabaseMetaData().getSequenceInformationList().stream().filter(sequenceInformation -> {
            Identifier catalog = sequenceInformation.getSequenceName().getCatalogName();
            Identifier schema = sequenceInformation.getSequenceName().getSchemaName();
            return !(!sequenceName.equalsIgnoreCase(sequenceInformation.getSequenceName().getSequenceName().getText()) || catalog != null && !catalog.equals(jdbcEnvironment.getCurrentCatalog()) || schema != null && !schema.equals(jdbcEnvironment.getCurrentSchema()));
        }).map(SequenceInformation::getIncrementValue).filter(Objects::nonNull).findFirst().orElse(null);
    }
}

