/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.engine.HibernateIterator;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.hql.internal.HolderInstantiator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public final class IteratorImpl
implements HibernateIterator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(IteratorImpl.class);
    private ResultSet rs;
    private final EventSource session;
    private boolean readOnly;
    private final Type[] types;
    private final boolean single;
    private Object currentResult;
    private boolean hasNext;
    private final String[][] names;
    private PreparedStatement ps;
    private HolderInstantiator holderInstantiator;

    public IteratorImpl(ResultSet rs, PreparedStatement ps, EventSource sess, boolean readOnly, Type[] types, String[][] columnNames, HolderInstantiator holderInstantiator) throws HibernateException, SQLException {
        this.rs = rs;
        this.ps = ps;
        this.session = sess;
        this.readOnly = readOnly;
        this.types = types;
        this.names = columnNames;
        this.holderInstantiator = holderInstantiator;
        this.single = types.length == 1;
        this.postNext();
    }

    @Override
    public void close() throws JDBCException {
        if (this.ps != null) {
            LOG.debug("Closing iterator");
            JdbcCoordinator jdbcCoordinator = this.session.getJdbcCoordinator();
            jdbcCoordinator.getResourceRegistry().release(this.ps);
            try {
                this.session.getPersistenceContext().getLoadContexts().cleanup(this.rs);
            }
            catch (Throwable ignore) {
                LOG.debugf("Exception trying to cleanup load context : %s", ignore.getMessage());
            }
            jdbcCoordinator.afterStatementExecution();
            this.ps = null;
            this.rs = null;
            this.hasNext = false;
        }
    }

    private void postNext() throws SQLException {
        LOG.debug("Attempting to retrieve next results");
        this.hasNext = this.rs.next();
        if (!this.hasNext) {
            LOG.debug("Exhausted results");
            this.close();
        } else {
            LOG.debug("Retrieved next results");
        }
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    public Object next() throws HibernateException {
        if (!this.hasNext) {
            throw new NoSuchElementException("No more results");
        }
        boolean sessionDefaultReadOnlyOrig = this.session.isDefaultReadOnly();
        this.session.setDefaultReadOnly(this.readOnly);
        try {
            boolean isHolder = this.holderInstantiator.isRequired();
            LOG.debug("Assembling results");
            if (this.single && !isHolder) {
                this.currentResult = this.types[0].nullSafeGet(this.rs, this.names[0], (SharedSessionContractImplementor)this.session, null);
            } else {
                Object[] currentResults = new Object[this.types.length];
                for (int i = 0; i < this.types.length; ++i) {
                    currentResults[i] = this.types[i].nullSafeGet(this.rs, this.names[i], (SharedSessionContractImplementor)this.session, null);
                }
                this.currentResult = isHolder ? this.holderInstantiator.instantiate(currentResults) : currentResults;
            }
            this.postNext();
            LOG.debug("Returning current results");
            Object object = this.currentResult;
            return object;
        }
        catch (SQLException sqle) {
            throw this.session.getFactory().getSQLExceptionHelper().convert(sqle, "could not get next iterator result");
        }
        finally {
            this.session.setDefaultReadOnly(sessionDefaultReadOnlyOrig);
        }
    }

    @Override
    public void remove() {
        if (!this.single) {
            throw new UnsupportedOperationException("Not a single column hibernate query result set");
        }
        if (this.currentResult == null) {
            throw new IllegalStateException("Called Iterator.remove() before next()");
        }
        if (!(this.types[0] instanceof EntityType)) {
            throw new UnsupportedOperationException("Not an entity");
        }
        this.session.delete(((EntityType)this.types[0]).getAssociatedEntityName(), this.currentResult, false, null);
    }
}

