/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 */
package org.springframework.jdbc.support.incrementer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;

public abstract class AbstractSequenceMaxValueIncrementer
extends AbstractDataFieldMaxValueIncrementer {
    public AbstractSequenceMaxValueIncrementer() {
    }

    public AbstractSequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
        super(dataSource, incrementerName);
    }

    @Override
    protected long getNextKey() throws DataAccessException {
        ResultSet rs;
        Statement stmt;
        Connection con;
        block5: {
            con = DataSourceUtils.getConnection(this.getDataSource());
            stmt = null;
            rs = null;
            stmt = con.createStatement();
            DataSourceUtils.applyTransactionTimeout(stmt, this.getDataSource());
            rs = stmt.executeQuery(this.getSequenceQuery());
            if (!rs.next()) break block5;
            long l = rs.getLong(1);
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(stmt);
            DataSourceUtils.releaseConnection(con, this.getDataSource());
            return l;
        }
        try {
            try {
                throw new DataAccessResourceFailureException("Sequence query did not return a result");
            }
            catch (SQLException ex) {
                throw new DataAccessResourceFailureException("Could not obtain sequence value", (Throwable)ex);
            }
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(stmt);
            DataSourceUtils.releaseConnection(con, this.getDataSource());
            throw throwable;
        }
    }

    protected abstract String getSequenceQuery();
}

