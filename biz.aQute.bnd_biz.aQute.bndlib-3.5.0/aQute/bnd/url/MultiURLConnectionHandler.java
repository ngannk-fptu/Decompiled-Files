/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.url;

import aQute.bnd.service.Registry;
import aQute.bnd.service.url.URLConnectionHandler;
import java.net.URL;
import java.net.URLConnection;

public class MultiURLConnectionHandler
implements URLConnectionHandler {
    private Registry registry;

    public MultiURLConnectionHandler(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void handle(URLConnection connection) throws Exception {
        for (URLConnectionHandler h : this.registry.getPlugins(URLConnectionHandler.class)) {
            h.handle(connection);
        }
    }

    @Override
    public boolean matches(URL url) {
        return true;
    }
}

