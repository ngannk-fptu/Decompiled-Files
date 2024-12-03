/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 *  org.joda.time.base.AbstractInstant
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.Contact;
import com.atlassian.upm.api.license.entity.LicenseEditionType;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.rest.representations.AbstractLicenseDetailsRepresentation;
import com.atlassian.upm.license.PluginLicenses;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.impl.PluginLicensesInternal;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRole;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.base.AbstractInstant;

public final class PluginLicenseRepresentation
extends AbstractLicenseDetailsRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final String pluginKey;
    @JsonProperty
    private final LicenseError error;
    @JsonProperty
    private final String licenseTypeDescriptionKey;
    @JsonProperty
    private final Boolean nearlyExpired;
    @JsonProperty
    private final Date maintenanceExpiryDate;
    @JsonProperty
    private final String maintenanceExpiryDateString;
    @JsonProperty
    private final Boolean maintenanceExpired;
    @JsonProperty
    private final String rawLicense;
    @JsonProperty
    private final Boolean renewable;
    @JsonProperty
    private final Integer currentRoleCount;
    @JsonProperty
    private final Boolean upgradable;
    @JsonProperty
    private final Boolean crossgradeable;
    @JsonProperty
    private final Boolean forged;
    @JsonProperty
    private final Boolean purchasePastServerCutoffDate;
    @JsonProperty
    private final URI roleManagementUri;
    @JsonProperty
    private final String typeI18nSingular;
    @JsonProperty
    private final String typeI18nPlural;
    private static final Function<DateTime, Date> toDate = AbstractInstant::toDate;

    @JsonCreator
    public PluginLicenseRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="valid") Boolean valid, @JsonProperty(value="error") LicenseError error, @JsonProperty(value="evaluation") Boolean evaluation, @JsonProperty(value="nearlyExpired") Boolean nearlyExpired, @JsonProperty(value="maximumNumberOfUsers") Integer maximumNumberOfUsers, @JsonProperty(value="maintenanceExpiryDate") Date maintenanceExpiryDate, @JsonProperty(value="maintenanceExpired") Boolean maintenanceExpired, @JsonProperty(value="licenseType") LicenseType licenseType, @JsonProperty(value="licenseTypeDescriptionKey") String licenseTypeDescriptionKey, @JsonProperty(value="creationDateString") String creationDateString, @JsonProperty(value="expiryDate") Date expiryDate, @JsonProperty(value="expiryDateString") String expiryDateString, @JsonProperty(value="rawLicense") String rawLicense, @JsonProperty(value="renewable") Boolean renewable, @JsonProperty(value="maintenanceExpiryDateString") String maintenanceExpiryDateString, @JsonProperty(value="pluginSupportEntitlementNumber") String supportEntitlementNumber, @JsonProperty(value="organizationName") String organizationName, @JsonProperty(value="contactEmail") String contactEmail, @JsonProperty(value="enterprise") Boolean enterprise, @JsonProperty(value="dataCenter") Boolean dataCenter, @JsonProperty(value="subscription") Boolean subscription, @JsonProperty(value="active") Boolean active, @JsonProperty(value="autoRenewal") Boolean autoRenewal, @JsonProperty(value="currentRoleCount") Integer currentRoleCount, @JsonProperty(value="upgradable") Boolean upgradable, @JsonProperty(value="crossgradeable") Boolean crossgradeable, @JsonProperty(value="forged") Boolean forged, @JsonProperty(value="purchasePastServerCutoffDate") Boolean purchasePastServerCutoffDate, @JsonProperty(value="roleManagementUri") URI roleManagementUri, @JsonProperty(value="typeI18nSingular") String typeI18nSingular, @JsonProperty(value="typeI18nPlural") String typeI18nPlural) {
        super(valid, evaluation, maximumNumberOfUsers, licenseType != null ? licenseType.name() : null, creationDateString, expiryDate, expiryDateString, supportEntitlementNumber, organizationName, contactEmail, enterprise, dataCenter, subscription, active, autoRenewal);
        this.links = links == null ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.pluginKey = pluginKey;
        this.error = error;
        this.nearlyExpired = nearlyExpired == null ? false : nearlyExpired;
        this.maintenanceExpiryDate = maintenanceExpiryDate;
        this.maintenanceExpiryDateString = maintenanceExpiryDateString;
        this.maintenanceExpired = maintenanceExpired;
        this.rawLicense = rawLicense;
        this.renewable = renewable;
        this.currentRoleCount = currentRoleCount;
        this.upgradable = upgradable;
        this.crossgradeable = crossgradeable;
        this.forged = forged;
        this.purchasePastServerCutoffDate = purchasePastServerCutoffDate;
        this.roleManagementUri = roleManagementUri;
        this.typeI18nSingular = typeI18nSingular;
        this.typeI18nPlural = typeI18nPlural;
        this.licenseTypeDescriptionKey = licenseTypeDescriptionKey;
    }

    public PluginLicenseRepresentation(String pluginKey, Option<com.atlassian.upm.core.Plugin> plugin, UpmLinkBuilder linkBuilder) {
        this.links = linkBuilder.buildLinksForPluginLicense(pluginKey, plugin, Option.none(PluginLicense.class)).build();
        this.pluginKey = pluginKey;
        this.error = null;
        this.licenseTypeDescriptionKey = null;
        this.nearlyExpired = null;
        this.maintenanceExpiryDate = null;
        this.maintenanceExpired = null;
        this.rawLicense = null;
        this.renewable = null;
        this.maintenanceExpiryDateString = null;
        this.currentRoleCount = null;
        this.upgradable = null;
        this.crossgradeable = null;
        this.forged = null;
        this.purchasePastServerCutoffDate = null;
        this.roleManagementUri = null;
        this.typeI18nSingular = null;
        this.typeI18nPlural = null;
    }

    public PluginLicenseRepresentation(String pluginKey, Option<com.atlassian.upm.core.Plugin> plugin, PluginLicense pluginLicense, LicenseDateFormatter dateFormatter, UpmLinkBuilder linkBuilder, RoleBasedLicensingPluginService roleBasedService, I18nResolver i18nResolver, HostLicenseInformation hostLicenseInformation) {
        super(pluginLicense.isValid(), pluginLicense.isEvaluation(), pluginLicense.getEdition().getOrElse((Integer)null), pluginLicense.getLicenseType().name(), dateFormatter.formatDate(pluginLicense.getCreationDate()), (Date)pluginLicense.getExpiryDate().map(toDate::apply).getOrElse((Date)null), (String)pluginLicense.getExpiryDate().map(PluginLicenseRepresentation.formatDate(dateFormatter)::apply).getOrElse((String)null), pluginLicense.getSupportEntitlementNumber().getOrElse((String)null), pluginLicense.getOrganization().getName(), PluginLicenseRepresentation.getContactsEmail(pluginLicense.getContacts()), pluginLicense.isEnterprise(), pluginLicense.isDataCenter(), pluginLicense.isSubscription(), pluginLicense.isActive(), pluginLicense.isAutoRenewal());
        this.links = linkBuilder.buildLinksForPluginLicense(pluginKey, plugin, Option.some(pluginLicense)).build();
        this.pluginKey = pluginKey;
        this.error = pluginLicense.getError().getOrElse((LicenseError)null);
        this.licenseTypeDescriptionKey = pluginLicense.getLicenseTypeDescriptionKey();
        this.nearlyExpired = PluginLicenses.isNearlyExpired().test(pluginLicense);
        this.maintenanceExpiryDate = (Date)pluginLicense.getMaintenanceExpiryDate().map(toDate::apply).getOrElse((Date)null);
        this.rawLicense = pluginLicense.getRawLicense();
        this.maintenanceExpiryDateString = (String)pluginLicense.getMaintenanceExpiryDate().map(PluginLicenseRepresentation.formatDate(dateFormatter)::apply).getOrElse((String)null);
        this.maintenanceExpired = pluginLicense.isMaintenanceExpired();
        this.currentRoleCount = this.calculateRoleCount(plugin, pluginLicense);
        this.upgradable = PluginLicenses.isPluginUpgradable(Option.some(pluginLicense), Option.option(this.currentRoleCount));
        this.renewable = PluginLicenses.isPluginRenewable(Option.some(pluginLicense), Option.option(this.currentRoleCount));
        this.crossgradeable = PluginLicenses.isPluginCrossgradeable(plugin.map(com.atlassian.upm.core.Plugin::getPluginInformation), hostLicenseInformation, Option.some(pluginLicense));
        this.forged = pluginLicense.isForged();
        this.purchasePastServerCutoffDate = Optional.ofNullable(pluginLicense.getPurchaseDate()).map(pd -> pd.isAfter((ReadableInstant)PluginLicense.SERVER_LICENSE_CUTOFF_DATE)).orElse(false);
        Option<Plugin> plugPlugin = plugin.map(Plugins.toPlugPlugin);
        this.roleManagementUri = this.calculateRoleManagementUri(plugPlugin, pluginLicense, roleBasedService);
        this.typeI18nSingular = this.calculateTypeI18nSingular(i18nResolver, roleBasedService, plugPlugin, pluginLicense);
        this.typeI18nPlural = this.calculateTypeI18nPlural(i18nResolver, roleBasedService, plugPlugin, pluginLicense);
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public LicenseError getError() {
        return this.error;
    }

    public Boolean isNearlyExpired() {
        return this.nearlyExpired;
    }

    public Date getMaintenanceExpiryDate() {
        return this.maintenanceExpiryDate;
    }

    public String getMaintenanceExpiryDateString() {
        return this.maintenanceExpiryDateString;
    }

    public String getRawLicense() {
        return this.rawLicense;
    }

    public String getTypeI18nPlural() {
        return this.typeI18nPlural;
    }

    public String getTypeI18nSingular() {
        return this.typeI18nSingular;
    }

    public URI getRoleManagementUri() {
        return this.roleManagementUri;
    }

    public Integer getCurrentRoleCount() {
        return this.currentRoleCount;
    }

    private static String getContactsEmail(Iterable<Contact> contacts) {
        return StreamSupport.stream(contacts.spliterator(), false).map(Contact::getEmail).filter(email -> !email.isEmpty()).findFirst().orElse(null);
    }

    private static Function<DateTime, String> formatDate(LicenseDateFormatter dateFormatter) {
        return dateFormatter::formatDate;
    }

    private Integer calculateRoleCount(Option<com.atlassian.upm.core.Plugin> plugin, PluginLicense license) {
        Iterator<Integer> iterator;
        if (plugin.isDefined() && PluginLicensesInternal.isRoleBasedLicense(license) && (iterator = PluginLicensesInternal.getCurrentRoleCount(license).iterator()).hasNext()) {
            Integer cnt = iterator.next();
            return cnt;
        }
        return null;
    }

    private URI calculateRoleManagementUri(Option<Plugin> plugin, PluginLicense license, RoleBasedLicensingPluginService roleBasedService) {
        Iterator<PluginLicensingRole> iterator;
        if (PluginLicensesInternal.isRoleBasedLicense(license) && (iterator = roleBasedService.getLicensingRoleForPlugin(plugin).iterator()).hasNext()) {
            PluginLicensingRole role = iterator.next();
            return role.getManagementPage();
        }
        return null;
    }

    private String calculateTypeI18nSingular(I18nResolver i18nResolver, RoleBasedLicensingPluginService roleBasedService, Option<Plugin> plugin, PluginLicense license) {
        String property;
        String string = property = license.getEditionType().equals((Object)LicenseEditionType.REMOTE_AGENT_COUNT) ? "upm.plugin.license.remoteagent.singular" : "upm.plugin.license.user.singular";
        if (PluginLicensesInternal.isRoleBasedLicense(license)) {
            Iterator<String> iterator = roleBasedService.getSingularI18nKey(plugin).iterator();
            while (iterator.hasNext()) {
                String singularKey;
                property = singularKey = iterator.next();
            }
        }
        return i18nResolver.getText(property);
    }

    private String calculateTypeI18nPlural(I18nResolver i18nResolver, RoleBasedLicensingPluginService roleBasedService, Option<Plugin> plugin, PluginLicense license) {
        String property;
        String string = property = license.getEditionType().equals((Object)LicenseEditionType.REMOTE_AGENT_COUNT) ? "upm.plugin.license.remoteagent.plural" : "upm.plugin.license.user.plural";
        if (PluginLicensesInternal.isRoleBasedLicense(license)) {
            Iterator<String> iterator = roleBasedService.getPluralI18nKey(plugin).iterator();
            while (iterator.hasNext()) {
                String pluralKey;
                property = pluralKey = iterator.next();
            }
        }
        return i18nResolver.getText(property);
    }
}

