/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.license.MultiProductLicenseDetails
 *  com.atlassian.sal.api.license.ProductLicense
 *  com.atlassian.sal.api.license.SingleProductLicenseDetailsView
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.extras.api.Product;
import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.license.MultiProductLicenseDetails;
import com.atlassian.sal.api.license.ProductLicense;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.license.internal.HostApplicationEmbeddedAddonLicense;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostApplicationLicenses;
import com.atlassian.upm.license.internal.LicenseEntityFactory;
import com.atlassian.upm.license.internal.ProductLicenses;
import com.atlassian.upm.license.internal.SalLicenses;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class HostApplicationLicenseFactoryImpl
implements HostApplicationLicenseFactory {
    private final LicenseEntityFactory factory;
    private final HostApplicationDescriptor hostApplicationDescriptor;
    private final RoleBasedLicensingPluginService roleBasedService;
    private final ApplicationProperties applicationProperties;
    private final UpmPluginAccessor accessor;

    public HostApplicationLicenseFactoryImpl(LicenseEntityFactory factory, HostApplicationDescriptor hostApplicationDescriptor, RoleBasedLicensingPluginService roleBasedService, ApplicationProperties applicationProperties, UpmPluginAccessor accessor) {
        this.factory = Objects.requireNonNull(factory, "factory");
        this.hostApplicationDescriptor = Objects.requireNonNull(hostApplicationDescriptor, "hostApplicationDescriptor");
        this.roleBasedService = Objects.requireNonNull(roleBasedService, "roleBasedService");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.accessor = Objects.requireNonNull(accessor, "accessor");
    }

    @Override
    public HostApplicationLicense getHostLicense(com.atlassian.extras.api.ProductLicense productLicense, String rawLicense) {
        Product product = productLicense.getProduct();
        Option<SubscriptionPeriod> subscriptionPeriod = Option.none();
        for (boolean monthly : ProductLicenses.isMonthlyOnDemandSubscription(productLicense)) {
            if (!monthly) continue;
            subscriptionPeriod = Option.some(SubscriptionPeriod.MONTHLY);
        }
        for (boolean annual : ProductLicenses.isAnnualOnDemandSubscription(productLicense)) {
            if (!annual) continue;
            subscriptionPeriod = Option.some(SubscriptionPeriod.ANNUAL);
        }
        List<HostApplicationEmbeddedAddonLicense> embeddedAddonLicenses = ProductLicenses.getEmbeddedPluginKeys(productLicense, this.applicationProperties.getDisplayName().toLowerCase()).stream().map(pluginKey -> this.getEmbeddedAddonLicense(productLicense, new Product(pluginKey, pluginKey, true), rawLicense, this.accessor.getPlugin((String)pluginKey))).collect(Collectors.toList());
        Option<String> sen = this.ifNotBlank(Option.option(productLicense.getSupportEntitlementNumber()));
        Option<Integer> userOrEditionCount = product.equals((Object)Product.BAMBOO) ? ProductLicenses.getMaximumNumberOfRemoteAgents(productLicense, product) : ProductLicenses.getMaximumNumberOfUsers(productLicense, product);
        return new HostApplicationLicense(productLicense.isEvaluation(), ProductLicenses.isDataCenter(productLicense), ProductLicenses.isLegacyEnterprise(productLicense), ProductLicenses.isRoleBased(productLicense, product), ProductLicenses.isStarter(productLicense, product).getOrElse(false), ProductLicenses.isAutoRenewal(productLicense, product), product.getNamespace(), product.getNamespace(), product.getName(), LicenseType.valueOf(productLicense.getLicenseType().name()), productLicense.getServerId(), rawLicense, new DateTime((Object)productLicense.getPurchaseDate()), sen, subscriptionPeriod, userOrEditionCount, embeddedAddonLicenses, ProductLicenses.getLastModified(productLicense), ProductLicenses.getExpiryDate(productLicense), ProductLicenses.isAtlassianStackLicense(productLicense));
    }

    @Override
    public HostApplicationLicense getHostLicense(SingleProductLicenseDetailsView lic, MultiProductLicenseDetails baseLic, String productKey, String rawLicense) {
        ArrayList<HostApplicationEmbeddedAddonLicense> embeddedAddonLicenses = new ArrayList<HostApplicationEmbeddedAddonLicense>();
        for (ProductLicense embeddedLicense : baseLic.getEmbeddedLicenses()) {
            String embeddedKey = embeddedLicense.getProductKey();
            if (!ProductLicenses.isSpecificPluginProduct(Product.fromNamespace((String)embeddedKey), this.applicationProperties.getPlatformId())) continue;
            embeddedAddonLicenses.add(this.getEmbeddedAddonLicense(embeddedLicense, baseLic, rawLicense, this.accessor.getPlugin(ProductLicenses.getPluginKeyFromProductNamespace(embeddedKey))));
        }
        Option<Integer> edition = this.withNamespace(productKey, ns -> SalLicenses.getEdition(lic, baseLic, ns));
        Option<SubscriptionPeriod> subscriptionPeriod = this.withNamespace(productKey, ns -> SalLicenses.getSubscriptionPeriod(baseLic, ns));
        boolean eval = (Boolean)this.withNamespace(productKey, ns -> {
            boolean val = SalLicenses.isEvaluation(baseLic, ns);
            return val ? Option.some(val) : Option.none(Boolean.class);
        }).getOrElse(false);
        boolean autoRenewal = (Boolean)this.withNamespace(productKey, ns -> {
            boolean val = SalLicenses.isAutoRenewal(baseLic, ns);
            return val ? Option.some(val) : Option.none(Boolean.class);
        }).getOrElse(false);
        boolean roleBased = this.withNamespace(productKey, ns -> Option.option(lic.getProperty(ProductLicenses.getNumRoleCountPropertyKey(new Product(ns, ns, true))))).isDefined();
        boolean starter = (Boolean)this.withNamespace(productKey, ns -> {
            boolean val = SalLicenses.isStarter(baseLic, ns);
            return val ? Option.some(val) : Option.none(Boolean.class);
        }).getOrElse(false);
        String encodedProductKey = (String)this.withNamespace(productKey, ns -> Option.option(lic.getProperty(SalLicenses.getActivePropertyKey(ns))).isDefined() ? Option.some(ns) : Option.none(String.class)).getOrElse(productKey);
        Option<String> sen = this.ifNotBlank(Option.option(lic.getSupportEntitlementNumber()));
        return new HostApplicationLicense(eval, lic.isDataCenter(), SalLicenses.isEnterprise(baseLic), roleBased, starter, autoRenewal, productKey, encodedProductKey, lic.getProductDisplayName(), LicenseType.valueOf(lic.getLicenseTypeName()), lic.getServerId(), rawLicense, SalLicenses.getPurchaseDate(baseLic), sen, subscriptionPeriod, edition, Collections.unmodifiableList(embeddedAddonLicenses), SalLicenses.getLastModified(baseLic), SalLicenses.getExpiryDate(baseLic), SalLicenses.isAtlassianStackLicense(baseLic));
    }

    private <T> Option<T> withNamespace(String productKey, Function<String, Option<T>> f) {
        List<String> namespaces = Arrays.asList(productKey, this.applicationProperties.getPlatformId() + ".product." + productKey, this.applicationProperties.getPlatformId());
        for (String namespace : namespaces) {
            Option<T> t = f.apply(namespace);
            if (!t.isDefined()) continue;
            return t;
        }
        return Option.none();
    }

    private HostApplicationEmbeddedAddonLicense getEmbeddedAddonLicense(ProductLicense lic, MultiProductLicenseDetails baseLic, String rawLicense, Option<Plugin> plugin) {
        String productKey = lic.getProductKey();
        Option<SubscriptionPeriod> subscriptionPeriod = this.withNamespace(productKey, ns -> SalLicenses.getSubscriptionPeriod(baseLic, ns));
        HostApplicationLicenses.LicenseEditionAndRoleCount editionInfo = SalLicenses.getEditionAndRoleCountForEmbeddedLicense(baseLic, productKey, plugin, this.roleBasedService, this.applicationProperties);
        Option<String> sen = this.ifNotBlank(Option.option(baseLic.getSupportEntitlementNumber()));
        return new HostApplicationEmbeddedAddonLicense(SalLicenses.isEvaluation(baseLic, productKey), baseLic.isDataCenter(), SalLicenses.isEnterprise(baseLic), SalLicenses.isAutoRenewal(baseLic, productKey), SalLicenses.isActive(baseLic, productKey).getOrElse(false), SalLicenses.isSubscription(baseLic), LicenseType.valueOf(baseLic.getLicenseTypeName().toUpperCase()), ProductLicenses.getPluginKeyFromProductNamespace(lic.getProductKey()), baseLic.getServerId(), rawLicense, baseLic.getDescription(), sen, subscriptionPeriod, editionInfo.edition, editionInfo.editionType, editionInfo.rbpMeta, SalLicenses.getLastModified(baseLic), SalLicenses.getSubscriptionEndDate(baseLic, lic.getProductKey()), SalLicenses.getMaintenanceExpiryDate(baseLic), SalLicenses.getExpiryDate(baseLic), SalLicenses.getPurchaseDate(baseLic), SalLicenses.getCreationDate(baseLic), SalLicenses.getLicenseVersion(baseLic), SalLicenses.getPartner(baseLic, this.factory), SalLicenses.getContacts(baseLic, this.factory), this.factory.getOrganization(baseLic.getOrganisationName()), SalLicenses.isAtlassianStackLicense(baseLic));
    }

    @Override
    public HostApplicationEmbeddedAddonLicense getEmbeddedAddonLicense(com.atlassian.extras.api.ProductLicense lic, Product product, String rawLicense, Option<Plugin> plugin) {
        Option<SubscriptionPeriod> subscriptionPeriod = Option.none();
        if (lic.isSubscription()) {
            for (boolean monthly : ProductLicenses.isMonthlyOnDemandSubscription(lic)) {
                if (!monthly) continue;
                subscriptionPeriod = Option.some(SubscriptionPeriod.MONTHLY);
            }
            for (boolean annual : ProductLicenses.isAnnualOnDemandSubscription(lic)) {
                if (!annual) continue;
                subscriptionPeriod = Option.some(SubscriptionPeriod.ANNUAL);
            }
        }
        HostApplicationLicenses.LicenseEditionAndRoleCount editionInfo = ProductLicenses.getEditionAndRoleCountForEmbeddedLicense(lic, product, plugin, this.roleBasedService, this.applicationProperties);
        Option<String> sen = this.ifNotBlank(Option.option(lic.getSupportEntitlementNumber()));
        return new HostApplicationEmbeddedAddonLicense(ProductLicenses.isEvaluation(lic, product), ProductLicenses.isDataCenter(lic), ProductLicenses.isLegacyEnterprise(lic), ProductLicenses.isAutoRenewal(lic, product), ProductLicenses.isActive(lic, product).getOrElse(false), lic.isSubscription(), LicenseType.valueOf(lic.getLicenseType().name()), ProductLicenses.getPluginKeyFromProductNamespace(product.getNamespace()), lic.getServerId(), rawLicense, lic.getDescription(), sen, subscriptionPeriod, editionInfo.edition, editionInfo.editionType, editionInfo.rbpMeta, ProductLicenses.getLastModified(lic), ProductLicenses.getSubscriptionEndDate(lic, product), ProductLicenses.getMaintenanceExpiryDate(lic), ProductLicenses.getExpiryDate(lic), new DateTime((Object)lic.getPurchaseDate()), new DateTime((Object)lic.getCreationDate()), ProductLicenses.getLicenseVersion(lic), ProductLicenses.getPartner(lic, this.factory), ProductLicenses.getContacts(lic, this.factory), this.factory.getOrganization(lic.getOrganisation()), ProductLicenses.isAtlassianStackLicense(lic));
    }

    private Option<String> ifNotBlank(Option<String> str) {
        for (String s : str) {
            if (StringUtils.isBlank((CharSequence)s)) continue;
            return Option.some(s);
        }
        return Option.none(String.class);
    }
}

