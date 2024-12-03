/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.RequiredLink;
import java.net.URI;

public class ConnectScope {
    Links _links;
    @RequiredLink(rel="alternate")
    URI alternateUri;
    String key;
    String name;
    String description;

    public Links getLinks() {
        return this._links;
    }

    public URI getAlternateUri() {
        return this.alternateUri;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}

