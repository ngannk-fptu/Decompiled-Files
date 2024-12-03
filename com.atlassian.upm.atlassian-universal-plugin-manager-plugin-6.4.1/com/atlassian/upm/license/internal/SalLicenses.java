/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.license.MultiProductLicenseDetails
 *  com.atlassian.sal.api.license.ProductLicense
 *  com.atlassian.sal.api.license.SingleProductLicenseDetailsView
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal;

import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.license.MultiProductLicenseDetails;
import com.atlassian.sal.api.license.ProductLicense;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import com.atlassian.upm.api.license.entity.Contact;
import com.atlassian.upm.api.license.entity.Partner;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.HostApplicationLicenses;
import com.atlassian.upm.license.internal.LicenseEntityFactory;
import com.atlassian.upm.license.internal.ProductLicenses;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.joda.time.DateTime;

public final class SalLicenses {
    private SalLicenses() {
    }

    public static boolean isEnterprise(MultiProductLicenseDetails lic) {
        return ProductLicenses.getBooleanValue(lic.getProperty("enterprise"));
    }

    public static boolean isAutoRenewal(MultiProductLicenseDetails lic, String productKey) {
        return ProductLicenses.getBooleanValue(SalLicenses.getNamespacedPropertyValue(lic, productKey, "AutoRenew"));
    }

    public static boolean isStarter(MultiProductLicenseDetails lic, String productKey) {
        return ProductLicenses.getBooleanValue(SalLicenses.getNamespacedPropertyValue(lic, productKey, "Starter"));
    }

    public static boolean isEvaluation(MultiProductLicenseDetails lic, String productKey) {
        return HostApplicationLicenses.isEvaluationInternal(lic.isEvaluationLicense(), SalLicenses.isSubscription(lic), SalLicenses.getTrialEndDate(lic, productKey));
    }

    public static boolean isSubscription(MultiProductLicenseDetails lic) {
        return ProductLicenses.getBooleanValue(lic.getProperty("Subscription"));
    }

    public static boolean isAtlassianStackLicense(MultiProductLicenseDetails lic) {
        return ProductLicenses.getBooleanValue(lic.getProperty("stack.active"));
    }

    public static Option<DateTime> getExpiryDate(MultiProductLicenseDetails lic) {
        return ProductLicenses.getDateTimeValue(lic.getLicenseExpiryDate());
    }

    public static Option<DateTime> getMaintenanceExpiryDate(MultiProductLicenseDetails lic) {
        return ProductLicenses.getDateTimeValue(lic.getMaintenanceExpiryDate());
    }

    public static DateTime getCreationDate(MultiProductLicenseDetails lic) {
        return SalLicenses.getRequiredDateTimeValue(lic, "CreationDate");
    }

    public static DateTime getPurchaseDate(MultiProductLicenseDetails lic) {
        return SalLicenses.getRequiredDateTimeValue(lic, "PurchaseDate");
    }

    private static DateTime getRequiredDateTimeValue(MultiProductLicenseDetails lic, String property) {
        Iterator<DateTime> iterator = ProductLicenses.getDateTimeValue(lic.getProperty(property)).iterator();
        if (iterator.hasNext()) {
            DateTime dt = iterator.next();
            return dt;
        }
        throw new IllegalArgumentException("License was missing a required property: " + property);
    }

    public static Option<SubscriptionPeriod> getSubscriptionPeriod(MultiProductLicenseDetails lic, String productKey) {
        for (Integer period : ProductLicenses.getIntegerValue(SalLicenses.getNamespacedPropertyValue(lic, productKey, "BillingPeriod"), Option.none(Integer.class))) {
            switch (period) {
                case 1: {
                    return Option.some(SubscriptionPeriod.MONTHLY);
                }
                case 12: {
                    return Option.some(SubscriptionPeriod.ANNUAL);
                }
            }
        }
        return Option.none();
    }

    public static Option<DateTime> getLastModified(MultiProductLicenseDetails license) {
        return ProductLicenses.getDateTimeValue(license.getProperty("LastModified"));
    }

    public static Option<Integer> getLicenseVersion(MultiProductLicenseDetails license) {
        return ProductLicenses.getIntegerValue(license.getProperty("licenseVersion"), Option.none(Integer.class));
    }

    public static Option<Partner> getPartner(MultiProductLicenseDetails license, LicenseEntityFactory factory) {
        return Option.option(license.getProperty("PartnerName")).map(name -> factory.getPartner((String)name));
    }

    public static List<Contact> getContacts(MultiProductLicenseDetails license, LicenseEntityFactory factory) {
        for (String name : Option.option(license.getProperty("ContactName"))) {
            Iterator<String> iterator = Option.option(license.getProperty("ContactEMail")).iterator();
            if (!iterator.hasNext()) continue;
            String email = iterator.next();
            return Collections.singletonList(factory.getContact(name, email));
        }
        return Collections.emptyList();
    }

    public static Option<Boolean> isActive(MultiProductLicenseDetails lic, String productKey) {
        String property = SalLicenses.getNamespacedPropertyValue(lic, productKey, "active");
        return property == null ? Option.none(Boolean.class) : Option.some(ProductLicenses.getBooleanValue(property));
    }

    public static Option<Integer> getEdition(SingleProductLicenseDetailsView lic, MultiProductLicenseDetails baseLic, String namespace) {
        return SalLicenses.getEditionInternal(lic.getNumberOfUsers(), baseLic, namespace);
    }

    public static Option<Integer> getEdition(ProductLicense lic, MultiProductLicenseDetails baseLic, String namespace) {
        return SalLicenses.getEditionInternal(lic.getNumberOfUsers(), baseLic, namespace);
    }

    private static Option<Integer> getEditionInternal(int numberOfUsers, MultiProductLicenseDetails baseLic, String namespace) {
        return ProductLicenses.getIntegerValue(SalLicenses.getNamespacedPropertyValue(baseLic, namespace, "NumberOfUsers"), Option.some(numberOfUsers)).flatMap(val -> val == -1 ? Option.none(Integer.class) : Option.some(val));
    }

    public static Option<DateTime> getSubscriptionEndDate(MultiProductLicenseDetails baseLic, String namespace) {
        return HostApplicationLicenses.getSubscriptionEndDate(SalLicenses.isSubscription(baseLic), SalLicenses.getTrialEndDate(baseLic, namespace), SalLicenses.getPurchaseExpiryDate(baseLic));
    }

    private static Option<DateTime> getTrialEndDate(MultiProductLicenseDetails baseLic, String namespace) {
        return ProductLicenses.getDateTimeValue(SalLicenses.getNamespacedPropertyValue(baseLic, namespace, "TrialEndDate"));
    }

    private static Option<DateTime> getPurchaseExpiryDate(MultiProductLicenseDetails baseLic) {
        return ProductLicenses.getDateTimeValue(baseLic.getProperty("PurchaseExpiryDate"));
    }

    public static Option<Option<Integer>> getLicensedRoleCount(MultiProductLicenseDetails baseLic, String namespace) {
        return HostApplicationLicenses.getLicensedRoleCount(Option.option(SalLicenses.getNamespacedPropertyValue(baseLic, namespace, "numRoleCount")));
    }

    public static HostApplicationLicenses.LicenseEditionAndRoleCount getEditionAndRoleCountForEmbeddedLicense(MultiProductLicenseDetails baseLic, String namespace, Option<Plugin> plugin, RoleBasedLicensingPluginService roleBasedService, ApplicationProperties applicationProperties) {
        return HostApplicationLicenses.getEditionAndRoleCountForEmbeddedLicense(SalLicenses.getEditionInternal(baseLic.getProductLicense(namespace).getNumberOfUsers(), baseLic, namespace), SalLicenses.getMaximumNumberOfRemoteAgents(baseLic, namespace), SalLicenses.getLicensedRoleCount(baseLic, namespace), SalLicenses.isEvaluation(baseLic, namespace), plugin, roleBasedService, applicationProperties);
    }

    public static Option<Integer> getMaximumNumberOfRemoteAgents(MultiProductLicenseDetails baseLic, String namespace) {
        if (!SalLicenses.hasMaximumNumberOfRemoteAgentsInternalProperty(baseLic, namespace)) {
            return SalLicenses.getEditionInternal(baseLic.getProductLicense(namespace).getNumberOfUsers(), baseLic, namespace);
        }
        return SalLicenses.getMaximumNumberOfRemoteAgentsInternalProperty(baseLic, namespace);
    }

    private static Option<Integer> getMaximumNumberOfRemoteAgentsInternalProperty(MultiProductLicenseDetails baseLic, String namespace) {
        return ProductLicenses.getIntegerValue(baseLic.getProperty("NumberOfBambooRemoteAgents"), Option.some(0));
    }

    private static boolean hasMaximumNumberOfRemoteAgentsInternalProperty(MultiProductLicenseDetails baseLic, String namespace) {
        return baseLic.getProperty("NumberOfBambooRemoteAgents") != null;
    }

    public static String getActivePropertyKey(String namespace) {
        return SalLicenses.getNamespacedPropertyKey(namespace, "active");
    }

    private static String getNamespacedPropertyKey(String namespace, String property) {
        return namespace + "." + property;
    }

    private static String getNamespacedPropertyValue(MultiProductLicenseDetails lic, String namespace, String property) {
        return lic.getProperty(SalLicenses.getNamespacedPropertyKey(namespace, property));
    }
}

