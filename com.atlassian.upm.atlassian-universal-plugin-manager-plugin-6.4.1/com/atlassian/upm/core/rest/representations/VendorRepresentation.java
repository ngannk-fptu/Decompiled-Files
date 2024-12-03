/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.representations;

import java.net.URI;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class VendorRepresentation {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final URI link;
    @JsonProperty
    private final URI marketplaceLink;
    @JsonProperty
    private final Boolean topVendor;

    @JsonCreator
    public VendorRepresentation(@JsonProperty(value="name") String name, @JsonProperty(value="marketplaceLink") URI marketplaceLink, @JsonProperty(value="topVendor") Boolean topVendor) {
        this.name = Objects.requireNonNull(name, "name");
        this.link = marketplaceLink;
        this.marketplaceLink = marketplaceLink;
        this.topVendor = topVendor;
    }

    public String getName() {
        return this.name;
    }

    public URI getLink() {
        return this.link;
    }

    public URI getMarketplaceLink() {
        return this.marketplaceLink;
    }

    public Boolean getTopVendor() {
        return this.topVendor;
    }
}

