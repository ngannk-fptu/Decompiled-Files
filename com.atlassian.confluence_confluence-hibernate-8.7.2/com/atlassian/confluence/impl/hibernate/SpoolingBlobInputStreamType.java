/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.spool.SmartSpool
 *  com.atlassian.core.spool.Spool
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.io.CountingInputStream
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.usertype.UserType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.support.lob.DefaultLobHandler
 *  org.springframework.jdbc.support.lob.LobCreator
 *  org.springframework.jdbc.support.lob.LobHandler
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.core.spool.SmartSpool;
import com.atlassian.core.spool.Spool;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.io.CountingInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpoolingBlobInputStreamType
implements UserType {
    private static final Logger log = LoggerFactory.getLogger(SpoolingBlobInputStreamType.class);
    private static final LobHandler DEFAULT_LOB_HANDLER = new DefaultLobHandler();
    private final Spool spool = SpoolingBlobInputStreamType.getDefaultSpool();

    private static Spool getDefaultSpool() {
        SmartSpool defaultSpool = new SmartSpool();
        defaultSpool.setThresholdBytes(131072);
        return defaultSpool;
    }

    private static LobHandler getLobHandler() {
        return ContainerManager.isContainerSetup() ? (LobHandler)ContainerManager.getComponent((String)"lobHandler") : DEFAULT_LOB_HANDLER;
    }

    public int[] sqlTypes() {
        return new int[]{2004};
    }

    public Class returnedClass() {
        return InputStream.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return x == null ? y == null : x == y || x.equals(y);
    }

    public int hashCode(Object x) throws HibernateException {
        return x != null ? x.hashCode() : 0;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        try {
            return this.nullSafeGetInternal(rs, rs.findColumn(names[0]), SpoolingBlobInputStreamType.getLobHandler());
        }
        catch (IOException var5) {
            throw new HibernateException("I/O errors during LOB access", (Throwable)var5);
        }
    }

    private Object nullSafeGetInternal(ResultSet rs, int column, LobHandler lobHandler) throws IOException, SQLException {
        try (InputStream is = SpoolingBlobInputStreamType.getLobHandler().getBlobAsBinaryStream(rs, column);){
            log.debug("Spooling data for blob get");
            InputStream inputStream = is == null ? null : this.spool.spool(is);
            return inputStream;
        }
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        LobCreator lobCreator = SpoolingBlobInputStreamType.getLobHandler().getLobCreator();
        try {
            this.nullSafeSetInternal(st, index, value, lobCreator);
        }
        catch (IOException var6) {
            throw new HibernateException("I/O errors during LOB access", (Throwable)var6);
        }
    }

    private void nullSafeSetInternal(PreparedStatement ps, int index, Object value, LobCreator lobCreator) throws IOException, SQLException {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("SpoolingBlobInputStreamType requires active transaction synchronization when preparing statement: " + ps.toString());
        }
        log.debug("Registering Spring transaction synchronization for LobCreator");
        TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new LobCreatorSynchronization(lobCreator));
        if (value == null) {
            lobCreator.setBlobAsBinaryStream(ps, index, null, 0);
            return;
        }
        InputStream is = (InputStream)value;
        CountingInputStream countingStream = new CountingInputStream(is);
        if (log.isDebugEnabled()) {
            log.debug("Spooling data for blob set");
        }
        InputStream spooledStream = this.spool.spool((InputStream)countingStream);
        if (log.isDebugEnabled()) {
            log.debug("Spooled " + countingStream.getCount() + " bytes");
        }
        lobCreator.setBlobAsBinaryStream(ps, index, spooledStream, (int)countingStream.getCount());
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
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

    private static class LobCreatorSynchronization
    implements TransactionSynchronization {
        private final LobCreator lobCreator;

        public LobCreatorSynchronization(LobCreator lobCreator) {
            this.lobCreator = lobCreator;
        }

        public void beforeCompletion() {
            this.lobCreator.close();
        }
    }
}

