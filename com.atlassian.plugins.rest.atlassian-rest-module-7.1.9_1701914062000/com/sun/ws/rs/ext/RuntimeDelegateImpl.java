/*
 * Decompiled with CFR 0.152.
 */
package com.sun.ws.rs.ext;

import com.sun.jersey.core.spi.factory.AbstractRuntimeDelegate;
import javax.ws.rs.core.Application;

public class RuntimeDelegateImpl
extends AbstractRuntimeDelegate {
    @Override
    public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}

