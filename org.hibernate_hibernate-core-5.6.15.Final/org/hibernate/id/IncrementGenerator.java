/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.model.naming.ObjectNameNormalizer;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class IncrementGenerator
implements IdentifierGenerator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(IncrementGenerator.class);
    private Class returnClass;
    private String column;
    private List<QualifiedTableName> physicalTableNames;
    private String sql;
    private IntegralDataTypeHolder previousValueHolder;

    @Deprecated
    public String[] getAllSqlForTests() {
        return new String[]{this.sql};
    }

    @Override
    public synchronized Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        if (this.sql != null) {
            this.initializePreviousValueHolder(session);
        }
        return this.previousValueHolder.makeValueThenIncrement();
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        this.returnClass = type.getReturnedClass();
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        ObjectNameNormalizer normalizer = (ObjectNameNormalizer)params.get("identifier_normalizer");
        this.column = params.getProperty("column");
        if (this.column == null) {
            this.column = params.getProperty("target_column");
        }
        this.column = normalizer.normalizeIdentifierQuoting(this.column).render(jdbcEnvironment.getDialect());
        IdentifierHelper identifierHelper = jdbcEnvironment.getIdentifierHelper();
        String schema = normalizer.toDatabaseIdentifierText(params.getProperty("schema"));
        String catalog = normalizer.toDatabaseIdentifierText(params.getProperty("catalog"));
        String tableList = params.getProperty("tables");
        if (tableList == null) {
            tableList = params.getProperty("identity_tables");
        }
        this.physicalTableNames = new ArrayList<QualifiedTableName>();
        for (String tableName : StringHelper.split(", ", tableList)) {
            this.physicalTableNames.add(new QualifiedTableName(identifierHelper.toIdentifier(catalog), identifierHelper.toIdentifier(schema), identifierHelper.toIdentifier(tableName)));
        }
    }

    @Override
    public void initialize(SqlStringGenerationContext context) {
        String maxColumn;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.physicalTableNames.size(); ++i) {
            String tableName = context.format(this.physicalTableNames.get(i));
            if (this.physicalTableNames.size() > 1) {
                buf.append("select max(").append(this.column).append(") as mx from ");
            }
            buf.append(tableName);
            if (i >= this.physicalTableNames.size() - 1) continue;
            buf.append(" union ");
        }
        if (this.physicalTableNames.size() > 1) {
            buf.insert(0, "( ").append(" ) ids_");
            maxColumn = "ids_.mx";
        } else {
            maxColumn = this.column;
        }
        this.sql = "select max(" + maxColumn + ") from " + buf.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initializePreviousValueHolder(SharedSessionContractImplementor session) {
        this.previousValueHolder = IdentifierGeneratorHelper.getIntegralDataTypeHolder(this.returnClass);
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Fetching initial value: %s", this.sql);
        }
        try {
            PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(this.sql);
            try {
                ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(st);
                try {
                    if (rs.next()) {
                        this.previousValueHolder.initialize(rs, 0L).increment();
                    } else {
                        this.previousValueHolder.initialize(1L);
                    }
                    this.sql = null;
                    if (LOG.isDebugEnabled()) {
                        LOG.debugf("First free id: %s", this.previousValueHolder.makeValue());
                    }
                }
                finally {
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
                }
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not fetch initial value for increment generator", this.sql);
        }
    }
}

