/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.client;

import com.sun.xml.ws.api.ResourceLoader;
import com.sun.xml.ws.api.server.Container;
import java.net.MalformedURLException;
import java.net.URL;

final class ClientContainer
extends Container {
    private final ResourceLoader loader = new ResourceLoader(){

        @Override
        public URL getResource(String resource) throws MalformedURLException {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = this.getClass().getClassLoader();
            }
            return cl.getResource("META-INF/" + resource);
        }
    };

    ClientContainer() {
    }

    public <T> T getSPI(Class<T> spiType) {
        T t = super.getSPI(spiType);
        if (t != null) {
            return t;
        }
        if (spiType == ResourceLoader.class) {
            return spiType.cast(this.loader);
        }
        return null;
    }
}

