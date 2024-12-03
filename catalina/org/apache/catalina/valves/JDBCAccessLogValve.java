/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.catalina.valves;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import javax.servlet.ServletException;
import org.apache.catalina.AccessLog;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.tomcat.util.ExceptionUtils;

public final class JDBCAccessLogValve
extends ValveBase
implements AccessLog {
    boolean useLongContentLength = false;
    String connectionName = null;
    String connectionPassword = null;
    Driver driver = null;
    private String driverName = null;
    private String connectionURL = null;
    private String tableName = "access";
    private String remoteHostField = "remoteHost";
    private String userField = "userName";
    private String timestampField = "timestamp";
    private String virtualHostField = "virtualHost";
    private String methodField = "method";
    private String queryField = "query";
    private String statusField = "status";
    private String bytesField = "bytes";
    private String refererField = "referer";
    private String userAgentField = "userAgent";
    private String pattern = "common";
    private boolean resolveHosts = false;
    private Connection conn = null;
    private PreparedStatement ps = null;
    private long currentTimeMillis = new Date().getTime();
    boolean requestAttributesEnabled = true;

    public JDBCAccessLogValve() {
        super(true);
    }

    @Override
    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    @Override
    public boolean getRequestAttributesEnabled() {
        return this.requestAttributesEnabled;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getConnectionPassword() {
        return this.connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setRemoteHostField(String remoteHostField) {
        this.remoteHostField = remoteHostField;
    }

    public void setUserField(String userField) {
        this.userField = userField;
    }

    public void setTimestampField(String timestampField) {
        this.timestampField = timestampField;
    }

    public void setVirtualHostField(String virtualHostField) {
        this.virtualHostField = virtualHostField;
    }

    public void setMethodField(String methodField) {
        this.methodField = methodField;
    }

    public void setQueryField(String queryField) {
        this.queryField = queryField;
    }

    public void setStatusField(String statusField) {
        this.statusField = statusField;
    }

    public void setBytesField(String bytesField) {
        this.bytesField = bytesField;
    }

    public void setRefererField(String refererField) {
        this.refererField = refererField;
    }

    public void setUserAgentField(String userAgentField) {
        this.userAgentField = userAgentField;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setResolveHosts(String resolveHosts) {
        this.resolveHosts = Boolean.parseBoolean(resolveHosts);
    }

    public boolean getUseLongContentLength() {
        return this.useLongContentLength;
    }

    public void setUseLongContentLength(boolean useLongContentLength) {
        this.useLongContentLength = useLongContentLength;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        this.getNext().invoke(request, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void log(Request request, Response response, long time) {
        Object addr;
        Object host;
        if (!this.getState().isAvailable()) {
            return;
        }
        String EMPTY = "";
        String remoteHost = this.resolveHosts ? (this.requestAttributesEnabled ? ((host = request.getAttribute("org.apache.catalina.AccessLog.RemoteHost")) == null ? request.getRemoteHost() : (String)host) : request.getRemoteHost()) : (this.requestAttributesEnabled ? ((addr = request.getAttribute("org.apache.catalina.AccessLog.RemoteAddr")) == null ? request.getRemoteAddr() : (String)addr) : request.getRemoteAddr());
        String user = request.getRemoteUser();
        String query = request.getRequestURI();
        long bytes = response.getBytesWritten(true);
        if (bytes < 0L) {
            bytes = 0L;
        }
        int status = response.getStatus();
        String virtualHost = "";
        String method = "";
        String referer = "";
        String userAgent = "";
        String logPattern = this.pattern;
        if (logPattern.equals("combined")) {
            virtualHost = request.getServerName();
            method = request.getMethod();
            referer = request.getHeader("referer");
            userAgent = request.getHeader("user-agent");
        }
        JDBCAccessLogValve jDBCAccessLogValve = this;
        synchronized (jDBCAccessLogValve) {
            for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
                try {
                    this.open();
                    this.ps.setString(1, remoteHost);
                    this.ps.setString(2, user);
                    this.ps.setTimestamp(3, new Timestamp(this.getCurrentTimeMillis()));
                    this.ps.setString(4, query);
                    this.ps.setInt(5, status);
                    if (this.useLongContentLength) {
                        this.ps.setLong(6, bytes);
                    } else {
                        if (bytes > Integer.MAX_VALUE) {
                            bytes = -1L;
                        }
                        this.ps.setInt(6, (int)bytes);
                    }
                    if (logPattern.equals("combined")) {
                        this.ps.setString(7, virtualHost);
                        this.ps.setString(8, method);
                        this.ps.setString(9, referer);
                        this.ps.setString(10, userAgent);
                    }
                    this.ps.executeUpdate();
                    return;
                }
                catch (SQLException e) {
                    this.container.getLogger().error((Object)sm.getString("jdbcAccessLogValve.exception"), (Throwable)e);
                    if (this.conn == null) continue;
                    this.close();
                    continue;
                }
            }
        }
    }

    protected void open() throws SQLException {
        if (this.conn != null) {
            return;
        }
        if (this.driver == null) {
            try {
                Class<?> clazz = Class.forName(this.driverName);
                this.driver = (Driver)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable e) {
                ExceptionUtils.handleThrowable((Throwable)e);
                throw new SQLException(e.getMessage(), e);
            }
        }
        Properties props = new Properties();
        if (this.connectionName != null) {
            props.put("user", this.connectionName);
        }
        if (this.connectionPassword != null) {
            props.put("password", this.connectionPassword);
        }
        this.conn = this.driver.connect(this.connectionURL, props);
        this.conn.setAutoCommit(true);
        String logPattern = this.pattern;
        if (logPattern.equals("common")) {
            this.ps = this.conn.prepareStatement("INSERT INTO " + this.tableName + " (" + this.remoteHostField + ", " + this.userField + ", " + this.timestampField + ", " + this.queryField + ", " + this.statusField + ", " + this.bytesField + ") VALUES(?, ?, ?, ?, ?, ?)");
        } else if (logPattern.equals("combined")) {
            this.ps = this.conn.prepareStatement("INSERT INTO " + this.tableName + " (" + this.remoteHostField + ", " + this.userField + ", " + this.timestampField + ", " + this.queryField + ", " + this.statusField + ", " + this.bytesField + ", " + this.virtualHostField + ", " + this.methodField + ", " + this.refererField + ", " + this.userAgentField + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }
    }

    protected void close() {
        if (this.conn == null) {
            return;
        }
        try {
            this.ps.close();
        }
        catch (Throwable f) {
            ExceptionUtils.handleThrowable((Throwable)f);
        }
        this.ps = null;
        try {
            this.conn.close();
        }
        catch (SQLException e) {
            this.container.getLogger().error((Object)sm.getString("jdbcAccessLogValve.close"), (Throwable)e);
        }
        finally {
            this.conn = null;
        }
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        try {
            this.open();
        }
        catch (SQLException e) {
            throw new LifecycleException(e);
        }
        super.startInternal();
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.close();
    }

    public long getCurrentTimeMillis() {
        long systime = System.currentTimeMillis();
        if (systime - this.currentTimeMillis > 1000L) {
            this.currentTimeMillis = new Date(systime).getTime();
        }
        return this.currentTimeMillis;
    }
}

