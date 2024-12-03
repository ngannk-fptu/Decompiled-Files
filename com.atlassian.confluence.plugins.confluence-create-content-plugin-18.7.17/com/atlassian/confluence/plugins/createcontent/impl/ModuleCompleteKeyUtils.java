/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.plugin.ModuleCompleteKey;

public class ModuleCompleteKeyUtils {
    public static ModuleCompleteKey getModuleCompleteKeyFromRelative(String pluginKey, String moduleKey) {
        try {
            return new ModuleCompleteKey(moduleKey);
        }
        catch (Exception e) {
            return new ModuleCompleteKey(pluginKey, moduleKey);
        }
    }
}

