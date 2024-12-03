/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.EmptyResultDataAccessException
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.support;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.LobRetrievalFailureException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.Nullable;

public abstract class AbstractLobStreamingResultSetExtractor<T>
implements ResultSetExtractor<T> {
    @Override
    @Nullable
    public final T extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (!rs.next()) {
            this.handleNoRowFound();
        } else {
            try {
                this.streamData(rs);
                if (rs.next()) {
                    this.handleMultipleRowsFound();
                }
            }
            catch (IOException ex) {
                throw new LobRetrievalFailureException("Could not stream LOB content", ex);
            }
        }
        return null;
    }

    protected void handleNoRowFound() throws DataAccessException {
        throw new EmptyResultDataAccessException("LobStreamingResultSetExtractor did not find row in database", 1);
    }

    protected void handleMultipleRowsFound() throws DataAccessException {
        throw new IncorrectResultSizeDataAccessException("LobStreamingResultSetExtractor found multiple rows in database", 1);
    }

    protected abstract void streamData(ResultSet var1) throws SQLException, IOException, DataAccessException;
}

