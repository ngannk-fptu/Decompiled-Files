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
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.Update;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.jboss.logging.Logger;

public class PessimisticWriteUpdateLockingStrategy
implements LockingStrategy {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)PessimisticWriteUpdateLockingStrategy.class.getName());
    private final Lockable lockable;
    private final LockMode lockMode;
    private final String sql;

    public PessimisticWriteUpdateLockingStrategy(Lockable lockable, LockMode lockMode) {
        this.lockable = lockable;
        this.lockMode = lockMode;
        if (lockMode.lessThan(LockMode.PESSIMISTIC_READ)) {
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
    public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
        if (!this.lockable.isVersioned()) {
            throw new HibernateException("write locks via update not supported for non-versioned entities [" + this.lockable.getEntityName() + "]");
        }
        SessionFactoryImplementor factory = session.getFactory();
        try {
            try {
                JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
                PreparedStatement st = jdbcCoordinator.getStatementPreparer().prepareStatement(this.sql);
                try {
                    int affected;
                    this.lockable.getVersionType().nullSafeSet(st, version, 1, session);
                    int offset = 2;
                    this.lockable.getIdentifierType().nullSafeSet(st, id, offset, session);
                    offset += this.lockable.getIdentifierType().getColumnSpan(factory);
                    if (this.lockable.isVersioned()) {
                        this.lockable.getVersionType().nullSafeSet(st, version, offset, session);
                    }
                    if ((affected = jdbcCoordinator.getResultSetReturn().executeUpdate(st)) < 0) {
                        StatisticsImplementor statistics = factory.getStatistics();
                        if (statistics.isStatisticsEnabled()) {
                            statistics.optimisticFailure(this.lockable.getEntityName());
                        }
                        throw new StaleObjectStateException(this.lockable.getEntityName(), id);
                    }
                }
                finally {
                    jdbcCoordinator.getLogicalConnection().getResourceRegistry().release(st);
                    jdbcCoordinator.afterStatementExecution();
                }
            }
            catch (SQLException e) {
                throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "could not lock: " + MessageHelper.infoString((EntityPersister)this.lockable, id, session.getFactory()), this.sql);
            }
        }
        catch (JDBCException e) {
            throw new PessimisticEntityLockException(object, "could not obtain pessimistic lock", e);
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

