/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est.jcajce;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.est.jcajce.SSLSocketFactoryCreator;

class SSLSocketFactoryCreatorBuilder {
    protected String tlsVersion = "TLS";
    protected Provider tlsProvider;
    protected KeyManager[] keyManagers;
    protected X509TrustManager[] trustManagers;
    protected SecureRandom secureRandom;

    public SSLSocketFactoryCreatorBuilder(X509TrustManager trustManager) {
        if (trustManager == null) {
            throw new NullPointerException("Trust managers can not be null");
        }
        this.trustManagers = new X509TrustManager[]{trustManager};
    }

    public SSLSocketFactoryCreatorBuilder(X509TrustManager[] trustManagers) {
        if (trustManagers == null) {
            throw new NullPointerException("Trust managers can not be null");
        }
        this.trustManagers = trustManagers;
    }

    public SSLSocketFactoryCreatorBuilder withTLSVersion(String tlsVersion) {
        this.tlsVersion = tlsVersion;
        return this;
    }

    public SSLSocketFactoryCreatorBuilder withSecureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    public SSLSocketFactoryCreatorBuilder withProvider(String tlsProviderName) throws NoSuchProviderException {
        this.tlsProvider = Security.getProvider(tlsProviderName);
        if (this.tlsProvider == null) {
            throw new NoSuchProviderException("JSSE provider not found: " + tlsProviderName);
        }
        return this;
    }

    public SSLSocketFactoryCreatorBuilder withProvider(Provider tlsProvider) {
        this.tlsProvider = tlsProvider;
        return this;
    }

    public SSLSocketFactoryCreatorBuilder withKeyManager(KeyManager keyManager) {
        this.keyManagers = keyManager == null ? null : new KeyManager[]{keyManager};
        return this;
    }

    public SSLSocketFactoryCreatorBuilder withKeyManagers(KeyManager[] keyManagers) {
        this.keyManagers = keyManagers;
        return this;
    }

    public SSLSocketFactoryCreator build() {
        return new SSLSocketFactoryCreator(){

            @Override
            public boolean isTrusted() {
                for (int i = 0; i != SSLSocketFactoryCreatorBuilder.this.trustManagers.length; ++i) {
                    X509TrustManager tm = SSLSocketFactoryCreatorBuilder.this.trustManagers[i];
                    if (tm.getAcceptedIssuers().length <= 0) continue;
                    return true;
                }
                return false;
            }

            @Override
            public SSLSocketFactory createFactory() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
                SSLContext ctx = SSLSocketFactoryCreatorBuilder.this.tlsProvider != null ? SSLContext.getInstance(SSLSocketFactoryCreatorBuilder.this.tlsVersion, SSLSocketFactoryCreatorBuilder.this.tlsProvider) : SSLContext.getInstance(SSLSocketFactoryCreatorBuilder.this.tlsVersion);
                ctx.init(SSLSocketFactoryCreatorBuilder.this.keyManagers, SSLSocketFactoryCreatorBuilder.this.trustManagers, SSLSocketFactoryCreatorBuilder.this.secureRandom);
                return ctx.getSocketFactory();
            }
        };
    }
}

