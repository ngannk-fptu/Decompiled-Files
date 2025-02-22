/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est.jcajce;

import java.util.Set;
import javax.net.ssl.SSLSocketFactory;
import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTClientProvider;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.jcajce.ChannelBindingProvider;
import org.bouncycastle.est.jcajce.DefaultESTClient;
import org.bouncycastle.est.jcajce.DefaultESTClientSourceProvider;
import org.bouncycastle.est.jcajce.JsseHostnameAuthorizer;
import org.bouncycastle.est.jcajce.SSLSocketFactoryCreator;

class DefaultESTHttpClientProvider
implements ESTClientProvider {
    private final JsseHostnameAuthorizer hostNameAuthorizer;
    private final SSLSocketFactoryCreator socketFactoryCreator;
    private final int timeout;
    private final ChannelBindingProvider bindingProvider;
    private final Set<String> cipherSuites;
    private final Long absoluteLimit;
    private final boolean filterCipherSuites;

    public DefaultESTHttpClientProvider(JsseHostnameAuthorizer hostNameAuthorizer, SSLSocketFactoryCreator socketFactoryCreator, int timeout, ChannelBindingProvider bindingProvider, Set<String> cipherSuites, Long absoluteLimit, boolean filterCipherSuites) {
        this.hostNameAuthorizer = hostNameAuthorizer;
        this.socketFactoryCreator = socketFactoryCreator;
        this.timeout = timeout;
        this.bindingProvider = bindingProvider;
        this.cipherSuites = cipherSuites;
        this.absoluteLimit = absoluteLimit;
        this.filterCipherSuites = filterCipherSuites;
    }

    @Override
    public ESTClient makeClient() throws ESTException {
        try {
            SSLSocketFactory socketFactory = this.socketFactoryCreator.createFactory();
            return new DefaultESTClient(new DefaultESTClientSourceProvider(socketFactory, this.hostNameAuthorizer, this.timeout, this.bindingProvider, this.cipherSuites, this.absoluteLimit, this.filterCipherSuites));
        }
        catch (Exception e) {
            throw new ESTException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public boolean isTrusted() {
        return this.socketFactoryCreator.isTrusted();
    }
}

