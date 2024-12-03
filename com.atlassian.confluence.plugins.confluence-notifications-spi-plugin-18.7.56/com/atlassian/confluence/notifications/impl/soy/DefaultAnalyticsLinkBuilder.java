/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.notifications.impl.soy;

import com.atlassian.confluence.notifications.AnalyticsRenderContext;
import com.atlassian.confluence.notifications.JwtTokenGenerator;
import com.atlassian.confluence.notifications.impl.soy.AnalyticsLinkBuilder;
import com.atlassian.confluence.notifications.impl.soy.UrlBuilderFunction;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultAnalyticsLinkBuilder
implements AnalyticsLinkBuilder {
    private static final String ATTRIBUTE_KEY_FORMAT = "src.%s.%s";
    private final AnalyticsRenderContext analyticsRenderContext;
    private UrlBuilderFunction urlBuilderFunction;

    @Autowired
    public DefaultAnalyticsLinkBuilder(AnalyticsRenderContext analyticsRenderContext, JwtTokenGenerator jwtTokenGenerator) {
        this.analyticsRenderContext = analyticsRenderContext;
        this.urlBuilderFunction = new UrlBuilderFunction(jwtTokenGenerator);
    }

    @VisibleForTesting
    public void setUrlBuilderFunction(UrlBuilderFunction urlBuilderFunction) {
        this.urlBuilderFunction = urlBuilderFunction;
    }

    @Override
    public String buildAnalyticsQuery(String link, Option<String> action) {
        AnalyticsRenderContext.Context context = this.analyticsRenderContext.getContext();
        if (context == null) {
            return link;
        }
        String medium = context.getMediumKey();
        ImmutableMap.Builder paramBuilder = ImmutableMap.builder().put((Object)"src", (Object)medium).put((Object)this.transformKey(medium, "product"), (Object)"confluence-server").put((Object)this.transformKey(medium, "timestamp"), (Object)String.valueOf(context.getTimestamp().getTime())).put((Object)this.transformKey(medium, "notification"), (Object)context.getNotificationKey().getCompleteKey());
        if (context.getRecipient().isDefined()) {
            paramBuilder.put((Object)this.transformKey(medium, "recipient"), (Object)((UserKey)context.getRecipient().get()).getStringValue());
        }
        if (action.isDefined()) {
            paramBuilder.put((Object)this.transformKey(medium, "action"), (Object)((String)action.get()));
        }
        return this.urlBuilderFunction.apply(link, paramBuilder.build(), action, context.getRecipient());
    }

    private String transformKey(String source, String attribute) {
        return String.format(ATTRIBUTE_KEY_FORMAT, source, attribute);
    }
}

