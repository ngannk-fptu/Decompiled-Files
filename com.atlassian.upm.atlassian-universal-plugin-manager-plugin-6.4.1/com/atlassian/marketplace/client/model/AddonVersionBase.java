/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 *  org.joda.time.LocalDate
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.AddonCategoryId;
import com.atlassian.marketplace.client.api.AddonVersionExternalLinkType;
import com.atlassian.marketplace.client.api.LicenseTypeId;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonVersionDataCenterStatus;
import com.atlassian.marketplace.client.model.AddonVersionStatus;
import com.atlassian.marketplace.client.model.ArtifactInfo;
import com.atlassian.marketplace.client.model.ConnectScope;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.marketplace.client.model.ReadOnly;
import com.atlassian.marketplace.client.model.RequiredLink;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import org.joda.time.LocalDate;

public abstract class AddonVersionBase
implements Entity {
    Links _links;
    Option<String> name;
    AddonVersionStatus status;
    PaymentModel paymentModel;
    boolean staticAddon;
    boolean deployable;
    ReleaseProperties release;
    @ReadOnly
    DeploymentProperties deployment;
    Map<String, URI> vendorLinks;
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

    public Option<String> getName() {
        return this.name;
    }

    public PaymentModel getPaymentModel() {
        return this.paymentModel;
    }

    public AddonVersionStatus getStatus() {
        return this.status;
    }

    public boolean isBeta() {
        return this.release.beta;
    }

    public boolean isStatic() {
        return this.staticAddon;
    }

    public boolean isSupported() {
        return this.release.supported;
    }

    public boolean isDeployable() {
        return this.deployable;
    }

    public Iterable<ConnectScope> getConnectScopes() {
        return (Iterable)this.deployment.permissions.getOrElse((Object)ImmutableList.of());
    }

    public boolean isServer() {
        return this.deployment.server;
    }

    public boolean isCloud() {
        return this.deployment.cloud;
    }

    public boolean isAutoUpdateAllowed() {
        return this.deployment.autoUpdateAllowed;
    }

    public boolean isConnect() {
        return this.deployment.connect;
    }

    @Deprecated
    public boolean isDataCenterCompatible() {
        return this.deployment.dataCenter;
    }

    public Option<AddonVersionDataCenterStatus> getDataCenterStatus() {
        return this.deployment.dataCenterStatus;
    }

    public boolean isDataCenterStatusCompatible() {
        return this.deployment.dataCenterStatus.exists(AddonVersionDataCenterStatus.COMPATIBLE::equals);
    }

    public Option<LicenseTypeId> getLicenseTypeId() {
        Iterator iterator = this.getLinks().getUri("license").iterator();
        if (iterator.hasNext()) {
            URI uri = (URI)iterator.next();
            return Option.some((Object)LicenseTypeId.fromUri(uri));
        }
        return Option.none();
    }

    public LocalDate getReleaseDate() {
        return this.release.date;
    }

    public Option<String> getReleasedBy() {
        return this.release.releasedBy;
    }

    public abstract Option<ArtifactInfo> getArtifactInfo();

    public abstract Option<URI> getArtifactUri();

    public abstract Option<URI> getRemoteDescriptorUri();

    public Iterable<AddonCategoryId> getFunctionalCategoryIds() {
        return Iterables.transform(this.getLinks().getLinks("functionalCategories"), l -> AddonCategoryId.fromUri(l.getUri()));
    }

    public Option<URI> getExternalLinkUri(AddonVersionExternalLinkType type) {
        if (type.canSetForNewAddonVersions()) {
            return Option.option((Object)this.vendorLinks.get(type.getKey()));
        }
        return Option.none();
    }

    public abstract Iterable<AddonCategorySummary> getFunctionalCategories();

    static final class ReleaseProperties {
        LocalDate date;
        Option<String> releasedBy;
        Boolean beta;
        Boolean supported;

        ReleaseProperties() {
        }
    }

    static final class DeploymentProperties {
        Boolean server;
        Boolean cloud;
        Boolean connect;
        Boolean autoUpdateAllowed;
        Option<ImmutableList<ConnectScope>> permissions;
        @Deprecated
        Boolean dataCenter;
        Option<AddonVersionDataCenterStatus> dataCenterStatus;

        DeploymentProperties() {
        }
    }
}

