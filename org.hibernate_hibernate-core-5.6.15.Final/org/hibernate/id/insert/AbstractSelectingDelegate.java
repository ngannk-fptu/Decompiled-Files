/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.insert;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.insert.Binder;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.pretty.MessageHelper;

public abstract class AbstractSelectingDelegate
implements InsertGeneratedIdentifierDelegate {
    private final PostInsertIdentityPersister persister;

    protected AbstractSelectingDelegate(PostInsertIdentityPersister persister) {
        this.persister = persister;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    @Override
    public final Serializable performInsert(String insertSQL, SharedSessionContractImplementor session, Binder binder) {
        try {
            PreparedStatement insert = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(insertSQL, 2);
            try {
                binder.bindValues(insert);
                session.getJdbcCoordinator().getResultSetReturn().executeUpdate(insert);
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(insert);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not insert: " + MessageHelper.infoString(this.persister), insertSQL);
        }
        String selectSQL = this.getSelectSQL();
        try {
            PreparedStatement idSelect = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(selectSQL, false);
            try {
                this.bindParameters(session, idSelect, binder.getEntity());
                ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(idSelect);
                try {
                    Serializable serializable = this.getResult(session, rs, binder.getEntity());
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, idSelect);
                    return serializable;
                }
                catch (Throwable throwable) {
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, idSelect);
                    throw throwable;
                }
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(idSelect);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not retrieve generated id after insert: " + MessageHelper.infoString(this.persister), insertSQL);
        }
    }

    protected abstract String getSelectSQL();

    protected void bindParameters(SharedSessionContractImplementor session, PreparedStatement ps, Object entity) throws SQLException {
    }

    protected abstract Serializable getResult(SharedSessionContractImplementor var1, ResultSet var2, Object var3) throws SQLException;
}

