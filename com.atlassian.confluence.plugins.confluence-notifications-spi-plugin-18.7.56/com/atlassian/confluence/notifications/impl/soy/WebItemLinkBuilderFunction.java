/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.fugue.Option
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.notifications.impl.soy;

import com.atlassian.confluence.notifications.impl.soy.AnalyticsLinkBuilder;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.fugue.Option;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class WebItemLinkBuilderFunction
implements SoyServerFunction<String> {
    private static final String ANALYTICS_ACTION_KEY = "src-action";
    private static final String FUNCTION_NAME = "webItemLink";
    private static final Set<Integer> VALID_ARG_SIZES = ImmutableSet.of((Object)1);
    private final SettingsManager settingsManager;
    private final AnalyticsLinkBuilder linkBuilder;

    public WebItemLinkBuilderFunction(SettingsManager settingsManager, AnalyticsLinkBuilder linkBuilder) {
        this.settingsManager = settingsManager;
        this.linkBuilder = linkBuilder;
    }

    public String apply(Object ... args) {
        String analyticsAction;
        Preconditions.checkNotNull((Object)args);
        Preconditions.checkArgument((boolean)(args[0] instanceof Map), (String)"Param 0 [%s] should be an instance of a map", (Object)args[0]);
        Object link = args[0];
        Map linkInstance = (Map)link;
        String urlWithoutContextPath = (String)linkInstance.get("urlWithoutContextPath");
        Map params = (Map)linkInstance.get("params");
        Preconditions.checkNotNull((Object)urlWithoutContextPath);
        Option action = params != null ? (!StringUtils.isBlank((CharSequence)(analyticsAction = (String)params.get(ANALYTICS_ACTION_KEY))) ? Option.some((Object)analyticsAction) : Option.none()) : Option.none();
        String linkBody = this.settingsManager.getGlobalSettings().getBaseUrl() + urlWithoutContextPath;
        return this.linkBuilder.buildAnalyticsQuery(linkBody, (Option<String>)action);
    }

    public String getName() {
        return FUNCTION_NAME;
    }

    public Set<Integer> validArgSizes() {
        return VALID_ARG_SIZES;
    }
}

