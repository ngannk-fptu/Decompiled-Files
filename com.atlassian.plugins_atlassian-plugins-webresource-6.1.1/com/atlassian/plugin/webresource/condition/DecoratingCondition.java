/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.baseconditions.BaseCondition
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.plugin.web.baseconditions.BaseCondition;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Map;

public interface DecoratingCondition
extends BaseCondition {
    public void addToUrl(UrlBuilder var1, UrlBuildingStrategy var2);

    public boolean shouldDisplay(QueryParams var1);

    public boolean canEncodeStateIntoUrl();

    public boolean shouldDisplayImmediate(Map<String, Object> var1, UrlBuildingStrategy var2);

    public DecoratingCondition invertCondition();

    public Dimensions computeDimensions();
}

