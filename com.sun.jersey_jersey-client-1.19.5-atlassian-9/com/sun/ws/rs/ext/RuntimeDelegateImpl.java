/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.spi.factory.AbstractRuntimeDelegate
 *  javax.ws.rs.core.Application
 */
package com.sun.ws.rs.ext;

import com.sun.jersey.core.spi.factory.AbstractRuntimeDelegate;
import javax.ws.rs.core.Application;

public class RuntimeDelegateImpl
extends AbstractRuntimeDelegate {
    public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}

