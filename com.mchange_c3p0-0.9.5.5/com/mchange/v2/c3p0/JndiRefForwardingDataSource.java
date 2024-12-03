/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.impl.JndiRefDataSourceBase;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.sql.SqlUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.sql.DataSource;

final class JndiRefForwardingDataSource
extends JndiRefDataSourceBase
implements DataSource {
    static final MLogger logger = MLog.getLogger(JndiRefForwardingDataSource.class);
    transient DataSource cachedInner;
    private static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    public JndiRefForwardingDataSource() {
        this(true);
    }

    public JndiRefForwardingDataSource(boolean autoregister) {
        super(autoregister);
        this.setUpPropertyListeners();
    }

    private void setUpPropertyListeners() {
        VetoableChangeListener l = new VetoableChangeListener(){

            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                Object val = evt.getNewValue();
                if ("jndiName".equals(evt.getPropertyName()) && !(val instanceof Name) && !(val instanceof String)) {
                    throw new PropertyVetoException("jndiName must be a String or a javax.naming.Name", evt);
                }
            }
        };
        this.addVetoableChangeListener(l);
        PropertyChangeListener pcl = new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                JndiRefForwardingDataSource.this.cachedInner = null;
            }
        };
        this.addPropertyChangeListener(pcl);
    }

    private DataSource dereference() throws SQLException {
        Object jndiName = this.getJndiName();
        Hashtable jndiEnv = this.getJndiEnv();
        try {
            InitialContext ctx = jndiEnv != null ? new InitialContext(jndiEnv) : new InitialContext();
            if (jndiName instanceof String) {
                return (DataSource)ctx.lookup((String)jndiName);
            }
            if (jndiName instanceof Name) {
                return (DataSource)ctx.lookup((Name)jndiName);
            }
            throw new SQLException("Could not find ConnectionPoolDataSource with JNDI name: " + jndiName);
        }
        catch (NamingException e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "An Exception occurred while trying to look up a target DataSource via JNDI!", (Throwable)e);
            }
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    private synchronized DataSource inner() throws SQLException {
        if (this.cachedInner != null) {
            return this.cachedInner;
        }
        DataSource out = this.dereference();
        if (this.isCaching()) {
            this.cachedInner = out;
        }
        return out;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.inner().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.inner().getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.inner().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.inner().setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.inner().getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.inner().setLoginTimeout(seconds);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeShort(1);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        short version = ois.readShort();
        switch (version) {
            case 1: {
                this.setUpPropertyListeners();
                break;
            }
            default: {
                throw new IOException("Unsupported Serialized Version: " + version);
            }
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(this + " is not a Wrapper for " + iface.getName());
    }
}

