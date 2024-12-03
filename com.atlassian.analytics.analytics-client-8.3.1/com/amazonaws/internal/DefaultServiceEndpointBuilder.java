/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.internal;

import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.internal.ServiceEndpointBuilder;
import com.amazonaws.regions.Region;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@NotThreadSafe
public class DefaultServiceEndpointBuilder
extends ServiceEndpointBuilder {
    private static final Log log = LogFactory.getLog(DefaultServiceEndpointBuilder.class);
    private final String serviceName;
    private final String protocol;
    private Region region;

    public DefaultServiceEndpointBuilder(String serviceName, String protocol) {
        this.serviceName = serviceName;
        this.protocol = protocol;
    }

    @Override
    public DefaultServiceEndpointBuilder withRegion(Region region) {
        if (region == null) {
            throw new IllegalArgumentException("Region cannot be null");
        }
        this.region = region;
        return this;
    }

    @Override
    public URI getServiceEndpoint() {
        String serviceEndpoint = this.region.getServiceEndpoint(this.serviceName);
        if (serviceEndpoint == null) {
            serviceEndpoint = String.format("%s.%s.%s", this.serviceName, this.region.getName(), this.region.getDomain());
            log.info((Object)("{" + this.serviceName + ", " + this.region.getName() + "} was not found in region metadata, trying to construct an endpoint using the standard pattern for this region: '" + serviceEndpoint + "'."));
        }
        return this.toURI(this.stripProtocol(serviceEndpoint));
    }

    private String stripProtocol(String endpoint) {
        int protocolIndex = endpoint.indexOf("://");
        return protocolIndex >= 0 ? endpoint.substring(protocolIndex + "://".length()) : endpoint;
    }

    private URI toURI(String endpoint) throws IllegalArgumentException {
        try {
            return new URI(String.format("%s://%s", this.protocol, endpoint));
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Region getRegion() {
        return this.region;
    }
}

