/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.LicenseTypeId;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.RequiredLink;
import io.atlassian.fugue.Option;
import java.net.URI;

public final class LicenseType
implements Entity {
    Links _links;
    String key;
    String name;
    @RequiredLink(rel="self")
    URI selfUri;

    @Override
    public Links getLinks() {
        return this._links;
    }

    @Override
    public URI getSelfUri() {
        return this.selfUri;
    }

    public LicenseTypeId getId() {
        return LicenseTypeId.fromUri(this.selfUri);
    }

    public Option<URI> getAlternateUri() {
        return this._links.getUri("alternate");
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }
}

