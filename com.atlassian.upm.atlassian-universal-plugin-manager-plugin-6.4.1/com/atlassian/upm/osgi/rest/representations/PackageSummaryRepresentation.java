/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.osgi.rest.representations;

import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.impl.Wrapper;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PackageSummaryRepresentation {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String version;
    @JsonProperty
    Map<String, URI> links;

    @JsonCreator
    public PackageSummaryRepresentation(@JsonProperty(value="name") String name, @JsonProperty(value="version") String version, @JsonProperty(value="links") Map<String, URI> links) {
        this.name = Objects.requireNonNull(name, "name");
        this.version = Objects.requireNonNull(version, "version");
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
    }

    public PackageSummaryRepresentation(Package pkg, UpmUriBuilder uriBuilder) {
        this.name = pkg.getName();
        this.version = pkg.getVersion().toString();
        this.links = Collections.singletonMap("self", uriBuilder.buildOsgiPackageUri(pkg));
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public static Wrapper<Package, PackageSummaryRepresentation> wrapSummary(final UpmUriBuilder uriBuilder) {
        return new Wrapper<Package, PackageSummaryRepresentation>("packageSummaryRepresentation"){

            @Override
            public PackageSummaryRepresentation wrap(Package pkg) {
                return new PackageSummaryRepresentation(pkg, uriBuilder);
            }
        };
    }
}

