/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.lock;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.JDBCException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.StaleObjectStateException;
import org.hibernate.dialect.lock.AbstractSelectLockingStrategy;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.SimpleSelect;
import org.hibernate.stat.spi.StatisticsImplementor;

public class PessimisticReadSelectLockingStrategy
extends AbstractSelectLockingStrategy {
    public PessimisticReadSelectLockingStrategy(Lockable lockable, LockMode lockMode) {
        super(lockable, lockMode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
        String sql = this.determineSql(timeout);
        SessionFactoryImplementor factory = session.getFactory();
        try {
            Lockable lockable = this.getLockable();
            try {
                JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
                PreparedStatement st = jdbcCoordinator.getStatementPreparer().prepareStatement(sql);
                try {
                    ResultSet rs;
                    lockable.getIdentifierType().nullSafeSet(st, id, 1, session);
                    if (lockable.isVersioned()) {
                        lockable.getVersionType().nullSafeSet(st, version, lockable.getIdentifierType().getColumnSpan(factory) + 1, session);
                    }
                    if (!(rs = jdbcCoordinator.getResultSetReturn().extract(st)).next()) {
                        StatisticsImplementor statistics = factory.getStatistics();
                        if (statistics.isStatisticsEnabled()) {
                            statistics.optimisticFailure(lockable.getEntityName());
                        }
                        throw new StaleObjectStateException(lockable.getEntityName(), id);
                    }
                }
                finally {
                    jdbcCoordinator.getLogicalConnection().getResourceRegistry().release(st);
                    jdbcCoordinator.afterStatementExecution();
                }
            }
            catch (SQLException e) {
                throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "could not lock: " + MessageHelper.infoString((EntityPersister)lockable, id, session.getFactory()), sql);
            }
        }
        catch (JDBCException e) {
            throw new PessimisticEntityLockException(object, "could not obtain pessimistic lock", e);
        }
    }

    @Override
    protected String generateLockString(int lockTimeout) {
        SessionFactoryImplementor factory = this.getLockable().getFactory();
        LockOptions lockOptions = new LockOptions(this.getLockMode());
        lockOptions.setTimeOut(lockTimeout);
        SimpleSelect select = new SimpleSelect(factory.getDialect()).setLockOptions(lockOptions).setTableName(this.getLockable().getRootTableName()).addColumn(this.getLockable().getRootTableIdentifierColumnNames()[0]).addCondition(this.getLockable().getRootTableIdentifierColumnNames(), "=?");
        if (this.getLockable().isVersioned()) {
            select.addCondition(this.getLockable().getVersionColumnName(), "=?");
        }
        if (factory.getSessionFactoryOptions().isCommentsEnabled()) {
            select.setComment((Object)((Object)this.getLockMode()) + " lock " + this.getLockable().getEntityName());
        }
        return select.toStatementString();
    }
}

