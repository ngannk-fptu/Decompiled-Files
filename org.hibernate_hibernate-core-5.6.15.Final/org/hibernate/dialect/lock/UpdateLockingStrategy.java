/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.dialect.lock;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.LockMode;
import org.hibernate.StaleObjectStateException;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.Update;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;
import org.jboss.logging.Logger;

public class UpdateLockingStrategy
implements LockingStrategy {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)UpdateLockingStrategy.class.getName());
    private final Lockable lockable;
    private final LockMode lockMode;
    private final String sql;

    public UpdateLockingStrategy(Lockable lockable, LockMode lockMode) {
        this.lockable = lockable;
        this.lockMode = lockMode;
        if (lockMode.lessThan(LockMode.UPGRADE)) {
            throw new HibernateException("[" + (Object)((Object)lockMode) + "] not valid for update statement");
        }
        if (!lockable.isVersioned()) {
            LOG.writeLocksNotSupported(lockable.getEntityName());
            this.sql = null;
        } else {
            this.sql = this.generateLockString();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) throws StaleObjectStateException, JDBCException {
        String lockableEntityName = this.lockable.getEntityName();
        if (!this.lockable.isVersioned()) {
            throw new HibernateException("write locks via update not supported for non-versioned entities [" + lockableEntityName + "]");
        }
        SessionFactoryImplementor factory = session.getFactory();
        try {
            JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
            PreparedStatement st = jdbcCoordinator.getStatementPreparer().prepareStatement(this.sql);
            try {
                int affected;
                VersionType lockableVersionType = this.lockable.getVersionType();
                lockableVersionType.nullSafeSet(st, version, 1, session);
                int offset = 2;
                Type lockableIdentifierType = this.lockable.getIdentifierType();
                lockableIdentifierType.nullSafeSet(st, id, offset, session);
                offset += lockableIdentifierType.getColumnSpan(factory);
                if (this.lockable.isVersioned()) {
                    lockableVersionType.nullSafeSet(st, version, offset, session);
                }
                if ((affected = jdbcCoordinator.getResultSetReturn().executeUpdate(st)) < 0) {
                    StatisticsImplementor statistics = factory.getStatistics();
                    if (statistics.isStatisticsEnabled()) {
                        statistics.optimisticFailure(lockableEntityName);
                    }
                    throw new StaleObjectStateException(lockableEntityName, id);
                }
            }
            finally {
                jdbcCoordinator.getLogicalConnection().getResourceRegistry().release(st);
                jdbcCoordinator.afterStatementExecution();
            }
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not lock: " + MessageHelper.infoString((EntityPersister)this.lockable, id, session.getFactory()), this.sql);
        }
    }

    protected String generateLockString() {
        SessionFactoryImplementor factory = this.lockable.getFactory();
        Update update = new Update(factory.getDialect());
        update.setTableName(this.lockable.getRootTableName());
        update.addPrimaryKeyColumns(this.lockable.getRootTableIdentifierColumnNames());
        update.setVersionColumnName(this.lockable.getVersionColumnName());
        update.addColumn(this.lockable.getVersionColumnName());
        if (factory.getSessionFactoryOptions().isCommentsEnabled()) {
            update.setComment((Object)((Object)this.lockMode) + " lock " + this.lockable.getEntityName());
        }
        return update.toStatementString();
    }

    protected LockMode getLockMode() {
        return this.lockMode;
    }
}

