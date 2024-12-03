/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.google.common.base.Joiner
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.util;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.RequiresRestart;
import com.atlassian.plugin.util.ModuleRestricts;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginUtils {
    private static final Logger logger = LoggerFactory.getLogger(PluginUtils.class);
    public static final String ATLASSIAN_DEV_MODE = "atlassian.dev.mode";
    public static final String ATLASSIAN_PLUGINS_ENABLE_WAIT = "atlassian.plugins.enable.wait";
    public static final String DEFAULT_ATLASSIAN_PLUGINS_ENABLE_WAIT_SECONDS = "300";
    public static final String WEBRESOURCE_FILE_CACHE_SIZE = new String("atlassian.webresource.file.cache.size");
    public static final String WEBRESOURCE_DISABLE_FILE_CACHE = new String("atlassian.webresource.file.cache.disable");

    public static boolean doesPluginRequireRestart(Plugin plugin) {
        if (PluginUtils.isAtlassianDevMode()) {
            return false;
        }
        for (ModuleDescriptor descriptor : plugin.getModuleDescriptors()) {
            if (descriptor.getClass().getAnnotation(RequiresRestart.class) == null) continue;
            return true;
        }
        return false;
    }

    public static Set<String> getPluginModulesThatRequireRestart(Plugin plugin) {
        HashSet<String> keys = new HashSet<String>();
        for (ModuleDescriptor descriptor : plugin.getModuleDescriptors()) {
            if (descriptor.getClass().getAnnotation(RequiresRestart.class) == null) continue;
            keys.add(descriptor.getKey());
        }
        return keys;
    }

    public static boolean doesModuleElementApplyToApplication(Element element, Set<Application> applications, InstallationMode installationMode) {
        Preconditions.checkNotNull((Object)element);
        Preconditions.checkNotNull(applications);
        ModuleRestricts restricts = ModuleRestricts.parse(element);
        boolean valid = restricts.isValidFor(applications, installationMode);
        if (!valid && logger.isDebugEnabled()) {
            logger.debug("Module '{}' with key '{}' is restricted to the following applications {} and therefore does not apply to applications {}", new Object[]{element.getName(), element.attributeValue("key"), restricts, PluginUtils.asString(applications)});
        }
        return valid;
    }

    private static String asString(Set<Application> applications) {
        return "[" + Joiner.on((String)",").join((Iterable)applications.stream().map(app -> MoreObjects.toStringHelper((String)app.getKey()).add("version", (Object)app.getVersion()).add("build", (Object)app.getBuildNumber()).toString()).collect(Collectors.toList())) + "]";
    }

    public static int getDefaultEnablingWaitPeriod() {
        return Integer.parseInt(System.getProperty(ATLASSIAN_PLUGINS_ENABLE_WAIT, DEFAULT_ATLASSIAN_PLUGINS_ENABLE_WAIT_SECONDS));
    }

    public static boolean isAtlassianDevMode() {
        return Boolean.getBoolean(ATLASSIAN_DEV_MODE);
    }
}

