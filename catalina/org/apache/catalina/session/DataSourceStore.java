/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 */
package org.apache.catalina.session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Session;
import org.apache.catalina.session.JDBCStore;
import org.apache.catalina.session.StandardSession;
import org.apache.juli.logging.Log;

public class DataSourceStore
extends JDBCStore {
    @Override
    public String[] expiredKeys() throws IOException {
        return this.keys(true);
    }

    @Override
    public String[] keys() throws IOException {
        return this.keys(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String[] keys(boolean expiredOnly) throws IOException {
        String[] keys = null;
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            Connection _conn = this.getConnection();
            if (_conn == null) {
                return new String[0];
            }
            try {
                String keysSql = "SELECT " + this.sessionIdCol + " FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
                if (expiredOnly) {
                    keysSql = keysSql + " AND (" + this.sessionLastAccessedCol + " + " + this.sessionMaxInactiveCol + " * 1000 < ?)";
                }
                try (PreparedStatement preparedKeysSql = _conn.prepareStatement(keysSql);){
                    preparedKeysSql.setString(1, this.getName());
                    if (expiredOnly) {
                        preparedKeysSql.setLong(2, System.currentTimeMillis());
                    }
                    try (ResultSet rst = preparedKeysSql.executeQuery();){
                        ArrayList<String> tmpkeys = new ArrayList<String>();
                        if (rst != null) {
                            while (rst.next()) {
                                tmpkeys.add(rst.getString(1));
                            }
                        }
                        keys = tmpkeys.toArray(new String[0]);
                        numberOfTries = 0;
                        continue;
                    }
                }
            }
            catch (SQLException e) {
                this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                keys = new String[]{};
                continue;
            }
            finally {
                this.release(_conn);
            }
        }
        return keys;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSize() throws IOException {
        int size = 0;
        String sizeSql = "SELECT COUNT(" + this.sessionIdCol + ") FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            Connection _conn = this.getConnection();
            if (_conn == null) {
                return size;
            }
            try (PreparedStatement preparedSizeSql = _conn.prepareStatement(sizeSql);){
                preparedSizeSql.setString(1, this.getName());
                try (ResultSet rst = preparedSizeSql.executeQuery();){
                    if (rst.next()) {
                        size = rst.getInt(1);
                    }
                    numberOfTries = 0;
                    continue;
                }
            }
            catch (SQLException e) {
                this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                continue;
            }
            finally {
                this.release(_conn);
            }
        }
        return size;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Session load(String id) throws ClassNotFoundException, IOException {
        StandardSession _session = null;
        Context context = this.getManager().getContext();
        Log contextLog = context.getLogger();
        String loadSql = "SELECT " + this.sessionIdCol + ", " + this.sessionDataCol + " FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ? AND " + this.sessionAppCol + " = ?";
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            Connection _conn = this.getConnection();
            if (_conn == null) {
                return null;
            }
            ClassLoader oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
            try (PreparedStatement preparedLoadSql = _conn.prepareStatement(loadSql);){
                preparedLoadSql.setString(1, id);
                preparedLoadSql.setString(2, this.getName());
                try (ResultSet rst = preparedLoadSql.executeQuery();){
                    if (rst.next()) {
                        try (ObjectInputStream ois = this.getObjectInputStream(rst.getBinaryStream(2));){
                            if (contextLog.isDebugEnabled()) {
                                contextLog.debug((Object)sm.getString(this.getStoreName() + ".loading", new Object[]{id, this.sessionTable}));
                            }
                            _session = (StandardSession)this.manager.createEmptySession();
                            _session.readObjectData(ois);
                            _session.setManager(this.manager);
                        }
                    } else if (context.getLogger().isDebugEnabled()) {
                        contextLog.debug((Object)(this.getStoreName() + ": No persisted data object found"));
                    }
                    numberOfTries = 0;
                    continue;
                }
            }
            catch (SQLException e) {
                contextLog.error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                continue;
            }
            finally {
                context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                this.release(_conn);
            }
        }
        return _session;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(String id) throws IOException {
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            Connection _conn = this.getConnection();
            if (_conn == null) {
                return;
            }
            try {
                this.remove(id, _conn);
                numberOfTries = 0;
                continue;
            }
            catch (SQLException e) {
                this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                continue;
            }
            finally {
                this.release(_conn);
            }
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)sm.getString(this.getStoreName() + ".removing", new Object[]{id, this.sessionTable}));
        }
    }

    private void remove(String id, Connection _conn) throws SQLException {
        String removeSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ?  AND " + this.sessionAppCol + " = ?";
        try (PreparedStatement preparedRemoveSql = _conn.prepareStatement(removeSql);){
            preparedRemoveSql.setString(1, id);
            preparedRemoveSql.setString(2, this.getName());
            preparedRemoveSql.execute();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() throws IOException {
        String clearSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            Connection _conn = this.getConnection();
            if (_conn == null) {
                return;
            }
            try (PreparedStatement preparedClearSql = _conn.prepareStatement(clearSql);){
                preparedClearSql.setString(1, this.getName());
                preparedClearSql.execute();
                numberOfTries = 0;
                continue;
            }
            catch (SQLException e) {
                this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                continue;
            }
            finally {
                this.release(_conn);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void save(Session session) throws IOException {
        ByteArrayOutputStream bos = null;
        String saveSql = "INSERT INTO " + this.sessionTable + " (" + this.sessionIdCol + ", " + this.sessionAppCol + ", " + this.sessionDataCol + ", " + this.sessionValidCol + ", " + this.sessionMaxInactiveCol + ", " + this.sessionLastAccessedCol + ") VALUES (?, ?, ?, ?, ?, ?)";
        Session session2 = session;
        synchronized (session2) {
            for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
                Connection _conn = this.getConnection();
                if (_conn == null) {
                    return;
                }
                try {
                    this.remove(session.getIdInternal(), _conn);
                    bos = new ByteArrayOutputStream();
                    try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));){
                        ((StandardSession)session).writeObjectData(oos);
                    }
                    byte[] obs = bos.toByteArray();
                    int size = obs.length;
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(obs, 0, size);
                         BufferedInputStream in = new BufferedInputStream(bis, size);
                         PreparedStatement preparedSaveSql = _conn.prepareStatement(saveSql);){
                        preparedSaveSql.setString(1, session.getIdInternal());
                        preparedSaveSql.setString(2, this.getName());
                        preparedSaveSql.setBinaryStream(3, (InputStream)in, size);
                        preparedSaveSql.setString(4, session.isValid() ? "1" : "0");
                        preparedSaveSql.setInt(5, session.getMaxInactiveInterval());
                        preparedSaveSql.setLong(6, session.getLastAccessedTime());
                        preparedSaveSql.execute();
                        numberOfTries = 0;
                        continue;
                    }
                }
                catch (SQLException e) {
                    this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".SQLException", new Object[]{e}));
                    continue;
                }
                catch (IOException iOException) {
                    continue;
                }
                finally {
                    this.release(_conn);
                }
            }
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)sm.getString(this.getStoreName() + ".saving", new Object[]{session.getIdInternal(), this.sessionTable}));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Connection open() throws SQLException {
        if (this.dataSourceName != null && this.dataSource == null) {
            Context context = this.getManager().getContext();
            ClassLoader oldThreadContextCL = null;
            if (this.getLocalDataSource()) {
                oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
            }
            try {
                InitialContext initCtx = new InitialContext();
                javax.naming.Context envCtx = (javax.naming.Context)initCtx.lookup("java:comp/env");
                this.dataSource = (DataSource)envCtx.lookup(this.dataSourceName);
            }
            catch (NamingException e) {
                context.getLogger().error((Object)sm.getString(this.getStoreName() + ".wrongDataSource", new Object[]{this.dataSourceName}), (Throwable)e);
            }
            finally {
                if (this.getLocalDataSource()) {
                    context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                }
            }
        }
        if (this.dataSource != null) {
            return this.dataSource.getConnection();
        }
        throw new IllegalStateException(sm.getString(this.getStoreName() + ".missingDataSource"));
    }

    @Override
    protected void close(Connection dbConnection) {
        if (dbConnection == null) {
            return;
        }
        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.commit();
            }
        }
        catch (SQLException e) {
            this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".commitSQLException"), (Throwable)e);
        }
        try {
            dbConnection.close();
        }
        catch (SQLException e) {
            this.manager.getContext().getLogger().error((Object)sm.getString(this.getStoreName() + ".close", new Object[]{e.toString()}));
        }
    }
}

