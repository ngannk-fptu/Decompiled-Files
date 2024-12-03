/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.upgradetasks.v4;

import com.addonengine.addons.analytics.service.RestrictionsService;
import com.addonengine.addons.analytics.service.model.restrictions.InstanceRestrictions;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\b\u001a\u00020\tH\u0016J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2={"Lcom/addonengine/addons/analytics/upgradetasks/v4/UpgradeTask004;", "Lcom/atlassian/activeobjects/external/ActiveObjectsUpgradeTask;", "restrictionService", "Lcom/addonengine/addons/analytics/service/RestrictionsService;", "(Lcom/addonengine/addons/analytics/service/RestrictionsService;)V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "getModelVersion", "Lcom/atlassian/activeobjects/external/ModelVersion;", "upgrade", "", "currentVersion", "ao", "Lcom/atlassian/activeobjects/external/ActiveObjects;", "analytics"})
public final class UpgradeTask004
implements ActiveObjectsUpgradeTask {
    @NotNull
    private final RestrictionsService restrictionService;
    private final Logger log;

    @Autowired
    public UpgradeTask004(@NotNull RestrictionsService restrictionService) {
        Intrinsics.checkNotNullParameter((Object)restrictionService, (String)"restrictionService");
        this.restrictionService = restrictionService;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @NotNull
    public ModelVersion getModelVersion() {
        ModelVersion modelVersion = ModelVersion.valueOf((String)"4");
        Intrinsics.checkNotNullExpressionValue((Object)modelVersion, (String)"valueOf(...)");
        return modelVersion;
    }

    public void upgrade(@NotNull ModelVersion currentVersion, @NotNull ActiveObjects ao) {
        Intrinsics.checkNotNullParameter((Object)currentVersion, (String)"currentVersion");
        Intrinsics.checkNotNullParameter((Object)ao, (String)"ao");
        this.log.info("About to perform UpgradeTask004...");
        this.restrictionService.saveInstanceRestrictions(new InstanceRestrictions(CollectionsKt.emptyList()));
        this.log.info("Completed UpgradeTask004!");
    }
}

