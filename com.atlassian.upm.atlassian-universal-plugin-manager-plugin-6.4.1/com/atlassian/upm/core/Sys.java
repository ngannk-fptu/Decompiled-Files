/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.SysCommon;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.test.rest.resources.MpacBaseUrlResource;
import com.atlassian.upm.core.test.rest.resources.SysResource;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Sys {
    public static final String UPM_CONNECT_DESCRIPTOR_ALLOW_EXTERNAL = "atlassian.upm.connect.descriptor.allowExternal";
    public static final String UPM_P2_USER_INSTALLED_OVERRIDE = "atlassian.upm.user.installed.p2.override";
    public static final String UPM_REQUIRED_PLUGINS_OVERRIDE = "atlassian.upm.required.plugins.override";
    public static final String UPM_REQUIRED_MODULES_OVERRIDE = "atlassian.upm.required.modules.override";
    public static final String UPM_LEGACY_USER_INSTALLED_OVERRIDE = "atlassian.upm.user.installed.override";
    public static final String UPM_PREINSTALLED_DISABLEABLE_OVERRIDE = "atlassian.upm.preinstalled.disableable.override";
    public static final String UPM_XSRF_TOKEN_DISABLE = "upm.xsrf.token.disable";
    public static final String ATLASSIAN_DEV_MODE = "atlassian.dev.mode";
    private static final List<String> ALWAYS_SYSTEM_PLUGIN_KEYS = Collections.unmodifiableList(Arrays.asList("com.atlassian.upm.plugin-license-storage-plugin", "com.atlassian.upm.upm-application-plugin", "com.atlassian.upm.role-based-licensing-plugin", "com.atlassian.labs.httpservice.bridge-0.6.2", "org.osgi.compendium-4.1.0", "rome.rome-1.0"));

    public static boolean isAnalyticsConfiguredToSendServerInformation() {
        return !Boolean.getBoolean("atlassian.upm.server.data.disable");
    }

    public static boolean isDevModeEnabled() {
        return SysResource.getIsDevMode().getOrElse(Boolean.getBoolean(ATLASSIAN_DEV_MODE) || Boolean.getBoolean("jira.dev.mode"));
    }

    public static boolean isUpmDebugModeEnabled() {
        return Boolean.getBoolean("atlassian.upm.debug");
    }

    public static boolean allowExternalDescriptors() {
        return Sys.isDevModeEnabled() || Boolean.getBoolean(UPM_CONNECT_DESCRIPTOR_ALLOW_EXTERNAL);
    }

    public static boolean isXsrfTokenDisabled() {
        return Boolean.getBoolean(UPM_XSRF_TOKEN_DISABLE);
    }

    public static String getMpacBaseUrl() {
        return MpacBaseUrlResource.getMpacBaseUrl() != null ? MpacBaseUrlResource.getMpacBaseUrl() : System.getProperty("mpac.baseurl", "https://marketplace.atlassian.com");
    }

    public static Option<Iterable<String>> getP2OverriddenUserInstalledPluginKeys() {
        return Option.none();
    }

    public static Option<Iterable<String>> getLegacyOverriddenUserInstalledPluginKeys() {
        return Option.none();
    }

    public static Iterable<String> getOverriddenRequiredPluginKeys() {
        return Stream.concat(ALWAYS_SYSTEM_PLUGIN_KEYS.stream(), SysCommon.getPluginKeysFromSysProp(UPM_REQUIRED_PLUGINS_OVERRIDE).getOrElse(Collections.emptyList()).stream()).collect(Collectors.toList());
    }

    public static Iterable<String> getOverriddenRequiredModuleKeys() {
        return SysCommon.getPluginKeysFromSysProp(UPM_REQUIRED_MODULES_OVERRIDE).getOrElse(Collections.emptyList());
    }

    public static boolean isIncompatiblePluginCheckEnabled() {
        return !Sys.isDevModeEnabled();
    }

    public static String getGoAtlassianBaseUrl() {
        return "http://go.atlassian.com";
    }

    public static String getMacBaseUrl() {
        String macBaseUrl = System.getProperty("mac.baseurl");
        return macBaseUrl != null ? macBaseUrl : "https://my.atlassian.com";
    }

    public static String getMacBillingUrl() {
        String billingUrl = System.getProperty("mac.billing.url");
        return billingUrl != null ? billingUrl : "/admin/rest/billing/1";
    }

    public static String getHamletBaseUrl() {
        String hamletBaseUrl = System.getProperty("hamlet.baseurl");
        if (hamletBaseUrl != null) {
            return hamletBaseUrl;
        }
        return Sys.isUsingStagingMac() ? "https://hamlet.stg.internal.atlassian.com" : "https://hamlet.atlassian.com";
    }

    public static String getAtlassianIdBaseUrl() {
        String apiUrl = System.getProperty("atlassian.id.baseurl");
        if (apiUrl != null) {
            return apiUrl;
        }
        return Sys.isUsingStagingMac() ? "https://id.stg.internal.atlassian.com" : "https://id.atlassian.com";
    }

    public static String getShoppingCartBaseUrl() {
        String cartUrl = System.getProperty("shopping.cart.url");
        if (cartUrl != null) {
            return cartUrl;
        }
        return Sys.isUsingStagingMac() ? "https://qa-wac.internal.atlassian.com/purchase" : "https://www.atlassian.com/purchase";
    }

    public static boolean isUsingStagingMac() {
        return "https://my.stg.intsys.atlassian.com".equals(Sys.getMacBaseUrl());
    }

    public static final URI resolveMarketplaceUri(URI relativeUri) {
        return relativeUri.isAbsolute() ? relativeUri : URI.create(Sys.getMpacBaseUrl()).resolve(relativeUri);
    }

    public static final Function<URI, URI> resolveMarketplaceUri() {
        return Sys::resolveMarketplaceUri;
    }
}

