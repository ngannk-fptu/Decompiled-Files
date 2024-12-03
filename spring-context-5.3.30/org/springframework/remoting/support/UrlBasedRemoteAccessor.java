/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
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

    public void afterPropertiesSet() {
        if (this.getServiceUrl() == null) {
            throw new IllegalArgumentException("Property 'serviceUrl' is required");
        }
    }
}

