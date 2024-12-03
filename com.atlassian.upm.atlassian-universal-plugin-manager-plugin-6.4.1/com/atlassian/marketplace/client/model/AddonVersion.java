/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.AddonVersionExternalLinkType;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.api.LicenseTypeId;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.marketplace.client.model.AddonVersionStatus;
import com.atlassian.marketplace.client.model.ArtifactInfo;
import com.atlassian.marketplace.client.model.Highlight;
import com.atlassian.marketplace.client.model.HtmlString;
import com.atlassian.marketplace.client.model.LicenseType;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.marketplace.client.model.ReadOnly;
import com.atlassian.marketplace.client.model.Screenshot;
import com.atlassian.marketplace.client.model.VersionCompatibility;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public final class AddonVersion
extends AddonVersionBase {
    Embedded _embedded;
    Long buildNumber;
    transient Option<Long> dataCenterBuildNumber;
    Option<String> youtubeId;
    Option<ImmutableList<VersionCompatibility>> compatibilities;
    TextProperties text;
    @ReadOnly
    Option<LegacyProperties> legacy;

    @Override
    public Option<ArtifactInfo> getArtifactInfo() {
        return this._embedded.artifact;
    }

    @Override
    public Option<URI> getArtifactUri() {
        Iterator iterator = this._embedded.artifact.iterator();
        if (iterator.hasNext()) {
            ArtifactInfo a = (ArtifactInfo)iterator.next();
            return Option.some((Object)a.getBinaryUri());
        }
        return Option.none();
    }

    @Override
    public Option<URI> getRemoteDescriptorUri() {
        Iterator iterator = this._embedded.artifact.iterator();
        if (iterator.hasNext()) {
            ArtifactInfo a = (ArtifactInfo)iterator.next();
            return a.getRemoteDescriptorUri();
        }
        return Option.none();
    }

    public long getBuildNumber() {
        return this.buildNumber;
    }

    public Option<Long> getDataCenterBuildNumber() {
        return this.dataCenterBuildNumber;
    }

    public Iterable<VersionCompatibility> getCompatibilities() {
        return (Iterable)this.compatibilities.getOrElse((Object)ImmutableList.of());
    }

    public Option<Iterable<VersionCompatibility>> getCompatibilitiesIfSpecified() {
        return this.compatibilities.map(Function.identity());
    }

    @Override
    public Option<URI> getExternalLinkUri(AddonVersionExternalLinkType type) {
        if (type.canSetForNewAddonVersions()) {
            return Option.option(this.vendorLinks.get(type.getKey()));
        }
        Iterator iterator = this.legacy.iterator();
        if (iterator.hasNext()) {
            LegacyProperties l = (LegacyProperties)iterator.next();
            return Option.option((Object)l.vendorLinks.get(type.getKey()));
        }
        return Option.none();
    }

    @Override
    public Iterable<AddonCategorySummary> getFunctionalCategories() {
        return this._embedded.functionalCategories;
    }

    public Iterable<Highlight> getHighlights() {
        return (Iterable)this._embedded.highlights.getOrElse((Object)ImmutableList.of());
    }

    public Option<Iterable<Highlight>> getHighlightsIfSpecified() {
        return this._embedded.highlights.map(Function.identity());
    }

    public Option<LicenseType> getLicenseType() {
        return this._embedded.license;
    }

    @Override
    public Option<LicenseTypeId> getLicenseTypeId() {
        Iterator iterator = this.getLinks().getUri("license").iterator();
        if (iterator.hasNext()) {
            URI u = (URI)iterator.next();
            return Option.some((Object)LicenseTypeId.fromUri(u));
        }
        return Option.none();
    }

    public Option<HtmlString> getMoreDetails() {
        return this.text.moreDetails;
    }

    @Override
    public PaymentModel getPaymentModel() {
        return this.paymentModel;
    }

    public Option<HtmlString> getReleaseNotes() {
        return this.text.releaseNotes;
    }

    public Option<String> getReleaseSummary() {
        return this.text.releaseSummary;
    }

    public Iterable<Screenshot> getScreenshots() {
        return (Iterable)this._embedded.screenshots.getOrElse((Object)ImmutableList.of());
    }

    public Option<Iterable<Screenshot>> getScreenshotsIfSpecified() {
        return this._embedded.screenshots.map(Function.identity());
    }

    @Override
    public AddonVersionStatus getStatus() {
        return this.status;
    }

    @Override
    public boolean isStatic() {
        return this.staticAddon;
    }

    public Option<String> getYoutubeId() {
        return this.youtubeId;
    }

    public Iterable<ApplicationKey> getCompatibleApplications() {
        return Iterables.transform(this.getCompatibilities(), VersionCompatibility::getApplication);
    }

    public boolean isCompatibleWithApplication(ApplicationKey application) {
        for (VersionCompatibility c : this.getCompatibilities()) {
            if (!c.getApplication().equals(application)) continue;
            return true;
        }
        return false;
    }

    public boolean isCompatibleWith(Predicate<ApplicationKey> applicationCriteria, HostingType hostingType, int build) {
        for (VersionCompatibility c : this.getCompatibilities()) {
            if (!c.isCompatibleWith(applicationCriteria, hostingType, build)) continue;
            return true;
        }
        return false;
    }

    static final class TextProperties {
        Option<String> releaseSummary;
        Option<HtmlString> moreDetails;
        Option<HtmlString> releaseNotes;

        TextProperties() {
        }
    }

    static final class LegacyProperties {
        Map<String, URI> vendorLinks;

        LegacyProperties() {
        }
    }

    static final class Embedded {
        Option<ArtifactInfo> artifact;
        ImmutableList<AddonCategorySummary> functionalCategories;
        Option<ImmutableList<Highlight>> highlights;
        Option<LicenseType> license;
        Option<ImmutableList<Screenshot>> screenshots;

        Embedded() {
        }
    }
}

