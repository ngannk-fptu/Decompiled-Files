/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.AddonCategoryId;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.RequiredLink;
import java.net.URI;

public final class AddonCategorySummary
implements Entity {
    Links _links;
    @RequiredLink(rel="self")
    URI selfUri;
    String name;

    @Override
    public Links getLinks() {
        return this._links;
    }

    @Override
    public URI getSelfUri() {
        return this.selfUri;
    }

    public AddonCategoryId getId() {
        return AddonCategoryId.fromUri(this.selfUri);
    }

    public String getName() {
        return this.name;
    }
}

