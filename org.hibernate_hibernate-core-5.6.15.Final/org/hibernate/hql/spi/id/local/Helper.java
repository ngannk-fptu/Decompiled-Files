/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.local;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLWarning;
import java.sql.Statement;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.IdTableInfoImpl;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jdbc.AbstractWork;

public class Helper {
    public static final Helper INSTANCE = new Helper();
    private static final CoreMessageLogger log = CoreLogging.messageLogger(Helper.class);
    private static SqlExceptionHelper.WarningHandler WARNING_HANDLER = new SqlExceptionHelper.WarningHandlerLoggingSupport(){

        @Override
        public boolean doProcess() {
            return log.isDebugEnabled();
        }

        @Override
        public void prepare(SQLWarning warning) {
            log.warningsCreatingTempTable(warning);
        }

        @Override
        protected void logWarning(String description, String message) {
            log.debug(description);
            log.debug(message);
        }
    };

    private Helper() {
    }

    public void createTempTable(IdTableInfoImpl idTableInfo, TempTableDdlTransactionHandling ddlTransactionHandling, SharedSessionContractImplementor session) {
        TemporaryTableCreationWork work = new TemporaryTableCreationWork(idTableInfo, session.getFactory());
        if (ddlTransactionHandling == TempTableDdlTransactionHandling.NONE) {
            Connection connection = session.getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
            work.execute(connection);
            session.getJdbcCoordinator().afterStatementExecution();
        } else {
            session.getTransactionCoordinator().createIsolationDelegate().delegateWork(work, ddlTransactionHandling == TempTableDdlTransactionHandling.ISOLATE_AND_TRANSACT);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void releaseTempTable(IdTableInfoImpl idTableInfo, AfterUseAction afterUseAction, TempTableDdlTransactionHandling ddlTransactionHandling, SharedSessionContractImplementor session) {
        if (afterUseAction == AfterUseAction.NONE) {
            return;
        }
        if (afterUseAction == AfterUseAction.DROP) {
            TemporaryTableDropWork work = new TemporaryTableDropWork(idTableInfo, session.getFactory());
            if (ddlTransactionHandling == TempTableDdlTransactionHandling.NONE) {
                Connection connection = session.getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
                work.execute(connection);
                session.getJdbcCoordinator().afterStatementExecution();
            } else {
                session.getTransactionCoordinator().createIsolationDelegate().delegateWork(work, ddlTransactionHandling == TempTableDdlTransactionHandling.ISOLATE_AND_TRANSACT);
            }
        }
        if (afterUseAction != AfterUseAction.CLEAN) return;
        PreparedStatement ps = null;
        try {
            String sql22 = "delete from " + idTableInfo.getQualifiedIdTableName();
            ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql22, false);
            session.getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
            if (ps == null) return;
        }
        catch (Throwable t) {
            try {
                log.unableToCleanupTemporaryIdTable(t);
                return;
            }
            catch (Throwable throwable) {
                throw throwable;
            }
            finally {
                if (ps != null) {
                    try {
                        session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                    }
                    catch (Throwable throwable) {}
                }
            }
        }
        try {
            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
            return;
        }
        catch (Throwable sql22) {
            return;
        }
    }

    private static String logStatement(SessionFactoryImplementor factory, String sql) {
        SqlStatementLogger statementLogger = factory.getServiceRegistry().getService(JdbcServices.class).getSqlStatementLogger();
        statementLogger.logStatement(sql, FormatStyle.BASIC.getFormatter());
        return sql;
    }

    private static class TemporaryTableDropWork
    extends AbstractWork {
        private final IdTableInfoImpl idTableInfo;
        private final SessionFactoryImplementor factory;

        private TemporaryTableDropWork(IdTableInfoImpl idTableInfo, SessionFactoryImplementor factory) {
            this.idTableInfo = idTableInfo;
            this.factory = factory;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void execute(Connection connection) {
            try {
                Statement statement = connection.createStatement();
                try {
                    statement.executeUpdate(Helper.logStatement(this.factory, this.idTableInfo.getIdTableDropStatement()));
                    this.factory.getServiceRegistry().getService(JdbcServices.class).getSqlExceptionHelper().handleAndClearWarnings(statement, WARNING_HANDLER);
                }
                finally {
                    try {
                        statement.close();
                    }
                    catch (Throwable throwable) {}
                }
            }
            catch (Exception e) {
                log.warn("unable to drop temporary id table after use [" + e.getMessage() + "]");
            }
        }
    }

    private static class TemporaryTableCreationWork
    extends AbstractWork {
        private final IdTableInfoImpl idTableInfo;
        private final SessionFactoryImplementor factory;

        private TemporaryTableCreationWork(IdTableInfoImpl idTableInfo, SessionFactoryImplementor factory) {
            this.idTableInfo = idTableInfo;
            this.factory = factory;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void execute(Connection connection) {
            try {
                Statement statement = connection.createStatement();
                try {
                    statement.executeUpdate(Helper.logStatement(this.factory, this.idTableInfo.getIdTableCreationStatement()));
                    this.factory.getServiceRegistry().getService(JdbcServices.class).getSqlExceptionHelper().handleAndClearWarnings(statement, WARNING_HANDLER);
                }
                finally {
                    try {
                        statement.close();
                    }
                    catch (Throwable throwable) {}
                }
            }
            catch (Exception e) {
                log.debug("unable to create temporary id table [" + e.getMessage() + "]");
            }
        }
    }
}

