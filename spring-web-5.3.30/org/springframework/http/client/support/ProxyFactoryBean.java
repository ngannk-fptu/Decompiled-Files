/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http.client.support;

import java.net.InetSocketAddress;
import java.net.Proxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ProxyFactoryBean
implements FactoryBean<Proxy>,
InitializingBean {
    private Proxy.Type type = Proxy.Type.HTTP;
    @Nullable
    private String hostname;
    private int port = -1;
    @Nullable
    private Proxy proxy;

    public void setType(Proxy.Type type) {
        this.type = type;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void afterPropertiesSet() throws IllegalArgumentException {
        Assert.notNull((Object)((Object)this.type), (String)"Property 'type' is required");
        Assert.notNull((Object)this.hostname, (String)"Property 'hostname' is required");
        if (this.port < 0 || this.port > 65535) {
            throw new IllegalArgumentException("Property 'port' value out of range: " + this.port);
        }
        InetSocketAddress socketAddress = new InetSocketAddress(this.hostname, this.port);
        this.proxy = new Proxy(this.type, socketAddress);
    }

    @Nullable
    public Proxy getObject() {
        return this.proxy;
    }

    public Class<?> getObjectType() {
        return Proxy.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

