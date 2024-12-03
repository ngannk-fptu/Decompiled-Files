/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.insert;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.insert.Binder;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.pretty.MessageHelper;

public abstract class AbstractReturningDelegate
implements InsertGeneratedIdentifierDelegate {
    private final PostInsertIdentityPersister persister;

    public AbstractReturningDelegate(PostInsertIdentityPersister persister) {
        this.persister = persister;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final Serializable performInsert(String insertSQL, SharedSessionContractImplementor session, Binder binder) {
        PreparedStatement insert = this.prepare(insertSQL, session);
        try {
            binder.bindValues(insert);
            Serializable serializable = this.executeAndExtract(insert, session);
            this.releaseStatement(insert, session);
            return serializable;
        }
        catch (Throwable throwable) {
            try {
                this.releaseStatement(insert, session);
                throw throwable;
            }
            catch (SQLException sqle) {
                throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not insert: " + MessageHelper.infoString(this.persister), insertSQL);
            }
        }
    }

    protected PostInsertIdentityPersister getPersister() {
        return this.persister;
    }

    protected abstract PreparedStatement prepare(String var1, SharedSessionContractImplementor var2) throws SQLException;

    protected abstract Serializable executeAndExtract(PreparedStatement var1, SharedSessionContractImplementor var2) throws SQLException;

    protected void releaseStatement(PreparedStatement insert, SharedSessionContractImplementor session) {
        JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
        jdbcCoordinator.getLogicalConnection().getResourceRegistry().release(insert);
        jdbcCoordinator.afterStatementExecution();
    }
}

