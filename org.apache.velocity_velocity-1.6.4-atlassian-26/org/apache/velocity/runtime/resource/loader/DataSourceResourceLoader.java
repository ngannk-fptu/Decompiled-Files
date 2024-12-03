/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.velocity.runtime.resource.loader;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExceptionUtils;

public class DataSourceResourceLoader
extends ResourceLoader {
    private String dataSourceName;
    private String tableName;
    private String keyColumn;
    private String templateColumn;
    private String timestampColumn;
    private InitialContext ctx;
    private DataSource dataSource;

    @Override
    public void init(ExtendedProperties configuration) {
        this.dataSourceName = org.apache.velocity.util.StringUtils.nullTrim(configuration.getString("resource.datasource"));
        this.tableName = org.apache.velocity.util.StringUtils.nullTrim(configuration.getString("resource.table"));
        this.keyColumn = org.apache.velocity.util.StringUtils.nullTrim(configuration.getString("resource.keycolumn"));
        this.templateColumn = org.apache.velocity.util.StringUtils.nullTrim(configuration.getString("resource.templatecolumn"));
        this.timestampColumn = org.apache.velocity.util.StringUtils.nullTrim(configuration.getString("resource.timestampcolumn"));
        if (this.dataSource != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("DataSourceResourceLoader: using dataSource instance with table \"" + this.tableName + "\"");
                this.log.debug("DataSourceResourceLoader: using columns \"" + this.keyColumn + "\", \"" + this.templateColumn + "\" and \"" + this.timestampColumn + "\"");
            }
            this.log.trace("DataSourceResourceLoader initialized.");
        } else if (this.dataSourceName != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("DataSourceResourceLoader: using \"" + this.dataSourceName + "\" datasource with table \"" + this.tableName + "\"");
                this.log.debug("DataSourceResourceLoader: using columns \"" + this.keyColumn + "\", \"" + this.templateColumn + "\" and \"" + this.timestampColumn + "\"");
            }
            this.log.trace("DataSourceResourceLoader initialized.");
        } else {
            String msg = "DataSourceResourceLoader not properly initialized. No DataSource was identified.";
            this.log.error(msg);
            throw new RuntimeException(msg);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return resource.getLastModified() != this.readLastModified(resource, "checking timestamp");
    }

    @Override
    public long getLastModified(Resource resource) {
        return this.readLastModified(resource, "getting timestamp");
    }

    @Override
    public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
        ResultSet rs;
        Connection conn;
        block8: {
            if (StringUtils.isEmpty((CharSequence)name)) {
                throw new ResourceNotFoundException("DataSourceResourceLoader: Template name was empty or null");
            }
            conn = null;
            rs = null;
            conn = this.openDbConnection();
            rs = this.readData(conn, this.templateColumn, name);
            if (!rs.next()) break block8;
            InputStream stream = rs.getBinaryStream(this.templateColumn);
            if (stream == null) {
                throw new ResourceNotFoundException("DataSourceResourceLoader: template column for '" + name + "' is null");
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
            this.closeResultSet(rs);
            this.closeDbConnection(conn);
            return bufferedInputStream;
        }
        try {
            try {
                throw new ResourceNotFoundException("DataSourceResourceLoader: could not find resource '" + name + "'");
            }
            catch (SQLException sqle) {
                String msg = "DataSourceResourceLoader: database problem while getting resource '" + name + "': ";
                this.log.error(msg, sqle);
                throw new ResourceNotFoundException(msg);
            }
            catch (NamingException ne) {
                String msg = "DataSourceResourceLoader: database problem while getting resource '" + name + "': ";
                this.log.error(msg, ne);
                throw new ResourceNotFoundException(msg);
            }
        }
        catch (Throwable throwable) {
            this.closeResultSet(rs);
            this.closeDbConnection(conn);
            throw throwable;
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private long readLastModified(Resource resource, String operation) {
        long timeStamp = 0L;
        String name = resource.getName();
        if (name == null || name.length() == 0) {
            String msg = "DataSourceResourceLoader: Template name was empty or null";
            this.log.error(msg);
            throw new NullPointerException(msg);
        }
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = this.openDbConnection();
            rs = this.readData(conn, this.timestampColumn, name);
            if (!rs.next()) {
                String msg = "DataSourceResourceLoader: could not find resource " + name + " while " + operation;
                this.log.error(msg);
                throw new ResourceNotFoundException(msg);
            }
            Timestamp ts = rs.getTimestamp(this.timestampColumn);
            timeStamp = ts != null ? ts.getTime() : 0L;
            this.closeResultSet(rs);
            this.closeDbConnection(conn);
            return timeStamp;
        }
        catch (SQLException sqle) {
            try {
                String msg = "DataSourceResourceLoader: database problem while " + operation + " of '" + name + "': ";
                this.log.error(msg, sqle);
                throw ExceptionUtils.createRuntimeException(msg, sqle);
                catch (NamingException ne) {
                    msg = "DataSourceResourceLoader: database problem while " + operation + " of '" + name + "': ";
                    this.log.error(msg, ne);
                    throw ExceptionUtils.createRuntimeException(msg, ne);
                }
            }
            catch (Throwable throwable) {
                this.closeResultSet(rs);
                this.closeDbConnection(conn);
                throw throwable;
            }
        }
    }

    private Connection openDbConnection() throws NamingException, SQLException {
        if (this.dataSource != null) {
            return this.dataSource.getConnection();
        }
        if (this.ctx == null) {
            this.ctx = new InitialContext();
        }
        this.dataSource = (DataSource)this.ctx.lookup(this.dataSourceName);
        return this.dataSource.getConnection();
    }

    private void closeDbConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (RuntimeException re) {
                throw re;
            }
            catch (Exception e) {
                String msg = "DataSourceResourceLoader: problem when closing connection";
                this.log.error(msg, e);
                throw new VelocityException(msg, e);
            }
        }
    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (RuntimeException re) {
                throw re;
            }
            catch (Exception e) {
                String msg = "DataSourceResourceLoader: problem when closing result set";
                this.log.error(msg, e);
                throw new VelocityException(msg, e);
            }
        }
    }

    private ResultSet readData(Connection conn, String columnNames, String templateName) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT " + columnNames + " FROM " + this.tableName + " WHERE " + this.keyColumn + " = ?");
        ps.setString(1, templateName);
        return ps.executeQuery();
    }
}

