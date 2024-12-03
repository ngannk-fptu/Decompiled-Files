/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.MappingException;
import org.hibernate.boot.model.naming.ObjectNameNormalizer;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

@Deprecated
public class SequenceGenerator
implements PersistentIdentifierGenerator,
BulkInsertionCapableIdentifierGenerator {
    private static final Logger LOG = Logger.getLogger((String)SequenceGenerator.class.getName());
    public static final String SEQUENCE = "sequence";
    @Deprecated
    public static final String PARAMETERS = "parameters";
    private QualifiedName logicalQualifiedSequenceName;
    private QualifiedName physicalSequenceName;
    @Deprecated
    private String formattedSequenceNameForLegacyGetter;
    private Type identifierType;
    private String sql;

    protected Type getIdentifierType() {
        return this.identifierType;
    }

    @Override
    @Deprecated
    public Object generatorKey() {
        return this.getSequenceName();
    }

    @Deprecated
    public String getSequenceName() {
        return this.formattedSequenceNameForLegacyGetter;
    }

    public QualifiedName getPhysicalSequenceName() {
        return this.physicalSequenceName;
    }

    @Deprecated
    public String[] getAllSqlForTests() {
        return new String[]{this.sql};
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        DeprecationLogger.DEPRECATION_LOGGER.deprecatedSequenceGenerator(this.getClass().getName());
        this.identifierType = type;
        ObjectNameNormalizer normalizer = (ObjectNameNormalizer)params.get("identifier_normalizer");
        this.logicalQualifiedSequenceName = QualifiedNameParser.INSTANCE.parse(ConfigurationHelper.getString(SEQUENCE, params, "hibernate_sequence"), normalizer.normalizeIdentifierQuoting(params.getProperty("catalog")), normalizer.normalizeIdentifierQuoting(params.getProperty("schema")));
        if (params.containsKey(PARAMETERS)) {
            LOG.warn((Object)"Use of 'parameters' config setting is no longer supported; to specify initial-value or increment use the org.hibernate.id.enhanced.SequenceStyleGenerator generator instead.");
        }
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        return this.generateHolder(session).makeValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    protected IntegralDataTypeHolder generateHolder(SharedSessionContractImplementor session) {
        try {
            PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(this.sql);
            try {
                ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(st);
                try {
                    rs.next();
                    IntegralDataTypeHolder result = this.buildHolder();
                    result.initialize(rs, 1L);
                    LOG.debugf("Sequence identifier generated: %s", (Object)result);
                    IntegralDataTypeHolder integralDataTypeHolder = result;
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
                    return integralDataTypeHolder;
                }
                catch (Throwable throwable) {
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
                    throw throwable;
                }
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not get next sequence value", this.sql);
        }
    }

    protected IntegralDataTypeHolder buildHolder() {
        return IdentifierGeneratorHelper.getIntegralDataTypeHolder(this.identifierType.getReturnedClass());
    }

    @Override
    public boolean supportsBulkInsertionIdentifierGeneration() {
        return true;
    }

    @Override
    public String determineBulkInsertionIdentifierGenerationSelectFragment(SqlStringGenerationContext context) {
        return context.getDialect().getSelectSequenceNextValString(context.format(this.getPhysicalSequenceName()));
    }

    @Override
    public void registerExportables(Database database) {
        Namespace namespace = database.locateNamespace(this.logicalQualifiedSequenceName.getCatalogName(), this.logicalQualifiedSequenceName.getSchemaName());
        Sequence sequence = namespace.locateSequence(this.logicalQualifiedSequenceName.getObjectName());
        if (sequence != null) {
            sequence.validate(1, 1);
        } else {
            sequence = namespace.createSequence(this.logicalQualifiedSequenceName.getObjectName(), 1, 1);
        }
        this.physicalSequenceName = sequence.getName();
        JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
        this.formattedSequenceNameForLegacyGetter = jdbcEnvironment.getQualifiedObjectNameFormatter().format(this.physicalSequenceName, jdbcEnvironment.getDialect());
    }

    @Override
    public void initialize(SqlStringGenerationContext context) {
        String formattedPhysicalSequenceName = context.format(this.physicalSequenceName);
        this.sql = context.getDialect().getSequenceNextValString(formattedPhysicalSequenceName);
    }
}

