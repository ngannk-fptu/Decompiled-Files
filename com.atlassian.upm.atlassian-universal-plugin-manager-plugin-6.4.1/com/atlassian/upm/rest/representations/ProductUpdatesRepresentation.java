/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.HostStatusRepresentation;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

public class ProductUpdatesRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final Collection<ProductUpdateEntry> versions;
    @JsonProperty
    private final HostStatusRepresentation hostStatus;

    @JsonCreator
    public ProductUpdatesRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="versions") Collection<ProductUpdateEntry> versions, @JsonProperty(value="safeMode") HostStatusRepresentation hostStatus) {
        this.links = links;
        this.versions = versions;
        this.hostStatus = hostStatus;
    }

    public ProductUpdatesRepresentation(UpmUriBuilder uriBuilder, Collection<ApplicationVersion> products, UpmLinkBuilder linkBuilder, HostStatusRepresentation hostStatus) {
        this.links = linkBuilder.buildLinksFor(uriBuilder.buildProductUpdatesUri()).build();
        this.versions = Collections.unmodifiableList(products.stream().map(this.toEntries(uriBuilder)).collect(Collectors.toList()));
        this.hostStatus = hostStatus;
    }

    public Collection<ProductUpdateEntry> getVersions() {
        return this.versions;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public HostStatusRepresentation getHostStatus() {
        return this.hostStatus;
    }

    private Function<ApplicationVersion, ProductUpdateEntry> toEntries(UpmUriBuilder uriBuilder) {
        return product -> new ProductUpdateEntry((ApplicationVersion)product, uriBuilder);
    }

    public static final class ProductUpdateEntry {
        @JsonProperty
        private final String version;
        @JsonProperty
        private final boolean recent;
        @JsonProperty
        private final Map<String, URI> links;

        @JsonCreator
        public ProductUpdateEntry(@JsonProperty(value="version") String version, @JsonProperty(value="recent") boolean recent, @JsonProperty(value="links") Map<String, URI> links) {
            this.version = Objects.requireNonNull(version, "version");
            this.recent = recent;
            this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        }

        public ProductUpdateEntry(ApplicationVersion product, UpmUriBuilder uriBuilder) {
            this.version = product.getName();
            this.recent = ProductUpdateEntry.isRecent(product);
            this.links = Collections.singletonMap("self", uriBuilder.buildProductUpdatePluginCompatibilityUri(product.getBuildNumber()));
        }

        public URI getSelf() {
            return this.links.get("self");
        }

        public String getVersion() {
            return this.version;
        }

        public boolean isRecent() {
            return this.recent;
        }

        private static boolean isRecent(ApplicationVersion product) {
            Date releaseDate = product.getReleaseDate().toDateMidnight().toDate();
            return releaseDate == null ? false : new DateTime((Object)releaseDate).isAfter((ReadableInstant)new DateTime().minusWeeks(2));
        }
    }
}

