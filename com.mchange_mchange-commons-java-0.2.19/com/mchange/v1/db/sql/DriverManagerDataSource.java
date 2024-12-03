/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.io.UnsupportedVersionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

public class DriverManagerDataSource
implements DataSource,
Serializable,
Referenceable {
    static final String REF_FACTORY_NAME = DmdsObjectFactory.class.getName();
    static final String REF_JDBC_URL = "jdbcUrl";
    static final String REF_DFLT_USER = "dfltUser";
    static final String REF_DFLT_PWD = "dfltPassword";
    String jdbcUrl;
    String dfltUser;
    String dfltPassword;
    static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    public DriverManagerDataSource(String string, String string2, String string3) {
        this.jdbcUrl = string;
        this.dfltUser = string2;
        this.dfltPassword = string3;
    }

    public DriverManagerDataSource(String string) {
        this(string, null, null);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.jdbcUrl, this.createProps(null, null));
    }

    @Override
    public Connection getConnection(String string, String string2) throws SQLException {
        return DriverManager.getConnection(this.jdbcUrl, this.createProps(string, string2));
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        DriverManager.setLogWriter(printWriter);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int n) throws SQLException {
        DriverManager.setLoginTimeout(n);
    }

    @Override
    public boolean isWrapperFor(Class<?> clazz) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) throws SQLException {
        throw new SQLException(this.getClass().getName() + " is not a wrapper for an object implementing any interface.");
    }

    @Override
    public Reference getReference() throws NamingException {
        Reference reference = new Reference(this.getClass().getName(), REF_FACTORY_NAME, null);
        reference.add(new StringRefAddr(REF_JDBC_URL, this.jdbcUrl));
        reference.add(new StringRefAddr(REF_DFLT_USER, this.dfltUser));
        reference.add(new StringRefAddr(REF_DFLT_PWD, this.dfltPassword));
        return reference;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("javax.sql.DataSource.getParentLogger() is not currently supported by " + this.getClass().getName());
    }

    private Properties createProps(String string, String string2) {
        Properties properties = new Properties();
        if (string != null) {
            properties.put("user", string);
            properties.put("password", string2);
        } else if (this.dfltUser != null) {
            properties.put("user", this.dfltUser);
            properties.put("password", this.dfltPassword);
        }
        return properties;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeShort(1);
        objectOutputStream.writeUTF(this.jdbcUrl);
        objectOutputStream.writeUTF(this.dfltUser);
        objectOutputStream.writeUTF(this.dfltPassword);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        short s = objectInputStream.readShort();
        switch (s) {
            case 1: {
                this.jdbcUrl = objectInputStream.readUTF();
                this.dfltUser = objectInputStream.readUTF();
                this.dfltPassword = objectInputStream.readUTF();
                break;
            }
            default: {
                throw new UnsupportedVersionException(this, s);
            }
        }
    }

    public static class DmdsObjectFactory
    implements ObjectFactory {
        public Object getObjectInstance(Object object, Name name, Context context, Hashtable hashtable) throws Exception {
            Reference reference;
            String string = DriverManagerDataSource.class.getName();
            if (object instanceof Reference && (reference = (Reference)object).getClassName().equals(string)) {
                return new DriverManagerDataSource((String)reference.get(DriverManagerDataSource.REF_JDBC_URL).getContent(), (String)reference.get(DriverManagerDataSource.REF_DFLT_USER).getContent(), (String)reference.get(DriverManagerDataSource.REF_DFLT_PWD).getContent());
            }
            return null;
        }
    }
}

