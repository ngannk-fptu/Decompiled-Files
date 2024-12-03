/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.TimestampType;
import org.jboss.logging.Logger;

public class DbTimestampType
extends TimestampType {
    public static final DbTimestampType INSTANCE = new DbTimestampType();
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)DbTimestampType.class.getName());

    @Override
    public String getName() {
        return "dbtimestamp";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName()};
    }

    @Override
    public Date seed(SharedSessionContractImplementor session) {
        if (session == null) {
            LOG.trace("Incoming session was null; using current jvm time");
            return super.seed(null);
        }
        if (!session.getJdbcServices().getJdbcEnvironment().getDialect().supportsCurrentTimestampSelection()) {
            LOG.debug("Falling back to vm-based timestamp, as dialect does not support current timestamp selection");
            return super.seed(session);
        }
        return this.getCurrentTimestamp(session);
    }

    private Date getCurrentTimestamp(SharedSessionContractImplementor session) {
        Dialect dialect = session.getJdbcServices().getJdbcEnvironment().getDialect();
        String timestampSelectString = dialect.getCurrentTimestampSelectString();
        if (dialect.isCurrentTimestampSelectStringCallable()) {
            return this.useCallableStatement(timestampSelectString, session);
        }
        return this.usePreparedStatement(timestampSelectString, session);
    }

    private Timestamp usePreparedStatement(String timestampSelectString, SharedSessionContractImplementor session) {
        PreparedStatement ps = null;
        try {
            ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(timestampSelectString, false);
            ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(ps);
            rs.next();
            Timestamp ts = rs.getTimestamp(1);
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Current timestamp retrieved from db : {0} (nanos={1}, time={2})", ts, ts.getNanos(), ts.getTime());
            }
            Timestamp timestamp = ts;
            return timestamp;
        }
        catch (SQLException e) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "could not select current db timestamp", timestampSelectString);
        }
        finally {
            if (ps != null) {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
    }

    private Timestamp useCallableStatement(String callString, SharedSessionContractImplementor session) {
        CallableStatement cs = null;
        try {
            cs = (CallableStatement)session.getJdbcCoordinator().getStatementPreparer().prepareStatement(callString, true);
            cs.registerOutParameter(1, 93);
            session.getJdbcCoordinator().getResultSetReturn().execute(cs);
            Timestamp ts = cs.getTimestamp(1);
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Current timestamp retrieved from db : {0} (nanos={1}, time={2})", ts, ts.getNanos(), ts.getTime());
            }
            Timestamp timestamp = ts;
            return timestamp;
        }
        catch (SQLException e) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "could not call current db timestamp function", callString);
        }
        finally {
            if (cs != null) {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(cs);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
    }
}

