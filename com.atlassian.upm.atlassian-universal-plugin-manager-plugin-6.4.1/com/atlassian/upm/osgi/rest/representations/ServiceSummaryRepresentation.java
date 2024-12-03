/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.osgi.rest.representations;

import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.impl.Wrapper;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ServiceSummaryRepresentation {
    @JsonProperty
    private final long id;
    @JsonProperty
    private final Map<String, URI> links;

    @JsonCreator
    public ServiceSummaryRepresentation(@JsonProperty(value="id") long id, @JsonProperty(value="links") Map<String, URI> links) {
        this.id = id;
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
    }

    public ServiceSummaryRepresentation(Service service, UpmUriBuilder uriBuilder) {
        this.id = service.getId();
        this.links = Collections.singletonMap("self", uriBuilder.buildOsgiServiceUri(service));
    }

    public long getId() {
        return this.id;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public static Wrapper<Service, ServiceSummaryRepresentation> wrapSummary(final UpmUriBuilder uriBuilder) {
        return new Wrapper<Service, ServiceSummaryRepresentation>("serviceSummaryRepresentation"){

            @Override
            public ServiceSummaryRepresentation wrap(Service service) {
                return new ServiceSummaryRepresentation(service, uriBuilder);
            }
        };
    }
}

