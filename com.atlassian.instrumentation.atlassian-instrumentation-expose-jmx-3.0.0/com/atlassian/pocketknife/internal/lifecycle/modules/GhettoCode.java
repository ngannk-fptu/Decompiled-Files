/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.Plugin
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pocketknife.internal.lifecycle.modules;

import com.atlassian.annotations.Internal;
import com.atlassian.plugin.Plugin;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhettoCode {
    private static final Logger log = LoggerFactory.getLogger(GhettoCode.class);

    static void addModuleDescriptorElement(Plugin plugin, Element element, String moduleKey) {
        try {
            element.addAttribute("com.atlassian.pocketknife.internal.lifecycle.modules.DynamicModule", "true");
            Method addModuleDescriptorElement = plugin.getClass().getDeclaredMethod("addModuleDescriptorElement", String.class, Element.class);
            addModuleDescriptorElement.setAccessible(true);
            addModuleDescriptorElement.invoke((Object)plugin, moduleKey, element);
        }
        catch (NoSuchMethodException e) {
            log.error("Unable to record OsgiPlugin dom.  Has the interface changed? ");
        }
        catch (InvocationTargetException e) {
            log.error("Unable to record OsgiPlugin dom.  Has the interface changed? ");
        }
        catch (IllegalAccessException e) {
            log.error("Unable to record OsgiPlugin dom.  Has the interface changed? ");
        }
    }

    static void removeModuleDescriptorElement(Plugin plugin, String moduleKey) {
        if (!GhettoCode.removeElementFromMap(plugin, moduleKey)) {
            GhettoCode.addModuleDescriptorElement(plugin, null, moduleKey);
        }
    }

    @Internal
    public static Map<String, Element> getModuleElements(Plugin plugin) {
        try {
            Method getModuleElements = plugin.getClass().getDeclaredMethod("getModuleElements", new Class[0]);
            getModuleElements.setAccessible(true);
            HashMap mapOfElements = getModuleElements.invoke((Object)plugin, new Object[0]);
            if (mapOfElements == null) {
                mapOfElements = new HashMap();
            }
            return mapOfElements;
        }
        catch (NoSuchMethodException e) {
            log.error("Unable to access OsgiPlugin dom.  Has the interface changed? ");
        }
        catch (InvocationTargetException e) {
            log.error("Unable to access OsgiPlugin dom.  Has the interface changed? ");
        }
        catch (IllegalAccessException e) {
            log.error("Unable to access OsgiPlugin dom.  Has the interface changed? ");
        }
        catch (ClassCastException e) {
            log.error("Unable to access OsgiPlugin dom.  Has the interface changed? ");
        }
        return Collections.emptyMap();
    }

    private static boolean removeElementFromMap(Plugin plugin, String moduleKey) {
        try {
            Method getModuleElements = plugin.getClass().getDeclaredMethod("getModuleElements", new Class[0]);
            getModuleElements.setAccessible(true);
            Map result = (Map)getModuleElements.invoke((Object)plugin, new Object[0]);
            result.remove(moduleKey);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}

