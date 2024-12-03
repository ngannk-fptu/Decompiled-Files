/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.osgi.rest.representations;

import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.impl.Wrapper;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class BundleSummaryRepresentation {
    @JsonProperty
    private final Bundle.State state;
    @JsonProperty
    private final long id;
    @JsonProperty
    @Nullable
    private final URI location;
    @JsonProperty
    private final String symbolicName;
    @JsonProperty
    @Nullable
    private final String name;
    @JsonProperty
    private final String version;
    @JsonProperty
    private final Map<String, URI> links;

    @JsonCreator
    BundleSummaryRepresentation(@JsonProperty(value="state") Bundle.State state, @JsonProperty(value="id") long id, @JsonProperty(value="location") @Nullable URI location, @JsonProperty(value="symbolicName") String symbolicName, @JsonProperty(value="name") @Nullable String name, @JsonProperty(value="version") String version, @JsonProperty(value="links") Map<String, URI> links) {
        this.state = Objects.requireNonNull(state, "state");
        this.id = id;
        this.location = location;
        this.symbolicName = Objects.requireNonNull(symbolicName, "symbolicName");
        this.name = name;
        this.version = Objects.requireNonNull(version, "version");
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
    }

    BundleSummaryRepresentation(Bundle bundle, UpmUriBuilder uriBuilder) {
        this.state = bundle.getState();
        this.id = bundle.getId();
        this.location = bundle.getLocation();
        this.symbolicName = bundle.getSymbolicName();
        this.name = bundle.getName();
        this.version = bundle.getVersion().toString();
        this.links = Collections.singletonMap("self", uriBuilder.buildOsgiBundleUri(bundle));
    }

    public Bundle.State getState() {
        return this.state;
    }

    public long getId() {
        return this.id;
    }

    @Nullable
    public URI getLocation() {
        return this.location;
    }

    public String getSymbolicName() {
        return this.symbolicName;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public static Wrapper<Bundle, BundleSummaryRepresentation> wrapSummary(final UpmUriBuilder uriBuilder) {
        return new Wrapper<Bundle, BundleSummaryRepresentation>("bundleSummaryRepresentation"){

            @Override
            public BundleSummaryRepresentation wrap(Bundle bundle) {
                return new BundleSummaryRepresentation(bundle, uriBuilder);
            }
        };
    }
}

