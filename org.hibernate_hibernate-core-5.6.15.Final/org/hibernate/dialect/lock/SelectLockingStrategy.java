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
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.SimpleSelect;
import org.hibernate.stat.spi.StatisticsImplementor;

public class SelectLockingStrategy
extends AbstractSelectLockingStrategy {
    public SelectLockingStrategy(Lockable lockable, LockMode lockMode) {
        super(lockable, lockMode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) throws StaleObjectStateException, JDBCException {
        String sql = this.determineSql(timeout);
        SessionFactoryImplementor factory = session.getFactory();
        Lockable lockable = this.getLockable();
        try {
            JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
            PreparedStatement st = jdbcCoordinator.getStatementPreparer().prepareStatement(sql);
            try {
                lockable.getIdentifierType().nullSafeSet(st, id, 1, session);
                if (lockable.isVersioned()) {
                    lockable.getVersionType().nullSafeSet(st, version, lockable.getIdentifierType().getColumnSpan(factory) + 1, session);
                }
                ResultSet rs = jdbcCoordinator.getResultSetReturn().extract(st);
                try {
                    if (!rs.next()) {
                        StatisticsImplementor statistics = factory.getStatistics();
                        if (statistics.isStatisticsEnabled()) {
                            statistics.optimisticFailure(lockable.getEntityName());
                        }
                        throw new StaleObjectStateException(lockable.getEntityName(), id);
                    }
                }
                finally {
                    jdbcCoordinator.getLogicalConnection().getResourceRegistry().release(rs, st);
                }
            }
            finally {
                jdbcCoordinator.getLogicalConnection().getResourceRegistry().release(st);
                jdbcCoordinator.afterStatementExecution();
            }
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not lock: " + MessageHelper.infoString((EntityPersister)lockable, id, session.getFactory()), sql);
        }
    }

    @Override
    protected String generateLockString(int timeout) {
        SessionFactoryImplementor factory = this.getLockable().getFactory();
        LockOptions lockOptions = new LockOptions(this.getLockMode());
        lockOptions.setTimeOut(timeout);
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

