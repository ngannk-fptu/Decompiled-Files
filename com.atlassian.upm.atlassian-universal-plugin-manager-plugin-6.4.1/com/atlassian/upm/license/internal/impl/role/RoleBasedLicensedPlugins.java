/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.LicensedPlugins;

public abstract class RoleBasedLicensedPlugins {
    public static final String PLUGIN_INFO_USES_ROLE_BASED_LICENSING_PARAM = "atlassian-licensing-role-based-enabled";
    public static final String PLUGIN_INFO_LICENSING_ROLE_KEY_PARAM = "atlassian-licensing-role-key";
    public static final String PLUGIN_INFO_LICENSING_NAME_KEY_PARAM = "atlassian-licensing-role-name-key";
    public static final String PLUGIN_INFO_LICENSING_DESCRIPTION_KEY_PARAM = "atlassian-licensing-role-description-key";
    public static final String PLUGIN_INFO_LICENSING_SINGULAR_KEY_PARAM = "atlassian-licensing-role-singular-key";
    public static final String PLUGIN_INFO_LICENSING_PLURAL_KEY_PARAM = "atlassian-licensing-role-plural-key";
    public static final String DEFAULT_NAME_KEY = "upm.plugin.license.role.name";
    public static final String DEFAULT_DESCRIPTION_KEY = "upm.plugin.license.role.description";
    public static final String DEFAULT_SINGULAR_KEY = "upm.plugin.license.role.singular";
    public static final String DEFAULT_PLURAL_KEY = "upm.plugin.license.role.plural";

    public static boolean usesRoleBasedLicensing(Plugin plugin) {
        return LicensedPlugins.hasLicensingEnabledParam(plugin) && RoleBasedLicensedPlugins.hasRoleBasedLicensingEnabledParam(plugin);
    }

    public static Option<RoleBasedPluginDescriptorMetadata> getRoleBasedLicensingMetadata(Plugin plugin) {
        if (!RoleBasedLicensedPlugins.usesRoleBasedLicensing(plugin)) {
            return Option.none(RoleBasedPluginDescriptorMetadata.class);
        }
        return Option.some(new RoleBasedPluginDescriptorMetadata(RoleBasedLicensedPlugins.getRoleKey(plugin), RoleBasedLicensedPlugins.getNameKey(plugin), RoleBasedLicensedPlugins.getDescriptionKey(plugin), RoleBasedLicensedPlugins.getSingularKey(plugin), RoleBasedLicensedPlugins.getPluralKey(plugin)));
    }

    public static boolean hasRoleBasedLicensingEnabledParam(Plugin plugin) {
        return PluginInfoUtils.getBooleanPluginInfoParam(plugin.getPluginInformation(), PLUGIN_INFO_USES_ROLE_BASED_LICENSING_PARAM);
    }

    private static String getRoleKey(Plugin plugin) {
        return PluginInfoUtils.getStringPluginInfoParam(plugin.getPluginInformation(), PLUGIN_INFO_LICENSING_ROLE_KEY_PARAM).getOrElse(RoleBasedLicensedPlugins.getDefaultRoleKey(plugin.getKey()));
    }

    private static String getNameKey(Plugin plugin) {
        return PluginInfoUtils.getStringPluginInfoParam(plugin.getPluginInformation(), PLUGIN_INFO_LICENSING_NAME_KEY_PARAM).getOrElse(DEFAULT_NAME_KEY);
    }

    private static String getDescriptionKey(Plugin plugin) {
        return PluginInfoUtils.getStringPluginInfoParam(plugin.getPluginInformation(), PLUGIN_INFO_LICENSING_DESCRIPTION_KEY_PARAM).getOrElse(DEFAULT_DESCRIPTION_KEY);
    }

    private static String getSingularKey(Plugin plugin) {
        return PluginInfoUtils.getStringPluginInfoParam(plugin.getPluginInformation(), PLUGIN_INFO_LICENSING_SINGULAR_KEY_PARAM).getOrElse(DEFAULT_SINGULAR_KEY);
    }

    private static String getPluralKey(Plugin plugin) {
        return PluginInfoUtils.getStringPluginInfoParam(plugin.getPluginInformation(), PLUGIN_INFO_LICENSING_PLURAL_KEY_PARAM).getOrElse(DEFAULT_PLURAL_KEY);
    }

    private static String getDefaultRoleKey(String pluginKey) {
        return "licensed-addon-users-" + pluginKey;
    }

    public static class RoleBasedPluginDescriptorMetadata {
        private final String roleKey;
        private final String nameKey;
        private final String descriptionKey;
        private final String singularKey;
        private final String pluralKey;

        private RoleBasedPluginDescriptorMetadata(String roleKey, String nameKey, String descriptionKey, String singularKey, String pluralKey) {
            this.roleKey = roleKey;
            this.nameKey = nameKey;
            this.descriptionKey = descriptionKey;
            this.singularKey = singularKey;
            this.pluralKey = pluralKey;
        }

        public String getRoleKey() {
            return this.roleKey;
        }

        public String getNameKey() {
            return this.nameKey;
        }

        public String getDescriptionKey() {
            return this.descriptionKey;
        }

        public String getSingularKey() {
            return this.singularKey;
        }

        public String getPluralKey() {
            return this.pluralKey;
        }
    }
}

