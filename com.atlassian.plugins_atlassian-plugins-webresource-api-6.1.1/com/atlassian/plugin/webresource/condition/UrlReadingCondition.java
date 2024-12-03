/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.baseconditions.BaseCondition
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.baseconditions.BaseCondition;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;

public interface UrlReadingCondition
extends BaseCondition {
    public void init(Map<String, String> var1) throws PluginParseException;

    public void addToUrl(UrlBuilder var1);

    public boolean shouldDisplay(QueryParams var1);
}

