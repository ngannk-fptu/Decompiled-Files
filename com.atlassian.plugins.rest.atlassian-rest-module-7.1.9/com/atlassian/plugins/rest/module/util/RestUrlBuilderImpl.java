/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package com.atlassian.plugins.rest.module.util;

import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.plugins.rest.module.util.GeneratedURIResponse;
import com.atlassian.plugins.rest.module.util.ProxyUtils;
import com.atlassian.plugins.rest.module.util.ResourcePathUrlInvokable;
import com.sun.jersey.spi.service.ServiceConfigurationError;
import com.sun.jersey.spi.service.ServiceFinder;
import java.net.URI;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;
import org.springframework.util.Assert;

public class RestUrlBuilderImpl
implements RestUrlBuilder {
    public RestUrlBuilderImpl() {
        this.loadServiceFinderClass();
        RuntimeDelegate.getInstance();
    }

    private void loadServiceFinderClass() {
        try {
            ServiceFinder.find("NOSUCHSERVICE");
        }
        catch (ServiceConfigurationError serviceConfigurationError) {
            // empty catch block
        }
    }

    @Override
    public URI getURI(Response resource) {
        if (resource instanceof GeneratedURIResponse) {
            return ((GeneratedURIResponse)resource).getURI();
        }
        throw new IllegalArgumentException("Supplied response is not a generated one");
    }

    @Override
    public <T> T getUrlFor(URI baseUri, Class<T> resourceClass) {
        Assert.notNull(resourceClass, (String)"resourceClass cannot be null");
        Assert.notNull((Object)baseUri, (String)"baseUri cannot be null");
        return ProxyUtils.create(resourceClass, new ResourcePathUrlInvokable(resourceClass, baseUri));
    }
}

