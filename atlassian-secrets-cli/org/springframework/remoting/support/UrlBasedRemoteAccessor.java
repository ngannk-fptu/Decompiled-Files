/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;

public abstract class UrlBasedRemoteAccessor
extends RemoteAccessor
implements InitializingBean {
    private String serviceUrl;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.getServiceUrl() == null) {
            throw new IllegalArgumentException("Property 'serviceUrl' is required");
        }
    }
}

