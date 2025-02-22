/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory
 *  ch.qos.logback.core.net.ssl.SSLComponent
 *  ch.qos.logback.core.net.ssl.SSLConfiguration
 *  ch.qos.logback.core.net.ssl.SSLParametersConfiguration
 *  ch.qos.logback.core.spi.ContextAware
 */
package ch.qos.logback.classic.net;

import ch.qos.logback.classic.net.SocketReceiver;
import ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import ch.qos.logback.core.spi.ContextAware;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

public class SSLSocketReceiver
extends SocketReceiver
implements SSLComponent {
    private SSLConfiguration ssl;
    private SocketFactory socketFactory;

    @Override
    protected SocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    @Override
    protected boolean shouldStart() {
        try {
            SSLContext sslContext = this.getSsl().createContext((ContextAware)this);
            SSLParametersConfiguration parameters = this.getSsl().getParameters();
            parameters.setContext(this.getContext());
            this.socketFactory = new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
            return super.shouldStart();
        }
        catch (Exception ex) {
            this.addError(ex.getMessage(), ex);
            return false;
        }
    }

    public SSLConfiguration getSsl() {
        if (this.ssl == null) {
            this.ssl = new SSLConfiguration();
        }
        return this.ssl;
    }

    public void setSsl(SSLConfiguration ssl) {
        this.ssl = ssl;
    }
}

