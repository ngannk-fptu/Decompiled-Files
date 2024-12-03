/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.config.management;

import com.sun.xml.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.ws.api.server.WSEndpoint;

public interface ManagedEndpointFactory {
    public <T> WSEndpoint<T> createEndpoint(WSEndpoint<T> var1, EndpointCreationAttributes var2);
}

