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

public class MySQLMaxValueIncrementer
extends AbstractColumnMaxValueIncrementer {
    private static final String VALUE_SQL = "select last_insert_id()";
    private long nextId = 0L;
    private long maxId = 0L;
    private boolean useNewConnection = true;

    public MySQLMaxValueIncrementer() {
    }

    public MySQLMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName) {
        super(dataSource, incrementerName, columnName);
    }

    public void setUseNewConnection(boolean useNewConnection) {
        this.useNewConnection = useNewConnection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected synchronized long getNextKey() throws DataAccessException {
        block28: {
            block27: {
                if (this.maxId != this.nextId) break block27;
                Connection con = null;
                Statement stmt = null;
                boolean mustRestoreAutoCommit = false;
                try {
                    if (this.useNewConnection) {
                        con = this.getDataSource().getConnection();
                        if (con.getAutoCommit()) {
                            mustRestoreAutoCommit = true;
                            con.setAutoCommit(false);
                        }
                    } else {
                        con = DataSourceUtils.getConnection(this.getDataSource());
                    }
                    stmt = con.createStatement();
                    if (!this.useNewConnection) {
                        DataSourceUtils.applyTransactionTimeout(stmt, this.getDataSource());
                    }
                    String columnName = this.getColumnName();
                    try {
                        stmt.executeUpdate("update " + this.getIncrementerName() + " set " + columnName + " = last_insert_id(" + columnName + " + " + this.getCacheSize() + ") limit 1");
                    }
                    catch (SQLException ex) {
                        throw new DataAccessResourceFailureException("Could not increment " + columnName + " for " + this.getIncrementerName() + " sequence table", (Throwable)ex);
                    }
                    ResultSet rs = stmt.executeQuery(VALUE_SQL);
                    try {
                        if (!rs.next()) {
                            throw new DataAccessResourceFailureException("last_insert_id() failed after executing an update");
                        }
                        this.maxId = rs.getLong(1);
                    }
                    finally {
                        JdbcUtils.closeResultSet(rs);
                    }
                    this.nextId = this.maxId - (long)this.getCacheSize() + 1L;
                }
                catch (SQLException ex) {
                    try {
                        throw new DataAccessResourceFailureException("Could not obtain last_insert_id()", (Throwable)ex);
                    }
                    catch (Throwable throwable) {
                        JdbcUtils.closeStatement(stmt);
                        if (con != null) {
                            if (this.useNewConnection) {
                                try {
                                    con.commit();
                                    if (mustRestoreAutoCommit) {
                                        con.setAutoCommit(true);
                                    }
                                }
                                catch (SQLException ignore) {
                                    throw new DataAccessResourceFailureException("Unable to commit new sequence value changes for " + this.getIncrementerName());
                                }
                                JdbcUtils.closeConnection(con);
                            } else {
                                DataSourceUtils.releaseConnection(con, this.getDataSource());
                            }
                        }
                        throw throwable;
                    }
                }
                JdbcUtils.closeStatement(stmt);
                if (con != null) {
                    if (this.useNewConnection) {
                        try {
                            con.commit();
                            if (mustRestoreAutoCommit) {
                                con.setAutoCommit(true);
                            }
                        }
                        catch (SQLException ignore) {
                            throw new DataAccessResourceFailureException("Unable to commit new sequence value changes for " + this.getIncrementerName());
                        }
                        JdbcUtils.closeConnection(con);
                    } else {
                        DataSourceUtils.releaseConnection(con, this.getDataSource());
                    }
                }
                break block28;
            }
            ++this.nextId;
        }
        return this.nextId;
    }
}

