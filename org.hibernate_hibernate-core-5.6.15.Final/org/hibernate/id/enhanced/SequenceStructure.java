/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.id.enhanced;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.AssertionFailure;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.DatabaseStructure;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class SequenceStructure
implements DatabaseStructure {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)SequenceStructure.class.getName());
    private final QualifiedName logicalQualifiedSequenceName;
    private final int initialValue;
    private final int incrementSize;
    private final Class numberType;
    private String sql;
    private boolean applyIncrementSizeToSourceValues;
    private int accessCounter;
    @Deprecated
    private String formattedSequenceNameForLegacyGetter;
    protected QualifiedName physicalSequenceName;

    public SequenceStructure(JdbcEnvironment jdbcEnvironment, QualifiedName qualifiedSequenceName, int initialValue, int incrementSize, Class numberType) {
        this.logicalQualifiedSequenceName = qualifiedSequenceName;
        this.initialValue = initialValue;
        this.incrementSize = incrementSize;
        this.numberType = numberType;
    }

    @Override
    @Deprecated
    public String getName() {
        return this.formattedSequenceNameForLegacyGetter;
    }

    @Override
    public QualifiedName getPhysicalName() {
        return this.physicalSequenceName;
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
    public int getInitialValue() {
        return this.initialValue;
    }

    @Override
    public String[] getAllSqlForTests() {
        return new String[]{this.sql};
    }

    @Override
    public AccessCallback buildCallback(final SharedSessionContractImplementor session) {
        if (this.sql == null) {
            throw new AssertionFailure("SequenceStyleGenerator's SequenceStructure was not properly initialized");
        }
        return new AccessCallback(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Enabled aggressive exception aggregation
             */
            @Override
            public IntegralDataTypeHolder getNextValue() {
                SequenceStructure.this.accessCounter++;
                try {
                    PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(SequenceStructure.this.sql);
                    try {
                        IntegralDataTypeHolder integralDataTypeHolder;
                        ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(st);
                        try {
                            rs.next();
                            IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder(SequenceStructure.this.numberType);
                            value.initialize(rs, 1L);
                            if (LOG.isDebugEnabled()) {
                                LOG.debugf("Sequence value obtained: %s", value.makeValue());
                            }
                            integralDataTypeHolder = value;
                        }
                        catch (Throwable throwable) {
                            try {
                                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
                            }
                            catch (Throwable throwable2) {
                                // empty catch block
                            }
                            throw throwable;
                        }
                        try {
                            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                        return integralDataTypeHolder;
                    }
                    finally {
                        session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                        session.getJdbcCoordinator().afterStatementExecution();
                    }
                }
                catch (SQLException sqle) {
                    throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not get next sequence value", SequenceStructure.this.sql);
                }
            }

            @Override
            public String getTenantIdentifier() {
                return session.getTenantIdentifier();
            }
        };
    }

    @Override
    public void configure(Optimizer optimizer) {
        this.applyIncrementSizeToSourceValues = optimizer.applyIncrementSizeToSourceValues();
    }

    @Override
    public void registerExportables(Database database) {
        this.buildSequence(database);
    }

    @Override
    public void initialize(SqlStringGenerationContext context) {
        this.sql = context.getDialect().getSequenceNextValString(context.format(this.physicalSequenceName));
    }

    @Override
    public boolean isPhysicalSequence() {
        return true;
    }

    protected final int getSourceIncrementSize() {
        return this.applyIncrementSizeToSourceValues ? this.incrementSize : 1;
    }

    protected QualifiedName getQualifiedName() {
        return this.logicalQualifiedSequenceName;
    }

    protected void buildSequence(Database database) {
        int sourceIncrementSize = this.getSourceIncrementSize();
        Namespace namespace = database.locateNamespace(this.logicalQualifiedSequenceName.getCatalogName(), this.logicalQualifiedSequenceName.getSchemaName());
        Sequence sequence = namespace.locateSequence(this.logicalQualifiedSequenceName.getObjectName());
        if (sequence != null) {
            sequence.validate(this.initialValue, sourceIncrementSize);
        } else {
            sequence = namespace.createSequence(this.logicalQualifiedSequenceName.getObjectName(), this.initialValue, sourceIncrementSize);
        }
        this.physicalSequenceName = sequence.getName();
        JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
        this.formattedSequenceNameForLegacyGetter = jdbcEnvironment.getQualifiedObjectNameFormatter().format(this.physicalSequenceName, jdbcEnvironment.getDialect());
    }
}

