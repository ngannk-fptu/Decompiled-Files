/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.wrapped.JsonableBoolean
 *  com.atlassian.plugin.util.PluginUtils
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.plugin.clientsideextensions;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.wrapped.JsonableBoolean;
import com.atlassian.plugin.util.PluginUtils;
import com.atlassian.webresource.api.data.WebResourceDataProvider;

public class AtlassianDevModeDataProvider
implements WebResourceDataProvider {
    public Jsonable get() {
        return new JsonableBoolean(Boolean.valueOf(PluginUtils.isAtlassianDevMode()));
    }
}

