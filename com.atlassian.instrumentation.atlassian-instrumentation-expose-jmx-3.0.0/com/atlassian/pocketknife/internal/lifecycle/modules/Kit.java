/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  org.apache.commons.lang.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.pocketknife.internal.lifecycle.modules;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

public class Kit {
    public static final String NOT_SPECIFIED = "not-specified??";

    static String getModuleIdentifier(Element element) {
        return Kit.mkId(element.attributeValue("key"), element.attributeValue("name"));
    }

    static String getModuleIdentifier(ModuleDescriptor<?> descriptor) {
        return descriptor.getCompleteKey();
    }

    static String pluginIdentifier(Plugin plugin) {
        return plugin.getKey();
    }

    private static String mkId(String key, String name) {
        return StringUtils.defaultIfEmpty((String)key, (String)NOT_SPECIFIED) + " - " + StringUtils.defaultIfEmpty((String)name, (String)"");
    }
}

