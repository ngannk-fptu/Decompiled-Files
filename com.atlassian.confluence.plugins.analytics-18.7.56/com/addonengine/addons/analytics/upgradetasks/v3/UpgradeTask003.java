/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.upgradetasks.v3;

import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.model.settings.NewDataRetentionSettings;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\b\u001a\u00020\tH\u0016J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2={"Lcom/addonengine/addons/analytics/upgradetasks/v3/UpgradeTask003;", "Lcom/atlassian/activeobjects/external/ActiveObjectsUpgradeTask;", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "(Lcom/addonengine/addons/analytics/service/SettingsService;)V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "getModelVersion", "Lcom/atlassian/activeobjects/external/ModelVersion;", "upgrade", "", "currentVersion", "ao", "Lcom/atlassian/activeobjects/external/ActiveObjects;", "analytics"})
public final class UpgradeTask003
implements ActiveObjectsUpgradeTask {
    @NotNull
    private final SettingsService settingsService;
    private final Logger log;

    @Autowired
    public UpgradeTask003(@NotNull SettingsService settingsService) {
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        this.settingsService = settingsService;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @NotNull
    public ModelVersion getModelVersion() {
        ModelVersion modelVersion = ModelVersion.valueOf((String)"3");
        Intrinsics.checkNotNullExpressionValue((Object)modelVersion, (String)"valueOf(...)");
        return modelVersion;
    }

    public void upgrade(@NotNull ModelVersion currentVersion, @NotNull ActiveObjects ao) {
        Intrinsics.checkNotNullParameter((Object)currentVersion, (String)"currentVersion");
        Intrinsics.checkNotNullParameter((Object)ao, (String)"ao");
        this.log.info("About to perform UpgradeTask003...");
        SettingsService.DefaultImpls.setDataRetentionSettings$default(this.settingsService, new NewDataRetentionSettings(12, false), null, 2, null);
        this.log.info("Completed UpgradeTask003!");
    }
}

