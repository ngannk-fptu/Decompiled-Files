/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.auth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicAuthCache
implements AuthCache {
    private static final Logger LOG = LoggerFactory.getLogger(BasicAuthCache.class);
    private final Map<Key, byte[]> map = new ConcurrentHashMap<Key, byte[]>();
    private final SchemePortResolver schemePortResolver;

    public BasicAuthCache(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
    }

    public BasicAuthCache() {
        this(null);
    }

    private Key key(String scheme, NamedEndpoint authority, String pathPrefix) {
        return new Key(scheme, authority.getHostName(), this.schemePortResolver.resolve(scheme, authority), pathPrefix);
    }

    @Override
    public void put(HttpHost host, AuthScheme authScheme) {
        this.put(host, null, authScheme);
    }

    @Override
    public AuthScheme get(HttpHost host) {
        return this.get(host, null);
    }

    @Override
    public void remove(HttpHost host) {
        this.remove(host, null);
    }

    @Override
    public void put(HttpHost host, String pathPrefix, AuthScheme authScheme) {
        block19: {
            Args.notNull(host, "HTTP host");
            if (authScheme == null) {
                return;
            }
            if (authScheme instanceof Serializable) {
                try {
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    try (ObjectOutputStream out = new ObjectOutputStream(buf);){
                        out.writeObject(authScheme);
                    }
                    this.map.put(this.key(host.getSchemeName(), host, pathPrefix), buf.toByteArray());
                }
                catch (IOException ex) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Unexpected I/O error while serializing auth scheme", (Throwable)ex);
                    }
                    break block19;
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Auth scheme {} is not serializable", authScheme.getClass());
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthScheme get(HttpHost host, String pathPrefix) {
        Args.notNull(host, "HTTP host");
        byte[] bytes = this.map.get(this.key(host.getSchemeName(), host, pathPrefix));
        if (bytes == null) return null;
        try {
            ByteArrayInputStream buf = new ByteArrayInputStream(bytes);
            try (ObjectInputStream in = new ObjectInputStream(buf);){
                AuthScheme authScheme = (AuthScheme)in.readObject();
                return authScheme;
            }
        }
        catch (IOException ex) {
            if (!LOG.isWarnEnabled()) return null;
            LOG.warn("Unexpected I/O error while de-serializing auth scheme", (Throwable)ex);
            return null;
        }
        catch (ClassNotFoundException ex) {
            if (!LOG.isWarnEnabled()) return null;
            LOG.warn("Unexpected error while de-serializing auth scheme", (Throwable)ex);
        }
        return null;
    }

    @Override
    public void remove(HttpHost host, String pathPrefix) {
        Args.notNull(host, "HTTP host");
        this.map.remove(this.key(host.getSchemeName(), host, pathPrefix));
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public String toString() {
        return this.map.toString();
    }

    static class Key {
        final String scheme;
        final String host;
        final int port;
        final String pathPrefix;

        Key(String scheme, String host, int port, String pathPrefix) {
            Args.notBlank(scheme, "Scheme");
            Args.notBlank(host, "Scheme");
            this.scheme = scheme.toLowerCase(Locale.ROOT);
            this.host = host.toLowerCase(Locale.ROOT);
            this.port = port;
            this.pathPrefix = pathPrefix;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Key) {
                Key that = (Key)obj;
                return this.scheme.equals(that.scheme) && this.host.equals(that.host) && this.port == that.port && Objects.equals(this.pathPrefix, that.pathPrefix);
            }
            return false;
        }

        public int hashCode() {
            int hash = 17;
            hash = LangUtils.hashCode(hash, this.scheme);
            hash = LangUtils.hashCode(hash, this.host);
            hash = LangUtils.hashCode(hash, this.port);
            hash = LangUtils.hashCode(hash, this.pathPrefix);
            return hash;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(this.scheme).append("://").append(this.host);
            if (this.port >= 0) {
                buf.append(":").append(this.port);
            }
            if (this.pathPrefix != null) {
                if (!this.pathPrefix.startsWith("/")) {
                    buf.append("/");
                }
                buf.append(this.pathPrefix);
            }
            return buf.toString();
        }
    }
}

