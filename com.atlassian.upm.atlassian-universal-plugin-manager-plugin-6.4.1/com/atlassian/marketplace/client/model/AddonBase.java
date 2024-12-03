/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.AddonCategoryId;
import com.atlassian.marketplace.client.api.PricingType;
import com.atlassian.marketplace.client.api.VendorId;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonDistributionSummary;
import com.atlassian.marketplace.client.model.AddonReviewsSummary;
import com.atlassian.marketplace.client.model.AddonStatus;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.ReadOnly;
import com.atlassian.marketplace.client.model.RequiredLink;
import com.atlassian.marketplace.client.model.VendorSummary;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Map;

public abstract class AddonBase
implements Entity {
    Links _links;
    String name;
    String key;
    AddonStatus status;
    Option<String> summary;
    Option<String> tagLine;
    @Deprecated
    @ReadOnly
    Option<Integer> cloudFreeUsers;
    @RequiredLink(rel="self")
    URI selfUri;
    @RequiredLink(rel="alternate")
    URI alternateUri;
    @RequiredLink(rel="vendor")
    URI vendorUri;
    Option<Boolean> storesPersonalData;

    @Override
    public Links getLinks() {
        return this._links;
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

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public AddonStatus getStatus() {
        return this.status;
    }

    public Option<String> getSummary() {
        return this.summary;
    }

    public Option<String> getTagLine() {
        return this.tagLine;
    }

    @Deprecated
    public Option<Integer> getCloudFreeUsers() {
        return this.cloudFreeUsers;
    }

    public Option<URI> getPricingUri(PricingType pricingType) {
        return this.getLinks().getUriTemplate("pricing", "application/json").map(t -> t.resolve((Map<String, String>)ImmutableMap.of((Object)"cloudOrServer", (Object)pricingType.getKey(), (Object)"liveOrPending", (Object)"live")));
    }

    public Option<URI> getPricingDetailsPageUri() {
        return this.getLinks().getUri("pricing", "text/html");
    }

    public Option<URI> getReviewDetailsPageUri() {
        return this.getLinks().getUri("reviews", "text/html");
    }

    public Iterable<AddonCategoryId> getCategoryIds() {
        return Iterables.transform(this.getLinks().getLinks("categories"), l -> AddonCategoryId.fromUri(l.getUri()));
    }

    public Option<Boolean> storesPersonalData() {
        return this.storesPersonalData;
    }

    public abstract Iterable<AddonCategorySummary> getCategories();

    public abstract AddonDistributionSummary getDistribution();

    public abstract Option<ImageInfo> getLogo();

    public abstract AddonReviewsSummary getReviews();

    public abstract Option<VendorSummary> getVendor();
}

