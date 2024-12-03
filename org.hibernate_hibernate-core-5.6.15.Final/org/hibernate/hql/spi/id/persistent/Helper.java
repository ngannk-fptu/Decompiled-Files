/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.persistent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.AbstractSessionImpl;
import org.hibernate.type.UUIDCharType;

public class Helper {
    public static final Helper INSTANCE = new Helper();
    public static final String SESSION_ID_COLUMN_NAME = "hib_sess_id";

    private Helper() {
    }

    public void bindSessionIdentifier(PreparedStatement ps, SharedSessionContractImplementor session, int position) throws SQLException {
        if (!AbstractSessionImpl.class.isInstance(session)) {
            throw new HibernateException("Only available on SessionImpl instances");
        }
        UUIDCharType.INSTANCE.set(ps, session.getSessionIdentifier(), position, session);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cleanUpRows(String tableName, SharedSessionContractImplementor session) {
        String sql = "delete from " + tableName + " where " + SESSION_ID_COLUMN_NAME + "=?";
        try {
            PreparedStatement ps = null;
            try {
                ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, false);
                this.bindSessionIdentifier(ps, session, 1);
                session.getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
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
        catch (SQLException e) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "Unable to clean up id table [" + tableName + "]", sql);
        }
    }
}

