/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.Executor;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.ObjectNameWrapper;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnectionMXBean;
import org.apache.tomcat.dbcp.dbcp2.PoolingConnection;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.pool2.ObjectPool;

public class PoolableConnection
extends DelegatingConnection<Connection>
implements PoolableConnectionMXBean {
    private static MBeanServer MBEAN_SERVER;
    private final ObjectPool<PoolableConnection> pool;
    private final ObjectNameWrapper jmxObjectName;
    private PreparedStatement validationPreparedStatement;
    private String lastValidationSql;
    private boolean fatalSqlExceptionThrown;
    private final Collection<String> disconnectionSqlCodes;
    private final boolean fastFailValidation;

    public PoolableConnection(Connection conn, ObjectPool<PoolableConnection> pool, ObjectName jmxName) {
        this(conn, pool, jmxName, null, true);
    }

    public PoolableConnection(Connection conn, ObjectPool<PoolableConnection> pool, ObjectName jmxObjectName, Collection<String> disconnectSqlCodes, boolean fastFailValidation) {
        super(conn);
        this.pool = pool;
        this.jmxObjectName = ObjectNameWrapper.wrap(jmxObjectName);
        this.disconnectionSqlCodes = disconnectSqlCodes;
        this.fastFailValidation = fastFailValidation;
        if (jmxObjectName != null) {
            try {
                MBEAN_SERVER.registerMBean(this, jmxObjectName);
            }
            catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException jMException) {
                // empty catch block
            }
        }
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        if (this.jmxObjectName != null) {
            this.jmxObjectName.unregisterMBean();
        }
        super.abort(executor);
    }

    @Override
    public synchronized void close() throws SQLException {
        boolean isUnderlyingConnectionClosed;
        if (this.isClosedInternal()) {
            return;
        }
        try {
            isUnderlyingConnectionClosed = this.getDelegateInternal().isClosed();
        }
        catch (SQLException e) {
            try {
                this.pool.invalidateObject(this);
            }
            catch (IllegalStateException ise) {
                this.passivate();
                this.getInnermostDelegate().close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw new SQLException("Cannot close connection (isClosed check failed)", e);
        }
        if (isUnderlyingConnectionClosed) {
            try {
                this.pool.invalidateObject(this);
            }
            catch (IllegalStateException e) {
                this.passivate();
                this.getInnermostDelegate().close();
            }
            catch (Exception e) {
                throw new SQLException("Cannot close connection (invalidating pooled object failed)", e);
            }
        } else {
            try {
                this.pool.returnObject(this);
            }
            catch (IllegalStateException e) {
                this.passivate();
                this.getInnermostDelegate().close();
            }
            catch (RuntimeException | SQLException e) {
                throw e;
            }
            catch (Exception e) {
                throw new SQLException("Cannot close connection (return to pool failed)", e);
            }
        }
    }

    public Collection<String> getDisconnectionSqlCodes() {
        return this.disconnectionSqlCodes;
    }

    @Override
    public String getToString() {
        return this.toString();
    }

    @Override
    protected void handleException(SQLException e) throws SQLException {
        this.fatalSqlExceptionThrown |= this.isFatalException(e);
        super.handleException(e);
    }

    @Override
    public boolean isClosed() throws SQLException {
        if (this.isClosedInternal()) {
            return true;
        }
        if (this.getDelegateInternal().isClosed()) {
            this.close();
            return true;
        }
        return false;
    }

    boolean isDisconnectionSqlException(SQLException e) {
        boolean fatalException = false;
        String sqlState = e.getSQLState();
        if (sqlState != null) {
            fatalException = this.disconnectionSqlCodes == null ? sqlState.startsWith("08") || Utils.getDisconnectionSqlCodes().contains(sqlState) : this.disconnectionSqlCodes.contains(sqlState);
        }
        return fatalException;
    }

    boolean isFatalException(SQLException e) {
        boolean fatalException = this.isDisconnectionSqlException(e);
        if (!fatalException) {
            SQLException parentException = e;
            SQLException nextException = e.getNextException();
            while (nextException != null && nextException != parentException && !fatalException) {
                fatalException = this.isDisconnectionSqlException(nextException);
                parentException = nextException;
                nextException = parentException.getNextException();
            }
        }
        return fatalException;
    }

    public boolean isFastFailValidation() {
        return this.fastFailValidation;
    }

    @Override
    protected void passivate() throws SQLException {
        super.passivate();
        this.setClosedInternal(true);
        if (this.getDelegateInternal() instanceof PoolingConnection) {
            ((PoolingConnection)this.getDelegateInternal()).connectionReturnedToPool();
        }
    }

    @Override
    public void reallyClose() throws SQLException {
        if (this.jmxObjectName != null) {
            this.jmxObjectName.unregisterMBean();
        }
        if (this.validationPreparedStatement != null) {
            Utils.closeQuietly((AutoCloseable)this.validationPreparedStatement);
        }
        super.closeInternal();
    }

    @Deprecated
    public void validate(String sql, int timeoutSeconds) throws SQLException {
        this.validate(sql, Duration.ofSeconds(timeoutSeconds));
    }

    public void validate(String sql, Duration timeoutDuration) throws SQLException {
        if (this.fastFailValidation && this.fatalSqlExceptionThrown) {
            throw new SQLException(Utils.getMessage("poolableConnection.validate.fastFail"));
        }
        if (sql == null || sql.isEmpty()) {
            if (timeoutDuration.isNegative()) {
                timeoutDuration = Duration.ZERO;
            }
            if (!this.isValid(timeoutDuration)) {
                throw new SQLException("isValid() returned false");
            }
            return;
        }
        if (!sql.equals(this.lastValidationSql)) {
            this.lastValidationSql = sql;
            this.validationPreparedStatement = this.getInnermostDelegateInternal().prepareStatement(sql);
        }
        if (timeoutDuration.compareTo(Duration.ZERO) > 0) {
            this.validationPreparedStatement.setQueryTimeout((int)timeoutDuration.getSeconds());
        }
        try (ResultSet rs = this.validationPreparedStatement.executeQuery();){
            if (!rs.next()) {
                throw new SQLException("validationQuery didn't return a row");
            }
        }
    }

    static {
        try {
            MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
        }
        catch (Exception | NoClassDefFoundError throwable) {
            // empty catch block
        }
    }
}

