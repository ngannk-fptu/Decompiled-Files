/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.transport.http.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.Module;
import java.util.ArrayList;
import java.util.List;

class ServerContainer
extends Container {
    private final Module module = new Module(){
        private final List<BoundEndpoint> endpoints = new ArrayList<BoundEndpoint>();

        @Override
        @NotNull
        public List<BoundEndpoint> getBoundEndpoints() {
            return this.endpoints;
        }
    };

    ServerContainer() {
    }

    public <T> T getSPI(Class<T> spiType) {
        T t = super.getSPI(spiType);
        if (t != null) {
            return t;
        }
        if (spiType == Module.class) {
            return spiType.cast(this.module);
        }
        return null;
    }
}

