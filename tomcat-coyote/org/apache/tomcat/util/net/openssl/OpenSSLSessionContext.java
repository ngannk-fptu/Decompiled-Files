/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.jni.SSLContext
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net.openssl;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import org.apache.tomcat.jni.SSLContext;
import org.apache.tomcat.util.net.openssl.OpenSSLContext;
import org.apache.tomcat.util.net.openssl.OpenSSLSessionStats;
import org.apache.tomcat.util.res.StringManager;

public class OpenSSLSessionContext
implements SSLSessionContext {
    private static final StringManager sm = StringManager.getManager(OpenSSLSessionContext.class);
    private static final Enumeration<byte[]> EMPTY = new EmptyEnumeration();
    private final OpenSSLSessionStats stats;
    private final OpenSSLContext context;
    private final long contextID;

    OpenSSLSessionContext(OpenSSLContext context) {
        this.context = context;
        this.contextID = context.getSSLContextID();
        this.stats = new OpenSSLSessionStats(this.contextID);
    }

    @Override
    public SSLSession getSession(byte[] bytes) {
        return null;
    }

    @Override
    public Enumeration<byte[]> getIds() {
        return EMPTY;
    }

    public void setTicketKeys(byte[] keys) {
        if (keys == null) {
            throw new IllegalArgumentException(sm.getString("sessionContext.nullTicketKeys"));
        }
        SSLContext.setSessionTicketKeys((long)this.contextID, (byte[])keys);
    }

    public void setSessionCacheEnabled(boolean enabled) {
        long mode = enabled ? 2L : 0L;
        SSLContext.setSessionCacheMode((long)this.contextID, (long)mode);
    }

    public boolean isSessionCacheEnabled() {
        return SSLContext.getSessionCacheMode((long)this.contextID) == 2L;
    }

    public OpenSSLSessionStats stats() {
        return this.stats;
    }

    @Override
    public void setSessionTimeout(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException();
        }
        SSLContext.setSessionCacheTimeout((long)this.contextID, (long)seconds);
    }

    @Override
    public int getSessionTimeout() {
        return (int)SSLContext.getSessionCacheTimeout((long)this.contextID);
    }

    @Override
    public void setSessionCacheSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        SSLContext.setSessionCacheSize((long)this.contextID, (long)size);
    }

    @Override
    public int getSessionCacheSize() {
        return (int)SSLContext.getSessionCacheSize((long)this.contextID);
    }

    public boolean setSessionIdContext(byte[] sidCtx) {
        return SSLContext.setSessionIdContext((long)this.contextID, (byte[])sidCtx);
    }

    private static final class EmptyEnumeration
    implements Enumeration<byte[]> {
        private EmptyEnumeration() {
        }

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public byte[] nextElement() {
            throw new NoSuchElementException();
        }
    }
}

