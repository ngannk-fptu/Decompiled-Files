/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.plugin.web;

import com.atlassian.plugin.PluginParseException;
import java.util.Map;

public interface ContextProvider {
    public void init(Map<String, String> var1) throws PluginParseException;

    public Map<String, Object> getContextMap(Map<String, Object> var1);
}

