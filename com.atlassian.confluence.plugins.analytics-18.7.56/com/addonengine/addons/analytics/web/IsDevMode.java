/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  kotlin.Metadata
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.web;

import com.addonengine.addons.analytics.util.UtilsKt;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;
import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0016J\u001e\u0010\u0007\u001a\u00020\u00042\u0014\u0010\b\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n\u0018\u00010\tH\u0016J\u0012\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016\u00a8\u0006\u000f"}, d2={"Lcom/addonengine/addons/analytics/web/IsDevMode;", "Lcom/atlassian/plugin/webresource/condition/UrlReadingCondition;", "()V", "addToUrl", "", "urlBuilder", "Lcom/atlassian/plugin/webresource/url/UrlBuilder;", "init", "map", "", "", "shouldDisplay", "", "params", "Lcom/atlassian/plugin/webresource/QueryParams;", "analytics"})
public final class IsDevMode
implements UrlReadingCondition {
    public void init(@Nullable Map<String, String> map) {
    }

    public void addToUrl(@Nullable UrlBuilder urlBuilder) {
    }

    public boolean shouldDisplay(@Nullable QueryParams params) {
        return UtilsKt.isAddonDevMode();
    }
}

