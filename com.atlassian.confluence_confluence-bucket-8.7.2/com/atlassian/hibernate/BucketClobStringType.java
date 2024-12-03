/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.spring.container.ContainerManager
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.usertype.UserType
 *  org.springframework.jdbc.support.lob.DefaultLobHandler
 *  org.springframework.jdbc.support.lob.LobCreator
 *  org.springframework.jdbc.support.lob.LobHandler
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationAdapter
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.hibernate;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.spring.container.ContainerManager;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class BucketClobStringType
implements UserType,
Serializable {
    private boolean useSetClobAsString = false;

    private HibernateConfig getHibernateConfig() {
        return ContainerManager.isContainerSetup() ? (HibernateConfig)ContainerManager.getComponent((String)"hibernateConfig", HibernateConfig.class) : null;
    }

    public boolean isUsingOracle() {
        return this.getHibernateConfig().isOracle();
    }

    public boolean isUsingHSQL() {
        HibernateConfig hibernateConfig = this.getHibernateConfig();
        return hibernateConfig != null && hibernateConfig.isHSQL();
    }

    public boolean isUsingMySQL() {
        return this.getHibernateConfig().isMySql();
    }

    protected LobHandler getLobHandler() {
        return ContainerManager.isContainerSetup() ? (LobHandler)ContainerManager.getComponent((String)"lobHandler") : new DefaultLobHandler();
    }

    public int[] sqlTypes() {
        return new int[]{2005};
    }

    public Class returnedClass() {
        return String.class;
    }

    public boolean equals(Object x, Object y) {
        return Objects.equals(x, y);
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        int index = rs.findColumn(names[0]);
        if (this.isUsingOracle()) {
            String data = this.getLobHandler().getClobAsString(rs, index);
            data = data != null ? new String(data) : null;
        } else if (this.isUsingHSQL()) {
            return rs.getString(index);
        }
        return this.getLobHandler().getClobAsString(rs, index);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("ClobStringType requires active transaction synchronization when preparing statement: " + st.toString());
        }
        LobCreator lobCreator = this.getLobHandler().getLobCreator();
        String data = (String)value;
        if (data == null) {
            data = "";
        }
        if (this.isUsingMySQL()) {
            lobCreator.setClobAsString(st, index, data);
        } else if (this.isUsingHSQL()) {
            st.setString(index, data);
        } else if (this.isUseSetClobAsString()) {
            lobCreator.setClobAsString(st, index, data);
        } else {
            lobCreator.setClobAsCharacterStream(st, index, (Reader)new StringReader(data), data.length());
        }
        TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new LobCreatorSynchronization(lobCreator));
    }

    public Object deepCopy(Object value) {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    protected void setUseSetClobAsString(boolean b) {
        this.useSetClobAsString = b;
    }

    protected boolean isUseSetClobAsString() {
        return this.useSetClobAsString;
    }

    public int hashCode(Object x) throws HibernateException {
        return x != null ? x.hashCode() : 0;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return value != null ? (Serializable)this.deepCopy(value) : null;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached != null ? this.deepCopy(cached) : null;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    protected static class LobCreatorSynchronization
    extends TransactionSynchronizationAdapter {
        private final LobCreator lobCreator;

        public LobCreatorSynchronization(LobCreator lobCreator) {
            this.lobCreator = lobCreator;
        }

        public void beforeCompletion() {
            this.lobCreator.close();
        }
    }
}

