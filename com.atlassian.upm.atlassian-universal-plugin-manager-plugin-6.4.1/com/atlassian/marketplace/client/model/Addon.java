/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.AddonExternalLinkType;
import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonDistributionSummary;
import com.atlassian.marketplace.client.model.AddonReviewsSummary;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.HtmlString;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.ReadOnly;
import com.atlassian.marketplace.client.model.VendorSummary;
import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

public class Addon
extends AddonBase {
    Embedded _embedded;
    Option<Boolean> enableAtlassianAnswers;
    Map<String, URI> vendorLinks;
    @ReadOnly
    Option<LegacyProperties> legacy;

    public Option<ImageInfo> getBanner() {
        return this._embedded.banner;
    }

    @Override
    public Option<ImageInfo> getLogo() {
        return this._embedded.logo;
    }

    @Override
    public Iterable<AddonCategorySummary> getCategories() {
        return this._embedded.categories;
    }

    @Override
    public AddonDistributionSummary getDistribution() {
        return this._embedded.distribution;
    }

    @Override
    public AddonReviewsSummary getReviews() {
        return this._embedded.reviews;
    }

    @Override
    public Option<VendorSummary> getVendor() {
        return this._embedded.vendor;
    }

    public Option<AddonVersion> getVersion() {
        return this._embedded.version;
    }

    public Option<URI> getSupportDetailsPageUri() {
        return this.getLinks().getUri("support", "text/html");
    }

    public Option<HtmlString> getDescription() {
        Iterator iterator = this.legacy.iterator();
        if (iterator.hasNext()) {
            LegacyProperties l = (LegacyProperties)iterator.next();
            return l.description;
        }
        return Option.none();
    }

    public Option<URI> getExternalLinkUri(AddonExternalLinkType type) {
        if (type.canSetForNewAddons()) {
            return Option.option((Object)this.vendorLinks.get(type.getKey()));
        }
        Iterator iterator = this.legacy.iterator();
        if (iterator.hasNext()) {
            LegacyProperties l = (LegacyProperties)iterator.next();
            return Option.option((Object)l.vendorLinks.get(type.getKey()));
        }
        return Option.none();
    }

    public Option<Boolean> isEnableAtlassianAnswers() {
        return this.enableAtlassianAnswers;
    }

    public Option<Long> getDataCenterBuildNumber() {
        return this._embedded.version.flatMap(v -> v.dataCenterBuildNumber);
    }

    static final class LegacyProperties {
        Option<HtmlString> description;
        Map<String, URI> vendorLinks;

        LegacyProperties() {
        }
    }

    static final class Embedded {
        Option<ImageInfo> banner;
        Option<ImageInfo> logo;
        ImmutableList<AddonCategorySummary> categories;
        AddonDistributionSummary distribution;
        AddonReviewsSummary reviews;
        Option<VendorSummary> vendor;
        Option<AddonVersion> version;

        Embedded() {
        }
    }
}

