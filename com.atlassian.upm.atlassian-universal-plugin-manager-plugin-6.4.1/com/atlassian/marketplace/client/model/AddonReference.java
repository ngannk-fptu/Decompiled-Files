/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.VendorId;
import com.atlassian.marketplace.client.model.AddonReviewsSummary;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.RequiredLink;
import io.atlassian.fugue.Option;
import java.net.URI;

public class AddonReference
implements Entity {
    Links _links;
    Embedded _embedded;
    String name;
    String key;
    @RequiredLink(rel="self")
    URI selfUri;
    @RequiredLink(rel="alternate")
    URI alternateUri;
    @RequiredLink(rel="vendor")
    URI vendorUri;

    @Override
    public Links getLinks() {
        return this._links;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public URI getSelfUri() {
        return this.selfUri;
    }

    public URI getAlternateUri() {
        return this.alternateUri;
    }

    public VendorId getVendorId() {
        return VendorId.fromUri(this.vendorUri);
    }

    public Option<ImageInfo> getImage() {
        return this._embedded.image;
    }

    public Option<AddonReviewsSummary> getReviews() {
        return this._embedded.reviews;
    }

    static final class Embedded {
        Option<ImageInfo> image;
        Option<AddonReviewsSummary> reviews;

        Embedded() {
        }
    }
}

