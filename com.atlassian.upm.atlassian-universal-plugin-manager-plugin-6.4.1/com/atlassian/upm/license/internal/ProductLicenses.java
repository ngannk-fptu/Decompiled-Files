/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.bamboo.BambooLicense
 *  com.atlassian.extras.common.DateEditor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.license.internal;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.api.bamboo.BambooLicense;
import com.atlassian.extras.common.DateEditor;
import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.api.license.entity.Contact;
import com.atlassian.upm.api.license.entity.Partner;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.HostApplicationLicenses;
import com.atlassian.upm.license.internal.LicenseEntityFactory;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProductLicenses {
    private static final Logger log = LoggerFactory.getLogger(ProductLicenses.class);
    public static final String ON_DEMAND = "ondemand";
    public static final String LEGACY_ENTERPRISE = "enterprise";
    public static final String PURCHASE_EXPIRY_DATE = "PurchaseExpiryDate";
    public static final String NUM_HOSTED_USERS = "numHostedUsers";
    public static final String NUMBER_OF_USERS = "NumberOfUsers";
    public static final String AUTO_RENEW = "AutoRenew";
    public static final String STARTER = "Starter";
    public static final String TRIAL_END_DATE = "TrialEndDate";
    public static final String LAST_MODIFIED = "LastModified";
    public static final String BILLING_PERIOD = "BillingPeriod";
    public static final String NUM_ROLE_COUNT = "numRoleCount";
    public static final String ATLASSIAN_STACK = "stack.active";
    private static final String DATA_CENTER = "DataCenter";

    private ProductLicenses() {
    }

    public static Option<Integer> getMaximumNumberOfUsers(ProductLicense license, Product product) {
        return ProductLicenses.getIntegerValue(ProductLicenses.getNamespacedPropertyValue(license, product, NUMBER_OF_USERS), ProductLicenses.getIntegerValue(license.getMaximumNumberOfUsers()));
    }

    public static Option<Integer> getMaximumNumberOfRemoteAgents(ProductLicense license, Product product) {
        if (!ProductLicenses.hasMaximumNumberOfRemoteAgentsInternalProperty(license, product)) {
            return ProductLicenses.getMaximumNumberOfUsers(license, product);
        }
        return ProductLicenses.getMaximumNumberOfRemoteAgentsInternalProperty(license, product);
    }

    private static Option<Integer> getMaximumNumberOfRemoteAgentsInternalProperty(ProductLicense license, Product product) {
        if (license instanceof BambooLicense) {
            return ProductLicenses.getIntegerValue(((BambooLicense)license).getMaximumNumberOfRemoteAgents());
        }
        return ProductLicenses.getIntegerValue(license.getProperty("NumberOfBambooRemoteAgents"), Option.some(0));
    }

    private static boolean hasMaximumNumberOfRemoteAgentsInternalProperty(ProductLicense license, Product product) {
        if (license instanceof BambooLicense) {
            return true;
        }
        return license.getProperty("NumberOfBambooRemoteAgents") != null;
    }

    public static Option<DateTime> getExpiryDate(ProductLicense license) {
        return ProductLicenses.getDateTimeValue(license.getExpiryDate());
    }

    public static Option<DateTime> getMaintenanceExpiryDate(ProductLicense license) {
        return ProductLicenses.getDateTimeValue(license.getMaintenanceExpiryDate());
    }

    public static Option<DateTime> getSubscriptionEndDate(ProductLicense license, Product product) {
        return HostApplicationLicenses.getSubscriptionEndDate(license.isSubscription(), ProductLicenses.getTrialEndDate(license, product), ProductLicenses.getPurchaseExpiryDate(license));
    }

    private static Option<DateTime> getTrialEndDate(ProductLicense license, Product product) {
        return ProductLicenses.getDateTimeValue(ProductLicenses.getNamespacedPropertyValue(license, product, TRIAL_END_DATE));
    }

    private static Option<DateTime> getPurchaseExpiryDate(ProductLicense license) {
        return ProductLicenses.getDateTimeValue(license.getProperty(PURCHASE_EXPIRY_DATE));
    }

    public static boolean isEvaluation(ProductLicense license, Product product) {
        return HostApplicationLicenses.isEvaluationInternal(license.isEvaluation(), license.isSubscription(), ProductLicenses.getTrialEndDate(license, product));
    }

    public static Option<Boolean> isActive(ProductLicense license, Product product) {
        String property = ProductLicenses.getNamespacedPropertyValue(license, product, "active");
        return property == null ? Option.none(Boolean.class) : Option.some(ProductLicenses.getBooleanValue(property));
    }

    public static boolean isAutoRenewal(ProductLicense license, Product product) {
        return ProductLicenses.getBooleanValue(ProductLicenses.getNamespacedPropertyValue(license, product, AUTO_RENEW));
    }

    public static Option<Boolean> isStarter(ProductLicense license, Product product) {
        String property = ProductLicenses.getNamespacedPropertyValue(license, product, STARTER);
        return property == null ? Option.none(Boolean.class) : Option.some(ProductLicenses.getBooleanValue(property));
    }

    public static boolean isLegacyEnterprise(ProductLicense license) {
        boolean enterpriseFromProperty = ProductLicenses.getBooleanValue(license.getProperty(LEGACY_ENTERPRISE));
        boolean enterpriseFromJiraDescription = license.getDescription() != null && license.getDescription().contains("for JIRA Enterprise");
        return (enterpriseFromProperty || enterpriseFromJiraDescription) && !ProductLicenses.isDataCenter(license);
    }

    public static boolean isDataCenter(ProductLicense license) {
        return ProductLicenses.getBooleanValue(ProductLicenses.getNamespacedPropertyValue(license, license.getProduct(), DATA_CENTER));
    }

    public static boolean isAtlassianStackLicense(ProductLicense license) {
        return ProductLicenses.getBooleanValue(license.getProperty(ATLASSIAN_STACK));
    }

    public static Option<Option<Integer>> getLicensedRoleCount(ProductLicense license, Product product) {
        return HostApplicationLicenses.getLicensedRoleCount(Option.option(ProductLicenses.getNamespacedPropertyValue(license, product, NUM_ROLE_COUNT)));
    }

    public static boolean isRoleBased(ProductLicense license, Product product) {
        return ProductLicenses.getLicensedRoleCount(license, product).isDefined();
    }

    public static Option<DateTime> getLastModified(ProductLicense license) {
        return ProductLicenses.getDateTimeValue(license.getProperty(LAST_MODIFIED));
    }

    public static Option<SubscriptionPeriod> getSubscriptionPeriod(ProductLicense license, Product product) {
        return Option.none(SubscriptionPeriod.class);
    }

    public static Option<Boolean> isAnnualOnDemandSubscription(ProductLicense license, Product product) {
        return ProductLicenses.getSubscriptionPeriod(license, product).map(period -> period == SubscriptionPeriod.ANNUAL);
    }

    public static Option<Boolean> isAnnualOnDemandSubscription(ProductLicense license) {
        return ProductLicenses.isAnnualOnDemandSubscription(license, license.getProduct());
    }

    public static Option<Boolean> isMonthlyOnDemandSubscription(ProductLicense license) {
        return ProductLicenses.getSubscriptionPeriod(license, license.getProduct()).map(period -> period == SubscriptionPeriod.MONTHLY);
    }

    private static String getNamespacedPropertyKey(Product product, String property) {
        return product.getNamespace() + "." + property;
    }

    private static String getNamespacedPropertyValue(ProductLicense license, Product product, String property) {
        String propValue = license.getProperty(ProductLicenses.getNamespacedPropertyKey(product, property));
        if (propValue == null && product.isPlugin()) {
            propValue = license.getProperty(ProductLicenses.getNamespacedPropertyKey(Product.ALL_PLUGINS, property));
        }
        return propValue;
    }

    static Option<Integer> getIntegerValue(String valueString, Option<Integer> defaultValue) {
        try {
            if (valueString != null) {
                return ProductLicenses.getIntegerValue(Integer.parseInt(valueString));
            }
        }
        catch (NumberFormatException e) {
            log.warn("Unexpected non-numeric property: " + valueString);
        }
        return defaultValue;
    }

    static Option<Integer> getIntegerValue(int value) {
        return value == -1 ? Option.none(Integer.class) : Option.some(value);
    }

    static Option<DateTime> getDateTimeValue(String valueString) {
        Date date;
        if (valueString != null && valueString.length() != 0 && (date = DateEditor.getDate((String)valueString)) != null) {
            return Option.some(new DateTime((Object)date));
        }
        return Option.none(DateTime.class);
    }

    static Option<DateTime> getDateTimeValue(Date date) {
        return date == null ? Option.none(DateTime.class) : Option.some(new DateTime((Object)date));
    }

    static boolean getBooleanValue(String booleanValue) {
        return (Boolean)Option.option(booleanValue).map(Boolean::parseBoolean).getOrElse(false);
    }

    public static String getDataCenterPropertyKey(Product product) {
        return ProductLicenses.getNamespacedPropertyKey(product, DATA_CENTER);
    }

    public static String getNumHostedUsersPropertyKey(Product product) {
        return ProductLicenses.getNamespacedPropertyKey(product, NUM_HOSTED_USERS);
    }

    public static String getAutoRenewPropertyKey(Product product) {
        return ProductLicenses.getNamespacedPropertyKey(product, AUTO_RENEW);
    }

    public static String getBillingPeriodPropertyKey(Product product) {
        return ProductLicenses.getNamespacedPropertyKey(product, BILLING_PERIOD);
    }

    public static String getNumRoleCountPropertyKey(Product product) {
        return ProductLicenses.getNamespacedPropertyKey(product, NUM_ROLE_COUNT);
    }

    public static String getNumBambooRemoteAgentCountPropertyKey(Product product) {
        return ProductLicenses.getNamespacedPropertyKey(product, "NumberOfBambooRemoteAgents");
    }

    public static String getActivePropertyKey(Product product) {
        return ProductLicenses.getNamespacedPropertyKey(product, "active");
    }

    public static Option<Integer> getLicenseVersion(ProductLicense license) {
        int version = license.getLicenseVersion();
        return version == 0 ? Option.none(Integer.class) : Option.some(version);
    }

    public static Option<Partner> getPartner(ProductLicense license, LicenseEntityFactory factory) {
        return Option.option(license.getPartner()).map(factory::getPartner);
    }

    public static List<Contact> getContacts(ProductLicense license, LicenseEntityFactory factory) {
        return Collections.unmodifiableList(license.getContacts().stream().map(factory::getContact).collect(Collectors.toList()));
    }

    public static Option<String> getSupportEntitlementNumber(ProductLicense license) {
        return Option.option(license.getSupportEntitlementNumber()).flatMap(sen -> StringUtils.isBlank((CharSequence)sen) ? Option.none(String.class) : Option.some(sen));
    }

    public static HostApplicationLicenses.LicenseEditionAndRoleCount getEditionAndRoleCountForEmbeddedLicense(ProductLicense license, Product product, Option<Plugin> plugin, RoleBasedLicensingPluginService roleBasedService, ApplicationProperties applicationProperties) {
        return HostApplicationLicenses.getEditionAndRoleCountForEmbeddedLicense(ProductLicenses.getMaximumNumberOfUsers(license, product), ProductLicenses.getMaximumNumberOfRemoteAgents(license, product), ProductLicenses.getLicensedRoleCount(license, product), ProductLicenses.isEvaluation(license, product), plugin, roleBasedService, applicationProperties);
    }

    public static List<String> getEmbeddedPluginKeys(ProductLicense hostLicense, String platformId) {
        List pluginProducts = Iterables.toStream(hostLicense.getProducts()).filter(product -> ProductLicenses.isSpecificPluginProduct(product, platformId)).collect(Collectors.toList());
        return Collections.unmodifiableList(pluginProducts.stream().map(product -> ProductLicenses.getPluginKeyFromProductNamespace(product.getNamespace())).collect(Collectors.toList()));
    }

    public static boolean isSpecificPluginProduct(Product p, String platformId) {
        return p.isPlugin() && !p.getNamespace().equals(Product.ALL_PLUGINS.getNamespace()) && !p.getNamespace().startsWith(platformId + ".product.");
    }

    public static String getPluginKeyFromProductNamespace(String namespace) {
        if (Product.BONFIRE.getNamespace().equals(namespace)) {
            return "com.atlassian.bonfire.plugin";
        }
        if (Product.TEAM_CALENDARS.getNamespace().equals(namespace)) {
            return "com.atlassian.confluence.extra.team-calendars";
        }
        if (Product.GREENHOPPER.getNamespace().equals(namespace)) {
            return "com.pyxis.greenhopper.jira";
        }
        if (Product.SHAREPOINT_PLUGIN.getNamespace().equals(namespace)) {
            return "com.atlassian.confluence.extra.sharepoint";
        }
        if (Product.VSS_PLUGIN.getNamespace().equals(namespace)) {
            return "com.atlassian.jira.plugin.ext.vss";
        }
        return namespace;
    }
}

