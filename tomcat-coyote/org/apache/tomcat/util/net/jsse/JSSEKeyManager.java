/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net.jsse;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;

public final class JSSEKeyManager
extends X509ExtendedKeyManager {
    private X509KeyManager delegate;
    private String serverKeyAlias;

    public JSSEKeyManager(X509KeyManager mgr, String serverKeyAlias) {
        this.delegate = mgr;
        this.serverKeyAlias = serverKeyAlias;
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        if (this.serverKeyAlias != null) {
            return this.serverKeyAlias;
        }
        return this.delegate.chooseServerAlias(keyType, issuers, socket);
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        if (this.serverKeyAlias != null) {
            return this.serverKeyAlias;
        }
        return super.chooseEngineServerAlias(keyType, issuers, engine);
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return this.delegate.chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return this.delegate.getCertificateChain(alias);
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return this.delegate.getClientAliases(keyType, issuers);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return this.delegate.getServerAliases(keyType, issuers);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return this.delegate.getPrivateKey(alias);
    }

    @Override
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return this.delegate.chooseClientAlias(keyType, issuers, null);
    }
}

