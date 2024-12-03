/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  javax.inject.Inject
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.web.condition;

import com.addonengine.addons.analytics.service.confluence.LicenseService;
import com.addonengine.addons.analytics.service.confluence.model.LicenseStatus;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import javax.inject.Inject;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/web/condition/IsLicensedCondition;", "Lcom/atlassian/confluence/plugin/descriptor/web/conditions/BaseConfluenceCondition;", "licenseService", "Lcom/addonengine/addons/analytics/service/confluence/LicenseService;", "(Lcom/addonengine/addons/analytics/service/confluence/LicenseService;)V", "shouldDisplay", "", "context", "Lcom/atlassian/confluence/plugin/descriptor/web/WebInterfaceContext;", "analytics"})
public final class IsLicensedCondition
extends BaseConfluenceCondition {
    @NotNull
    private final LicenseService licenseService;

    @Inject
    public IsLicensedCondition(@NotNull LicenseService licenseService) {
        Intrinsics.checkNotNullParameter((Object)licenseService, (String)"licenseService");
        this.licenseService = licenseService;
    }

    protected boolean shouldDisplay(@Nullable WebInterfaceContext context) {
        return this.licenseService.getStatus() == LicenseStatus.VALID;
    }
}

