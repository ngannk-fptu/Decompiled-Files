/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.core.support;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.util.Assert;

public abstract class AbstractLobCreatingPreparedStatementCallback
implements PreparedStatementCallback<Integer> {
    private final LobHandler lobHandler;

    public AbstractLobCreatingPreparedStatementCallback(LobHandler lobHandler) {
        Assert.notNull((Object)lobHandler, (String)"LobHandler must not be null");
        this.lobHandler = lobHandler;
    }

    @Override
    public final Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
        try (LobCreator lobCreator = this.lobHandler.getLobCreator();){
            this.setValues(ps, lobCreator);
            Integer n = ps.executeUpdate();
            return n;
        }
    }

    protected abstract void setValues(PreparedStatement var1, LobCreator var2) throws SQLException, DataAccessException;
}

