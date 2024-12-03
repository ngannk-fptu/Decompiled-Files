/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Predicate
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal;

import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.api.license.entity.LicenseEditionType;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRole;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensedPlugins;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginMetadata;
import com.google.common.base.Predicate;
import java.util.Objects;
import org.joda.time.DateTime;

public final class HostApplicationLicenses {
    private HostApplicationLicenses() {
    }

    static Option<DateTime> getSubscriptionEndDate(boolean subscription, Option<DateTime> trialEndDate, Option<DateTime> purchaseExpiryDate) {
        if (subscription) {
            return trialEndDate.isDefined() ? trialEndDate : purchaseExpiryDate;
        }
        return Option.none(DateTime.class);
    }

    static boolean isEvaluationInternal(boolean eval, boolean subscription, Option<DateTime> trialEndDate) {
        return eval || subscription && trialEndDate.isDefined() && trialEndDate.get().isAfterNow();
    }

    static Option<Option<Integer>> getLicensedRoleCount(Option<String> roleCount) {
        return roleCount.map(prop -> {
            int value = Integer.parseInt(prop);
            return value == -1 ? Option.none(Integer.class) : Option.some(value);
        });
    }

    static LicenseEditionAndRoleCount getEditionAndRoleCountForEmbeddedLicense(Option<Integer> userLimit, Option<Integer> remoteAgentLimit, Option<Option<Integer>> licensedRoleCount, boolean eval, Option<Plugin> plugin, RoleBasedLicensingPluginService roleBasedService, ApplicationProperties applicationProperties) {
        Option<RoleBasedPluginMetadata> rbpMeta;
        boolean roleBasedLicense;
        Option<Integer> edition;
        LicenseEditionType editionType;
        boolean roleBasedPlugin = plugin.exists((Predicate<Plugin>)((Predicate)RoleBasedLicensedPlugins::hasRoleBasedLicensingEnabledParam));
        if ("bamboo".equalsIgnoreCase(applicationProperties.getDisplayName())) {
            editionType = LicenseEditionType.REMOTE_AGENT_COUNT;
            edition = remoteAgentLimit;
        } else {
            editionType = LicenseEditionType.USER_COUNT;
            edition = userLimit;
        }
        boolean bl = roleBasedLicense = eval && licensedRoleCount.isDefined() || edition.exists((Predicate<Integer>)((Predicate)integer -> Objects.equals(integer, 0)));
        if (roleBasedPlugin && roleBasedLicense) {
            editionType = LicenseEditionType.ROLE_COUNT;
            edition = licensedRoleCount.getOrElse(Option.none(Integer.class));
            rbpMeta = Option.some(new RoleBasedPluginMetadata(edition, roleBasedService.getLicensingRoleForPlugin(plugin).map(PluginLicensingRole::getRoleCount)));
        } else {
            rbpMeta = Option.none();
        }
        return new LicenseEditionAndRoleCount(editionType, edition, rbpMeta);
    }

    public static class LicenseEditionAndRoleCount {
        public final LicenseEditionType editionType;
        public final Option<Integer> edition;
        public final Option<RoleBasedPluginMetadata> rbpMeta;

        public LicenseEditionAndRoleCount(LicenseEditionType editionType, Option<Integer> edition, Option<RoleBasedPluginMetadata> rbpMeta) {
            this.editionType = editionType;
            this.edition = edition;
            this.rbpMeta = rbpMeta;
        }
    }
}

