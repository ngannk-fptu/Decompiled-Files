/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.transport.http.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Module;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebModule;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.server.ServerAdapterList;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public final class ServerAdapter
extends HttpAdapter
implements BoundEndpoint {
    final String name;
    private static final Logger LOGGER = Logger.getLogger(ServerAdapter.class.getName());

    protected ServerAdapter(String name, String urlPattern, WSEndpoint endpoint, ServerAdapterList owner) {
        super(endpoint, owner, urlPattern);
        this.name = name;
        Module module = endpoint.getContainer().getSPI(Module.class);
        if (module == null) {
            LOGGER.log(Level.WARNING, "Container {0} doesn''t support {1}", new Object[]{endpoint.getContainer(), Module.class});
        } else {
            module.getBoundEndpoints().add(this);
        }
    }

    public String getName() {
        return this.name;
    }

    @Override
    @NotNull
    public URI getAddress() {
        WebModule webModule = this.endpoint.getContainer().getSPI(WebModule.class);
        if (webModule == null) {
            throw new WebServiceException("Container " + this.endpoint.getContainer() + " doesn't support " + WebModule.class);
        }
        return this.getAddress(webModule.getContextPath());
    }

    @Override
    @NotNull
    public URI getAddress(String baseAddress) {
        String adrs = baseAddress + this.getValidPath();
        try {
            return new URI(adrs);
        }
        catch (URISyntaxException e) {
            throw new WebServiceException("Unable to compute address for " + this.endpoint, (Throwable)e);
        }
    }

    public void dispose() {
        this.endpoint.dispose();
    }

    public String getUrlPattern() {
        return this.urlPattern;
    }

    public String toString() {
        return super.toString() + "[name=" + this.name + ']';
    }
}

