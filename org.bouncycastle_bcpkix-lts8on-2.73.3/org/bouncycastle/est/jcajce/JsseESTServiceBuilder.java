/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est.jcajce;

import java.net.Socket;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.est.ESTClientProvider;
import org.bouncycastle.est.ESTService;
import org.bouncycastle.est.ESTServiceBuilder;
import org.bouncycastle.est.jcajce.ChannelBindingProvider;
import org.bouncycastle.est.jcajce.DefaultESTHttpClientProvider;
import org.bouncycastle.est.jcajce.JcaJceUtils;
import org.bouncycastle.est.jcajce.JsseDefaultHostnameAuthorizer;
import org.bouncycastle.est.jcajce.JsseHostnameAuthorizer;
import org.bouncycastle.est.jcajce.SSLSocketFactoryCreator;
import org.bouncycastle.est.jcajce.SSLSocketFactoryCreatorBuilder;

public class JsseESTServiceBuilder
extends ESTServiceBuilder {
    protected SSLSocketFactoryCreator socketFactoryCreator;
    protected JsseHostnameAuthorizer hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
    protected int timeoutMillis = 0;
    protected ChannelBindingProvider bindingProvider;
    protected Set<String> supportedSuites = new HashSet<String>();
    protected Long absoluteLimit;
    protected SSLSocketFactoryCreatorBuilder sslSocketFactoryCreatorBuilder;
    protected boolean filterCipherSuites = true;

    public JsseESTServiceBuilder(String hostName, int portNo, SSLSocketFactoryCreator socketFactoryCreator) {
        super(hostName + ":" + portNo);
        if (socketFactoryCreator == null) {
            throw new NullPointerException("No socket factory creator.");
        }
        this.socketFactoryCreator = socketFactoryCreator;
    }

    public JsseESTServiceBuilder(String server, SSLSocketFactoryCreator socketFactoryCreator) {
        super(server);
        if (socketFactoryCreator == null) {
            throw new NullPointerException("No socket factory creator.");
        }
        this.socketFactoryCreator = socketFactoryCreator;
    }

    public JsseESTServiceBuilder(String server) {
        super(server);
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(JcaJceUtils.getTrustAllTrustManager());
    }

    public JsseESTServiceBuilder(String hostName, int portNo, X509TrustManager trustManager) {
        super(hostName + ":" + portNo);
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(trustManager);
    }

    public JsseESTServiceBuilder(String server, X509TrustManager trustManager) {
        super(server);
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(trustManager);
    }

    public JsseESTServiceBuilder(String hostName, int portNo, X509TrustManager[] trustManagers) {
        this(hostName + ":" + portNo, trustManagers);
    }

    public JsseESTServiceBuilder(String server, X509TrustManager[] trustManagers) {
        super(server);
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(trustManagers);
    }

    public JsseESTServiceBuilder withHostNameAuthorizer(JsseHostnameAuthorizer hostNameAuthorizer) {
        this.hostNameAuthorizer = hostNameAuthorizer;
        return this;
    }

    @Override
    public JsseESTServiceBuilder withClientProvider(ESTClientProvider clientProvider) {
        this.clientProvider = clientProvider;
        return this;
    }

    public JsseESTServiceBuilder withTimeout(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }

    public JsseESTServiceBuilder withReadLimit(long absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
        return this;
    }

    public JsseESTServiceBuilder withChannelBindingProvider(ChannelBindingProvider channelBindingProvider) {
        this.bindingProvider = channelBindingProvider;
        return this;
    }

    public JsseESTServiceBuilder addCipherSuites(String name) {
        this.supportedSuites.add(name);
        return this;
    }

    public JsseESTServiceBuilder addCipherSuites(String[] names) {
        this.supportedSuites.addAll(Arrays.asList(names));
        return this;
    }

    public JsseESTServiceBuilder withTLSVersion(String tlsVersion) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withTLSVersion(tlsVersion);
        return this;
    }

    public JsseESTServiceBuilder withSecureRandom(SecureRandom secureRandom) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withSecureRandom(secureRandom);
        return this;
    }

    public JsseESTServiceBuilder withProvider(String tlsProviderName) throws NoSuchProviderException {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withProvider(tlsProviderName);
        return this;
    }

    public JsseESTServiceBuilder withProvider(Provider tlsProvider) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withProvider(tlsProvider);
        return this;
    }

    public JsseESTServiceBuilder withKeyManager(KeyManager keyManager) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withKeyManager(keyManager);
        return this;
    }

    public JsseESTServiceBuilder withKeyManagers(KeyManager[] keyManagers) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withKeyManagers(keyManagers);
        return this;
    }

    public JsseESTServiceBuilder withFilterCipherSuites(boolean filter) {
        this.filterCipherSuites = filter;
        return this;
    }

    @Override
    public ESTService build() {
        if (this.bindingProvider == null) {
            this.bindingProvider = new ChannelBindingProvider(){

                @Override
                public boolean canAccessChannelBinding(Socket sock) {
                    return false;
                }

                @Override
                public byte[] getChannelBinding(Socket sock, String binding) {
                    return null;
                }
            };
        }
        if (this.socketFactoryCreator == null) {
            this.socketFactoryCreator = this.sslSocketFactoryCreatorBuilder.build();
        }
        if (this.clientProvider == null) {
            this.clientProvider = new DefaultESTHttpClientProvider(this.hostNameAuthorizer, this.socketFactoryCreator, this.timeoutMillis, this.bindingProvider, this.supportedSuites, this.absoluteLimit, this.filterCipherSuites);
        }
        return super.build();
    }
}

