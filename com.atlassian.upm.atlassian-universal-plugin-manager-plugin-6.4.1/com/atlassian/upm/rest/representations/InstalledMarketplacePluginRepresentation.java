/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.upm.Iterables;
import com.atlassian.upm.PluginPrimaryAction;
import com.atlassian.upm.PluginUpdateRequestStore;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.IncompatiblePluginData;
import com.atlassian.upm.rest.representations.PacDetailsRepresentation;
import com.atlassian.upm.rest.representations.PluginLicenseRepresentation;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class InstalledMarketplacePluginRepresentation {
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final PluginLicenseRepresentation licenseDetails;
    @JsonProperty
    private final boolean licenseReadOnly;
    @JsonProperty
    private final String hamsProductKey;
    @Deprecated
    @JsonProperty
    private final boolean dataCenterCompatible;
    @JsonProperty
    private final boolean statusDataCenterCompatible;
    @JsonProperty
    private final PluginPrimaryActionRepresentation primaryAction;
    @JsonProperty
    private final boolean updatableToPaid;
    @JsonProperty
    private final Boolean incompatible;
    @JsonProperty
    private final PacDetailsRepresentation.AvailablePluginUpdateRepresentation update;
    @JsonProperty
    private final boolean dataCenterApp;
    @JsonProperty
    private final boolean carebearServerTryOrBuy;
    @JsonProperty
    private final boolean carebearServerSpecific;

    @JsonCreator
    public InstalledMarketplacePluginRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="key") String key, @JsonProperty(value="name") String name, @JsonProperty(value="licenseDetails") PluginLicenseRepresentation licenseDetails, @JsonProperty(value="licenseReadOnly") boolean licenseReadOnly, @JsonProperty(value="primaryAction") PluginPrimaryActionRepresentation primaryAction, @JsonProperty(value="hamsProductKey") String hamsProductKey, @JsonProperty(value="updatableToPaid") boolean updatableToPaid, @JsonProperty(value="incompatible") Boolean incompatible, @JsonProperty(value="dataCenterCompatible") boolean dataCenterCompatible, @JsonProperty(value="statusDataCenterCompatible") boolean statusDataCenterCompatible, @JsonProperty(value="update") PacDetailsRepresentation.AvailablePluginUpdateRepresentation update, @JsonProperty(value="dataCenterApp") boolean dataCenterApp, @JsonProperty(value="carebearServerTryOrBuy") boolean carebearServerTryOrBuy, @JsonProperty(value="carebearServerSpecific") boolean carebearServerSpecific) {
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.key = key;
        this.name = name;
        this.licenseDetails = licenseDetails;
        this.licenseReadOnly = licenseReadOnly;
        this.hamsProductKey = hamsProductKey;
        this.dataCenterCompatible = dataCenterCompatible;
        this.statusDataCenterCompatible = statusDataCenterCompatible;
        this.primaryAction = primaryAction;
        this.updatableToPaid = updatableToPaid;
        this.incompatible = incompatible;
        this.update = update;
        this.dataCenterApp = dataCenterApp;
        this.carebearServerTryOrBuy = carebearServerTryOrBuy;
        this.carebearServerSpecific = carebearServerSpecific;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public URI getSelfLink() {
        return this.links.get("self");
    }

    public boolean isUpdateAvailable() {
        return this.links.get("update-details") != null;
    }

    public URI getPluginIconLink() {
        return this.links.get("plugin-icon");
    }

    public URI getPluginLogoLink() {
        return this.links.get("plugin-logo");
    }

    public URI getLicenseAdminUri() {
        return this.getLinks().get("license-admin");
    }

    public URI getPacDetailsLink() {
        return this.getLinks().get("pac-details");
    }

    public URI getPostInstallUrl() {
        return this.getLinks().get("post-install");
    }

    public URI getPostUpdateUrl() {
        return this.getLinks().get("post-update");
    }

    public PluginPrimaryActionRepresentation getPrimaryAction() {
        return this.primaryAction;
    }

    public URI getPluginDetailsLink() {
        return this.getLinks().get("plugin-details");
    }

    public String getHamsProductKey() {
        return this.hamsProductKey;
    }

    public PluginLicenseRepresentation getLicenseDetails() {
        return this.licenseDetails;
    }

    public boolean getDataCenterApp() {
        return this.dataCenterApp;
    }

    public boolean isLicenseReadOnly() {
        return this.licenseReadOnly;
    }

    public boolean isCarebearServerSpecific() {
        return this.carebearServerSpecific;
    }

    @Deprecated
    public boolean isDataCenterCompatible() {
        return this.dataCenterCompatible;
    }

    public boolean isStatusDataCenterCompatible() {
        return this.statusDataCenterCompatible;
    }

    public PacDetailsRepresentation.AvailablePluginUpdateRepresentation getUpdate() {
        return this.update;
    }

    public static Function<Plugin, InstalledMarketplacePluginRepresentation> toEntry(UpmRepresentationFactory representationFactory, UpmHostApplicationInformation appInfo, Iterable<AvailableAddonWithVersion> updates, Iterable<IncompatiblePluginData> incompatibles) {
        return new PluginToRepFunction(representationFactory, appInfo, updates, incompatibles);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static class PluginPrimaryActionRepresentation {
        @JsonProperty
        private final String name;
        @JsonProperty
        private final int priority;
        @JsonProperty
        private final boolean actionRequired;
        @JsonProperty
        private final boolean incompatible;
        @JsonProperty
        private final boolean nonDataCenterApproved;
        @JsonProperty
        private final boolean licenseIncompatibleInDataCenter;

        @JsonCreator
        public PluginPrimaryActionRepresentation(@JsonProperty(value="name") String name, @JsonProperty(value="priority") int priority, @JsonProperty(value="actionRequired") boolean actionRequired, @JsonProperty(value="incompatible") boolean incompatible, @JsonProperty(value="nonDataCenterApproved") boolean nonDataCenterApproved, @JsonProperty(value="licenseIncompatibleInDataCenter") boolean licenseIncompatibleInDataCenter) {
            this.name = name;
            this.priority = priority;
            this.actionRequired = actionRequired;
            this.incompatible = incompatible;
            this.nonDataCenterApproved = nonDataCenterApproved;
            this.licenseIncompatibleInDataCenter = licenseIncompatibleInDataCenter;
        }

        public PluginPrimaryActionRepresentation(PluginPrimaryAction action, Plugin plugin, PermissionEnforcer permissionEnforcer, PluginLicenseRepository licenseRepository, PluginUpdateRequestStore pluginUpdateRequestStore) {
            this.name = action.name().toLowerCase();
            this.priority = action.getPriority();
            Option<PluginLicense> license = licenseRepository.getPluginLicense(plugin.getKey());
            this.actionRequired = action == PluginPrimaryAction.INCOMPATIBLE_REQUESTED_UPDATE || action == PluginPrimaryAction.INCOMPATIBLE_DATA_CENTER_REQUESTED_UPDATE ? false : (action.canRequestUpdateFromVendor() ? permissionEnforcer.hasPermission(Permission.REQUEST_PLUGIN_UPDATE) && !pluginUpdateRequestStore.isPluginUpdateRequested(plugin) : true);
            this.incompatible = action.isIncompatible();
            this.nonDataCenterApproved = action.isNonDataCenterApproved();
            this.licenseIncompatibleInDataCenter = action.isLicenseIncompatibleInDataCenter();
        }

        public String getName() {
            return this.name;
        }

        public int getPriority() {
            return this.priority;
        }

        public boolean isActionRequired() {
            return this.actionRequired;
        }
    }

    private static final class PluginToRepFunction
    implements Function<Plugin, InstalledMarketplacePluginRepresentation> {
        private final UpmRepresentationFactory representationFactory;
        private final UpmHostApplicationInformation appInfo;
        private final Iterable<AvailableAddonWithVersion> updates;
        private final Iterable<IncompatiblePluginData> incompatibles;

        PluginToRepFunction(UpmRepresentationFactory representationFactory, UpmHostApplicationInformation appInfo, Iterable<AvailableAddonWithVersion> updates, Iterable<IncompatiblePluginData> incompatibles) {
            this.representationFactory = representationFactory;
            this.appInfo = appInfo;
            this.updates = updates;
            this.incompatibles = incompatibles;
        }

        @Override
        public InstalledMarketplacePluginRepresentation apply(Plugin plugin) {
            return this.representationFactory.createInstalledMarketplacePluginRepresentation(plugin, this.findVersion(this.updates, plugin), this.findIncompatible(this.incompatibles, plugin));
        }

        private Option<AvailableAddonWithVersion> findVersion(Iterable<AvailableAddonWithVersion> updates, Plugin plugin) {
            return Iterables.findOption(updates, addon -> plugin.getKey().equals(addon.getAddon().getKey()));
        }

        private Option<IncompatiblePluginData> findIncompatible(Iterable<IncompatiblePluginData> incompatibles, Plugin plugin) {
            return Iterables.findOption(incompatibles, incompatible -> plugin.getKey().equals(incompatible.getKey()));
        }
    }
}

