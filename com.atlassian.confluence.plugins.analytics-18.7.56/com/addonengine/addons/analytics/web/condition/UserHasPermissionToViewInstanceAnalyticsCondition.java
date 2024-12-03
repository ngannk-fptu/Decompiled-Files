/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.web.condition;

import com.addonengine.addons.analytics.service.RestrictionsService;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/web/condition/UserHasPermissionToViewInstanceAnalyticsCondition;", "Lcom/atlassian/confluence/plugin/descriptor/web/conditions/BaseConfluenceCondition;", "restrictionsService", "Lcom/addonengine/addons/analytics/service/RestrictionsService;", "(Lcom/addonengine/addons/analytics/service/RestrictionsService;)V", "shouldDisplay", "", "context", "Lcom/atlassian/confluence/plugin/descriptor/web/WebInterfaceContext;", "analytics"})
public final class UserHasPermissionToViewInstanceAnalyticsCondition
extends BaseConfluenceCondition {
    @NotNull
    private final RestrictionsService restrictionsService;

    @Inject
    public UserHasPermissionToViewInstanceAnalyticsCondition(@NotNull RestrictionsService restrictionsService) {
        Intrinsics.checkNotNullParameter((Object)restrictionsService, (String)"restrictionsService");
        this.restrictionsService = restrictionsService;
    }

    protected boolean shouldDisplay(@NotNull WebInterfaceContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        Object object = context.getCurrentUser();
        if (object == null || (object = object.getKey()) == null || (object = object.getStringValue()) == null) {
            return false;
        }
        Object userKey = object;
        return this.restrictionsService.isUserAllowedToViewInstanceAnalytics((String)userKey);
    }
}

