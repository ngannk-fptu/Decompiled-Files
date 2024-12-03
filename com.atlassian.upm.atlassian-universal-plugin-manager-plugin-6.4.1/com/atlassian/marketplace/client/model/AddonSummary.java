/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonDistributionSummary;
import com.atlassian.marketplace.client.model.AddonReviewsSummary;
import com.atlassian.marketplace.client.model.AddonVersionSummary;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.VendorSummary;
import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Option;

public class AddonSummary
extends AddonBase {
    Embedded _embedded;

    @Override
    public Iterable<AddonCategorySummary> getCategories() {
        return this._embedded.categories;
    }

    @Override
    public AddonDistributionSummary getDistribution() {
        return this._embedded.distribution;
    }

    @Override
    public Option<ImageInfo> getLogo() {
        return this._embedded.logo;
    }

    @Override
    public AddonReviewsSummary getReviews() {
        return this._embedded.reviews;
    }

    @Override
    public Option<VendorSummary> getVendor() {
        return this._embedded.vendor;
    }

    public Option<AddonVersionSummary> getVersion() {
        return this._embedded.version;
    }

    static final class Embedded {
        ImmutableList<AddonCategorySummary> categories;
        AddonDistributionSummary distribution;
        Option<ImageInfo> logo;
        AddonReviewsSummary reviews;
        Option<VendorSummary> vendor;
        Option<AddonVersionSummary> version;

        Embedded() {
        }
    }
}

