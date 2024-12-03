/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509ExtendedKeyManager;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.ssl.X509;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SniX509ExtendedKeyManager
extends X509ExtendedKeyManager {
    private static final Logger LOG = LoggerFactory.getLogger(SniX509ExtendedKeyManager.class);
    private final X509ExtendedKeyManager _delegate;
    private final SslContextFactory.Server _sslContextFactory;
    private UnaryOperator<String> _aliasMapper = UnaryOperator.identity();

    public SniX509ExtendedKeyManager(X509ExtendedKeyManager keyManager, SslContextFactory.Server sslContextFactory) {
        this._delegate = keyManager;
        this._sslContextFactory = Objects.requireNonNull(sslContextFactory, "SslContextFactory.Server must be provided");
    }

    public UnaryOperator<String> getAliasMapper() {
        return this._aliasMapper;
    }

    public void setAliasMapper(UnaryOperator<String> aliasMapper) {
        this._aliasMapper = Objects.requireNonNull(aliasMapper);
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return this._delegate.chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return this._delegate.chooseEngineClientAlias(keyType, issuers, engine);
    }

    protected String chooseServerAlias(String keyType, Principal[] issuers, Collection<SNIMatcher> matchers, SSLSession session) {
        List<SNIServerName> serverNames;
        String[] mangledAliases = this._delegate.getServerAliases(keyType, issuers);
        if (mangledAliases == null || mangledAliases.length == 0) {
            return null;
        }
        LinkedHashMap aliasMap = new LinkedHashMap();
        Arrays.stream(mangledAliases).forEach(alias -> aliasMap.put((String)this.getAliasMapper().apply((String)alias), alias));
        String host = null;
        if (session instanceof ExtendedSSLSession && (serverNames = ((ExtendedSSLSession)session).getRequestedServerNames()) != null) {
            host = serverNames.stream().findAny().filter(SNIHostName.class::isInstance).map(SNIHostName.class::cast).map(SNIHostName::getAsciiName).orElse(null);
        }
        if (host == null) {
            String string = host = matchers == null ? null : (String)matchers.stream().filter(SslContextFactory.AliasSNIMatcher.class::isInstance).map(SslContextFactory.AliasSNIMatcher.class::cast).findFirst().map(SslContextFactory.AliasSNIMatcher::getHost).orElse(null);
        }
        if (session != null && host != null) {
            session.putValue("org.eclipse.jetty.util.ssl.sniHost", host);
        }
        try {
            String alias2;
            Collection certificates = aliasMap.keySet().stream().map(this._sslContextFactory::getX509).filter(Objects::nonNull).collect(Collectors.toList());
            SniSelector sniSelector = this._sslContextFactory.getSNISelector();
            if (sniSelector == null) {
                sniSelector = this._sslContextFactory;
            }
            if ((alias2 = sniSelector.sniSelect(keyType, issuers, session, host, certificates)) == null || alias2 == "delegate_no_sni_match") {
                return alias2;
            }
            X509 x509 = this._sslContextFactory.getX509(alias2);
            if (!aliasMap.containsKey(alias2) || x509 == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Invalid X509 match for SNI {}: {}", (Object)host, (Object)alias2);
                }
                return null;
            }
            String mangledAlias = (String)aliasMap.get(alias2);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Matched SNI {} with alias {}, certificate {} from aliases {}", new Object[]{host, mangledAlias, x509, aliasMap.keySet()});
            }
            return mangledAlias;
        }
        catch (Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failure matching X509 for SNI {}", host, (Object)x);
            }
            return null;
        }
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        boolean delegate;
        SSLSocket sslSocket = (SSLSocket)socket;
        String alias = socket == null ? this.chooseServerAlias(keyType, issuers, Collections.emptyList(), null) : this.chooseServerAlias(keyType, issuers, sslSocket.getSSLParameters().getSNIMatchers(), sslSocket.getHandshakeSession());
        boolean bl = delegate = alias == "delegate_no_sni_match";
        if (delegate) {
            alias = this._delegate.chooseServerAlias(keyType, issuers, socket);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Chose {} alias={} keyType={} on {}", new Object[]{delegate ? "delegate" : "explicit", String.valueOf(alias), keyType, socket});
        }
        return alias;
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        boolean delegate;
        String alias = engine == null ? this.chooseServerAlias(keyType, issuers, Collections.emptyList(), null) : this.chooseServerAlias(keyType, issuers, engine.getSSLParameters().getSNIMatchers(), engine.getHandshakeSession());
        boolean bl = delegate = alias == "delegate_no_sni_match";
        if (delegate) {
            alias = this._delegate.chooseEngineServerAlias(keyType, issuers, engine);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Chose {} alias={} keyType={} on {}", new Object[]{delegate ? "delegate" : "explicit", String.valueOf(alias), keyType, engine});
        }
        return alias;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return this._delegate.getCertificateChain(alias);
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return this._delegate.getClientAliases(keyType, issuers);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return this._delegate.getPrivateKey(alias);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return this._delegate.getServerAliases(keyType, issuers);
    }

    @FunctionalInterface
    public static interface SniSelector {
        public static final String DELEGATE = "delegate_no_sni_match";

        public String sniSelect(String var1, Principal[] var2, SSLSession var3, String var4, Collection<X509> var5) throws SSLHandshakeException;
    }
}

