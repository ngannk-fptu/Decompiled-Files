/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class SqlRowSetResultSetExtractor
implements ResultSetExtractor<SqlRowSet> {
    private static final RowSetFactory rowSetFactory;

    @Override
    public SqlRowSet extractData(ResultSet rs) throws SQLException {
        return this.createSqlRowSet(rs);
    }

    protected SqlRowSet createSqlRowSet(ResultSet rs) throws SQLException {
        CachedRowSet rowSet = this.newCachedRowSet();
        rowSet.populate(rs);
        return new ResultSetWrappingSqlRowSet(rowSet);
    }

    protected CachedRowSet newCachedRowSet() throws SQLException {
        return rowSetFactory.createCachedRowSet();
    }

    static {
        try {
            rowSetFactory = RowSetProvider.newFactory();
        }
        catch (SQLException ex) {
            throw new IllegalStateException("Cannot create RowSetFactory through RowSetProvider", ex);
        }
    }
}

