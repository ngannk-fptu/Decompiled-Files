/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory
 *  ch.qos.logback.core.net.ssl.SSLComponent
 *  ch.qos.logback.core.net.ssl.SSLConfiguration
 *  ch.qos.logback.core.net.ssl.SSLParametersConfiguration
 *  ch.qos.logback.core.spi.ContextAware
 */
package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.net.server.ServerSocketReceiver;
import ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import ch.qos.logback.core.spi.ContextAware;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

public class SSLServerSocketReceiver
extends ServerSocketReceiver
implements SSLComponent {
    private SSLConfiguration ssl;
    private ServerSocketFactory socketFactory;

    @Override
    protected ServerSocketFactory getServerSocketFactory() throws Exception {
        if (this.socketFactory == null) {
            SSLContext sslContext = this.getSsl().createContext((ContextAware)this);
            SSLParametersConfiguration parameters = this.getSsl().getParameters();
            parameters.setContext(this.getContext());
            this.socketFactory = new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
        }
        return this.socketFactory;
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

