/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.UUIDGenerationStrategy;
import org.hibernate.id.UUIDGenerator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class GUIDGenerator
implements IdentifierGenerator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(GUIDGenerator.class);
    private static boolean WARNED;

    public GUIDGenerator() {
        if (!WARNED) {
            WARNED = true;
            LOG.deprecatedUuidGenerator(UUIDGenerator.class.getName(), UUIDGenerationStrategy.class.getName());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        String sql = session.getJdbcServices().getJdbcEnvironment().getDialect().getSelectGUIDString();
        PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql);
        try {
            String result;
            ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(st);
            try {
                if (!rs.next()) {
                    throw new HibernateException("The database returned no GUID identity value");
                }
                result = rs.getString(1);
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
            }
            LOG.guidGenerated(result);
            String string = result;
            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
            session.getJdbcCoordinator().afterStatementExecution();
            return string;
        }
        catch (Throwable throwable) {
            try {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                session.getJdbcCoordinator().afterStatementExecution();
                throw throwable;
            }
            catch (SQLException sqle) {
                throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not retrieve GUID", sql);
            }
        }
    }
}

