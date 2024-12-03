/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.api.license.entity.Contact;
import com.atlassian.upm.api.license.entity.LicenseEditionType;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.Organization;
import com.atlassian.upm.api.license.entity.Partner;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.HostApplicationEmbeddedAddonLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;
import com.atlassian.upm.license.internal.HostApplicationLicenses;
import com.atlassian.upm.license.internal.LicenseEntityFactory;
import com.atlassian.upm.license.internal.ProductLicenses;
import com.atlassian.upm.license.internal.impl.PluginLicenseImpl;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginMetadata;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.joda.time.DateTime;

public class PluginLicenseBuilder {
    String pluginKey;
    String rawLicense;
    Option<Integer> licenseVersion;
    String description;
    String serverId;
    Organization organization;
    Option<Partner> partner;
    Iterable<Contact> contacts;
    DateTime creationDate;
    DateTime purchaseDate;
    Option<DateTime> expiryDate;
    Option<String> supportEntitlementNumber;
    Option<DateTime> maintenanceExpiryDate;
    Option<DateTime> subscriptionEndDate;
    Option<DateTime> buildDate;
    Option<Integer> edition;
    Option<Integer> hostLicenseEdition;
    Option<RoleBasedPluginMetadata> roleBasedPluginMetadata;
    boolean evaluation;
    boolean hostEvaluation;
    boolean subscription;
    boolean embeddedWithinHostLicense;
    boolean active;
    boolean dataCenter;
    boolean hostDataCenter;
    boolean legacyEnterprise;
    boolean autoRenewal;
    LicenseType licenseType;
    Option<LicenseType> hostLicenseType;
    String pluginName;
    LicenseEditionType editionType;
    Option<SubscriptionPeriod> subscriptionPeriod;
    boolean dataCenterCompatibleApp;
    boolean appStackLicense;
    boolean hostStackLicense;
    boolean forged;

    private PluginLicenseBuilder() {
    }

    public static PluginLicenseBuilder from(ProductLicense license, String pluginKey, Option<Plugin> plugin, String rawLicense, boolean isForgedLicense, HostApplicationLicenseAttributes hostLicense, RoleBasedLicensingPluginService roleBasedService, LicenseEntityFactory factory, ApplicationProperties applicationProperties) {
        Objects.requireNonNull(license, "license");
        Objects.requireNonNull(pluginKey, "pluginKey");
        Objects.requireNonNull(rawLicense, "rawLicense");
        Product product = new Product(pluginKey, pluginKey, true);
        HostApplicationLicenses.LicenseEditionAndRoleCount editionInfo = ProductLicenses.getEditionAndRoleCountForEmbeddedLicense(license, product, plugin, roleBasedService, applicationProperties);
        return new PluginLicenseBuilder().rawLicense(rawLicense).pluginKey(pluginKey).licenseVersion(ProductLicenses.getLicenseVersion(license)).licenseType(LicenseType.valueOf(license.getLicenseType().name())).pluginName(PluginLicenseBuilder.getPluginName(plugin, pluginKey)).description(license.getDescription()).serverId(license.getServerId()).organization(factory.getOrganization(license.getOrganisation())).partner(ProductLicenses.getPartner(license, factory)).contacts(ProductLicenses.getContacts(license, factory)).creationDate(new DateTime((Object)license.getCreationDate())).purchaseDate(new DateTime((Object)license.getPurchaseDate())).expiryDate(ProductLicenses.getExpiryDate(license)).buildDate(factory.getPluginBuildDate(plugin)).maintenanceExpiryDate(ProductLicenses.getMaintenanceExpiryDate(license)).subscriptionEndDate(ProductLicenses.getSubscriptionEndDate(license, product)).supportEntitlementNumber(ProductLicenses.getSupportEntitlementNumber(license)).edition(editionInfo.edition, editionInfo.editionType).roleCount(editionInfo.rbpMeta).hostLicenseEdition(hostLicense.getEdition()).hostLicenseType(Option.some(hostLicense.getLicenseType())).evaluation(ProductLicenses.isEvaluation(license, product)).hostEvaluation(hostLicense.isEvaluation()).dataCenter(ProductLicenses.isDataCenter(license)).legacyEnterprise(ProductLicenses.isLegacyEnterprise(license)).hostDataCenter(hostLicense.isDataCenter()).subscription(license.isSubscription()).autoRenewal(ProductLicenses.isAutoRenewal(license, product)).active(ProductLicenses.isActive(license, product).getOrElse(false)).embeddedWithinHostLicense(false).subscriptionPeriod(ProductLicenses.getSubscriptionPeriod(license, product)).dataCenterCompatibleApp(PluginLicenseBuilder.isDataCenterCompatibleApp(plugin)).appStackLicense(ProductLicenses.isAtlassianStackLicense(license)).hostStackLicense(hostLicense.isStack()).forged(isForgedLicense);
    }

    public static PluginLicenseBuilder from(HostApplicationEmbeddedAddonLicense license, Option<Plugin> plugin, HostApplicationLicenseAttributes hostLicense, LicenseEntityFactory factory) {
        Objects.requireNonNull(license, "license");
        return new PluginLicenseBuilder().rawLicense(license.getRawLicense()).pluginKey(license.getPluginKey()).licenseVersion(license.getLicenseVersion()).licenseType(license.getLicenseType()).pluginName(PluginLicenseBuilder.getPluginName(plugin, license.getPluginKey())).description(license.getDescription()).serverId(license.getServerId()).organization(license.getOrganization()).partner(license.getPartner()).contacts(license.getContacts()).creationDate(license.getCreationDate()).purchaseDate(license.getPurchaseDate()).expiryDate(license.getExpiryDate()).buildDate(factory.getPluginBuildDate(plugin)).maintenanceExpiryDate(license.getMaintenanceExpiryDate()).subscriptionEndDate(license.getSubscriptionEndDate()).supportEntitlementNumber(license.getSen()).edition(license.getEdition(), license.getEditionType()).roleCount(license.getRoleBasedPluginMetadata()).hostLicenseEdition(hostLicense.getEdition()).hostLicenseType(Option.some(hostLicense.getLicenseType())).evaluation(license.isEvaluation()).hostEvaluation(hostLicense.isEvaluation()).dataCenter(license.isDataCenter()).legacyEnterprise(license.isLegacyEnterprise()).hostDataCenter(hostLicense.isDataCenter()).subscription(license.isSubscription()).autoRenewal(license.isAutoRenewal()).active(license.isActive()).embeddedWithinHostLicense(true).subscriptionPeriod(license.getSubscriptionPeriod()).dataCenterCompatibleApp(PluginLicenseBuilder.isDataCenterCompatibleApp(plugin)).appStackLicense(license.isStack()).hostStackLicense(hostLicense.isStack());
    }

    public PluginLicense build() {
        return new PluginLicenseImpl(this);
    }

    private PluginLicenseBuilder pluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
        return this;
    }

    private PluginLicenseBuilder pluginName(String pluginName) {
        this.pluginName = pluginName;
        return this;
    }

    private PluginLicenseBuilder rawLicense(String rawLicense) {
        this.rawLicense = rawLicense;
        return this;
    }

    private PluginLicenseBuilder licenseVersion(Option<Integer> licenseVersion) {
        this.licenseVersion = licenseVersion;
        return this;
    }

    private PluginLicenseBuilder description(String description) {
        this.description = description;
        return this;
    }

    private PluginLicenseBuilder serverId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    private PluginLicenseBuilder organization(Organization organization) {
        this.organization = organization;
        return this;
    }

    private PluginLicenseBuilder partner(Option<Partner> partner) {
        this.partner = partner;
        return this;
    }

    private PluginLicenseBuilder contacts(List<Contact> contacts) {
        this.contacts = Collections.unmodifiableList(contacts);
        return this;
    }

    private PluginLicenseBuilder creationDate(DateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    private PluginLicenseBuilder purchaseDate(DateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
        return this;
    }

    private PluginLicenseBuilder expiryDate(Option<DateTime> expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    private PluginLicenseBuilder supportEntitlementNumber(Option<String> supportEntitlementNumber) {
        this.supportEntitlementNumber = supportEntitlementNumber;
        return this;
    }

    private PluginLicenseBuilder maintenanceExpiryDate(Option<DateTime> maintenanceExpiryDate) {
        this.maintenanceExpiryDate = maintenanceExpiryDate;
        return this;
    }

    private PluginLicenseBuilder subscriptionEndDate(Option<DateTime> subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
        return this;
    }

    private PluginLicenseBuilder buildDate(Option<DateTime> buildDate) {
        this.buildDate = buildDate;
        return this;
    }

    private PluginLicenseBuilder edition(Option<Integer> edition, LicenseEditionType editionType) {
        this.edition = edition;
        this.editionType = editionType;
        return this;
    }

    private PluginLicenseBuilder roleCount(Option<RoleBasedPluginMetadata> roleBasedPluginMetadata) {
        this.roleBasedPluginMetadata = roleBasedPluginMetadata;
        return this;
    }

    private PluginLicenseBuilder hostLicenseEdition(Option<Integer> hostLicenseEdition) {
        this.hostLicenseEdition = hostLicenseEdition;
        return this;
    }

    private PluginLicenseBuilder hostLicenseType(Option<LicenseType> hostLicenseType) {
        this.hostLicenseType = hostLicenseType;
        return this;
    }

    private PluginLicenseBuilder evaluation(boolean evaluation) {
        this.evaluation = evaluation;
        return this;
    }

    private PluginLicenseBuilder hostEvaluation(boolean hostEvaluation) {
        this.hostEvaluation = hostEvaluation;
        return this;
    }

    private PluginLicenseBuilder dataCenter(boolean dataCenter) {
        this.dataCenter = dataCenter;
        return this;
    }

    private PluginLicenseBuilder legacyEnterprise(boolean legacyEnterprise) {
        this.legacyEnterprise = legacyEnterprise;
        return this;
    }

    private PluginLicenseBuilder hostDataCenter(boolean hostDataCenter) {
        this.hostDataCenter = hostDataCenter;
        return this;
    }

    private PluginLicenseBuilder subscription(boolean subscription) {
        this.subscription = subscription;
        return this;
    }

    private PluginLicenseBuilder autoRenewal(boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
        return this;
    }

    private PluginLicenseBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    private PluginLicenseBuilder embeddedWithinHostLicense(boolean embeddedWithinHostLicense) {
        this.embeddedWithinHostLicense = embeddedWithinHostLicense;
        return this;
    }

    private PluginLicenseBuilder licenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
        return this;
    }

    private PluginLicenseBuilder subscriptionPeriod(Option<SubscriptionPeriod> subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
        return this;
    }

    private PluginLicenseBuilder dataCenterCompatibleApp(boolean dataCenterCompatibleApp) {
        this.dataCenterCompatibleApp = dataCenterCompatibleApp;
        return this;
    }

    private PluginLicenseBuilder appStackLicense(boolean appStackLicense) {
        this.appStackLicense = appStackLicense;
        return this;
    }

    private PluginLicenseBuilder hostStackLicense(boolean hostStackLicense) {
        this.hostStackLicense = hostStackLicense;
        return this;
    }

    private PluginLicenseBuilder forged(boolean forged) {
        this.forged = forged;
        return this;
    }

    private static String getPluginName(Option<Plugin> maybePlugin, String pluginKey) {
        Iterator<Plugin> iterator = maybePlugin.iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            return plugin.getName();
        }
        return pluginKey;
    }

    private static boolean isDataCenterCompatibleApp(Option<Plugin> maybePlugin) {
        return (Boolean)maybePlugin.map(plugin -> PluginInfoUtils.isStatusDataCenterCompatibleAccordingToPluginDescriptor(plugin.getPluginInformation())).getOrElse(false);
    }
}

