/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.wadl;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.server.impl.wadl.WadlApplicationContextImpl;
import com.sun.jersey.server.impl.wadl.WadlMethodFactory;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.ext.Providers;

public final class WadlFactory {
    private static final Logger LOGGER = Logger.getLogger(WadlFactory.class.getName());
    private final boolean isWadlEnabled;
    private final ResourceConfig _resourceConfig;
    private final Providers _providers;
    private WadlApplicationContext wadlApplicationContext;

    public WadlFactory(ResourceConfig resourceConfig, Providers providers) {
        this.isWadlEnabled = WadlFactory.isWadlEnabled(resourceConfig);
        this._resourceConfig = resourceConfig;
        this._providers = providers;
    }

    public boolean isSupported() {
        return this.isWadlEnabled;
    }

    public WadlApplicationContext createWadlApplicationContext(Set<AbstractResource> rootResources) {
        if (!this.isSupported()) {
            return null;
        }
        return new WadlApplicationContextImpl(rootResources, this._resourceConfig, this._providers);
    }

    public void init(InjectableProviderFactory ipf, Set<AbstractResource> rootResources) {
        if (!this.isSupported()) {
            return;
        }
        this.wadlApplicationContext = new WadlApplicationContextImpl(rootResources, this._resourceConfig, this._providers);
    }

    public ResourceMethod createWadlOptionsMethod(Map<String, List<ResourceMethod>> methods, AbstractResource resource, PathPattern p) {
        if (!this.isSupported()) {
            return null;
        }
        if (p == null) {
            return new WadlMethodFactory.WadlOptionsMethod(methods, resource, null, this.wadlApplicationContext);
        }
        String path = p.getTemplate().getTemplate().substring(1);
        return new WadlMethodFactory.WadlOptionsMethod(methods, resource, path, this.wadlApplicationContext);
    }

    private static boolean isWadlEnabled(ResourceConfig resourceConfig) {
        return !resourceConfig.getFeature("com.sun.jersey.config.feature.DisableWADL");
    }

    WadlApplicationContext getWadlApplicationContext() {
        return this.wadlApplicationContext;
    }
}

