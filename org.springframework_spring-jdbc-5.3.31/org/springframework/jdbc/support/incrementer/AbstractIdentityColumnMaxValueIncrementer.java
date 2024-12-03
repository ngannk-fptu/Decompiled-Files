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
import org.springframework.jdbc.support.incrementer.AbstractColumnMaxValueIncrementer;

public abstract class AbstractIdentityColumnMaxValueIncrementer
extends AbstractColumnMaxValueIncrementer {
    private boolean deleteSpecificValues = false;
    private long[] valueCache;
    private int nextValueIndex = -1;

    public AbstractIdentityColumnMaxValueIncrementer() {
    }

    public AbstractIdentityColumnMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName) {
        super(dataSource, incrementerName, columnName);
    }

    public void setDeleteSpecificValues(boolean deleteSpecificValues) {
        this.deleteSpecificValues = deleteSpecificValues;
    }

    public boolean isDeleteSpecificValues() {
        return this.deleteSpecificValues;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected synchronized long getNextKey() throws DataAccessException {
        if (this.nextValueIndex < 0 || this.nextValueIndex >= this.getCacheSize()) {
            Connection con = DataSourceUtils.getConnection(this.getDataSource());
            Statement stmt = null;
            try {
                stmt = con.createStatement();
                DataSourceUtils.applyTransactionTimeout(stmt, this.getDataSource());
                this.valueCache = new long[this.getCacheSize()];
                this.nextValueIndex = 0;
                for (int i = 0; i < this.getCacheSize(); ++i) {
                    stmt.executeUpdate(this.getIncrementStatement());
                    ResultSet rs = stmt.executeQuery(this.getIdentityStatement());
                    try {
                        if (!rs.next()) {
                            throw new DataAccessResourceFailureException("Identity statement failed after inserting");
                        }
                        this.valueCache[i] = rs.getLong(1);
                        continue;
                    }
                    finally {
                        JdbcUtils.closeResultSet(rs);
                    }
                }
                stmt.executeUpdate(this.getDeleteStatement(this.valueCache));
            }
            catch (SQLException ex) {
                throw new DataAccessResourceFailureException("Could not increment identity", (Throwable)ex);
            }
            finally {
                JdbcUtils.closeStatement(stmt);
                DataSourceUtils.releaseConnection(con, this.getDataSource());
            }
        }
        return this.valueCache[this.nextValueIndex++];
    }

    protected abstract String getIncrementStatement();

    protected abstract String getIdentityStatement();

    protected String getDeleteStatement(long[] values) {
        StringBuilder sb = new StringBuilder(64);
        sb.append("delete from ").append(this.getIncrementerName()).append(" where ").append(this.getColumnName());
        if (this.isDeleteSpecificValues()) {
            sb.append(" in (").append(values[0] - 1L);
            for (int i = 0; i < values.length - 1; ++i) {
                sb.append(", ").append(values[i]);
            }
            sb.append(')');
        } else {
            long maxValue = values[values.length - 1];
            sb.append(" < ").append(maxValue);
        }
        return sb.toString();
    }
}

