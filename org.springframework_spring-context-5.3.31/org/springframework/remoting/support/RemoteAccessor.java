/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.remoting.support;

import org.springframework.remoting.support.RemotingSupport;
import org.springframework.util.Assert;

public abstract class RemoteAccessor
extends RemotingSupport {
    private Class<?> serviceInterface;

    public void setServiceInterface(Class<?> serviceInterface) {
        Assert.notNull(serviceInterface, (String)"'serviceInterface' must not be null");
        Assert.isTrue((boolean)serviceInterface.isInterface(), (String)"'serviceInterface' must be an interface");
        this.serviceInterface = serviceInterface;
    }

    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }
}

