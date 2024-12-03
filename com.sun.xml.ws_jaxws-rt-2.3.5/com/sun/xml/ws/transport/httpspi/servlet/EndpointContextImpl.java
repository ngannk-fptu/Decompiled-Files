/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Endpoint
 *  javax.xml.ws.EndpointContext
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import java.util.HashSet;
import java.util.Set;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointContext;

public class EndpointContextImpl
extends EndpointContext {
    private final Set<Endpoint> set = new HashSet<Endpoint>();

    void add(Endpoint endpoint) {
        this.set.add(endpoint);
    }

    public Set<Endpoint> getEndpoints() {
        return this.set;
    }
}

