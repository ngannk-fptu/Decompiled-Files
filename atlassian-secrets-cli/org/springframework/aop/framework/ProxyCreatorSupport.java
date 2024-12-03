/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import java.util.LinkedList;
import java.util.List;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AdvisedSupportListener;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.util.Assert;

public class ProxyCreatorSupport
extends AdvisedSupport {
    private AopProxyFactory aopProxyFactory;
    private final List<AdvisedSupportListener> listeners = new LinkedList<AdvisedSupportListener>();
    private boolean active = false;

    public ProxyCreatorSupport() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    public ProxyCreatorSupport(AopProxyFactory aopProxyFactory) {
        Assert.notNull((Object)aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }

    public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
        Assert.notNull((Object)aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }

    public AopProxyFactory getAopProxyFactory() {
        return this.aopProxyFactory;
    }

    public void addListener(AdvisedSupportListener listener) {
        Assert.notNull((Object)listener, "AdvisedSupportListener must not be null");
        this.listeners.add(listener);
    }

    public void removeListener(AdvisedSupportListener listener) {
        Assert.notNull((Object)listener, "AdvisedSupportListener must not be null");
        this.listeners.remove(listener);
    }

    protected final synchronized AopProxy createAopProxy() {
        if (!this.active) {
            this.activate();
        }
        return this.getAopProxyFactory().createAopProxy(this);
    }

    private void activate() {
        this.active = true;
        for (AdvisedSupportListener listener : this.listeners) {
            listener.activated(this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void adviceChanged() {
        super.adviceChanged();
        ProxyCreatorSupport proxyCreatorSupport = this;
        synchronized (proxyCreatorSupport) {
            if (this.active) {
                for (AdvisedSupportListener listener : this.listeners) {
                    listener.adviceChanged(this);
                }
            }
        }
    }

    protected final synchronized boolean isActive() {
        return this.active;
    }
}

