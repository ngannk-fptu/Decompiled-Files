/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.addonengine.addons.analytics.upgradetasks.v2;

import com.addonengine.addons.analytics.store.server.ao.Settings;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\fH\u0016R\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2={"Lcom/addonengine/addons/analytics/upgradetasks/v2/UpgradeTask002;", "Lcom/atlassian/activeobjects/external/ActiveObjectsUpgradeTask;", "()V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "getModelVersion", "Lcom/atlassian/activeobjects/external/ModelVersion;", "upgrade", "", "currentVersion", "ao", "Lcom/atlassian/activeobjects/external/ActiveObjects;", "analytics"})
@SourceDebugExtension(value={"SMAP\nUpgradeTask002.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UpgradeTask002.kt\ncom/addonengine/addons/analytics/upgradetasks/v2/UpgradeTask002\n+ 2 KotlinActiveObjectExtensions.kt\ncom/addonengine/addons/analytics/extensions/ao/KotlinActiveObjectExtensionsKt\n*L\n1#1,24:1\n38#2:25\n*S KotlinDebug\n*F\n+ 1 UpgradeTask002.kt\ncom/addonengine/addons/analytics/upgradetasks/v2/UpgradeTask002\n*L\n20#1:25\n*E\n"})
public final class UpgradeTask002
implements ActiveObjectsUpgradeTask {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @NotNull
    public ModelVersion getModelVersion() {
        ModelVersion modelVersion = ModelVersion.valueOf((String)"2");
        Intrinsics.checkNotNullExpressionValue((Object)modelVersion, (String)"valueOf(...)");
        return modelVersion;
    }

    public void upgrade(@NotNull ModelVersion currentVersion, @NotNull ActiveObjects ao) {
        Intrinsics.checkNotNullParameter((Object)currentVersion, (String)"currentVersion");
        Intrinsics.checkNotNullParameter((Object)ao, (String)"ao");
        this.log.info("About to perform UpgradeTask002...");
        ActiveObjects $this$migrate$iv = ao;
        boolean $i$f$migrate = false;
        Class[] classArray = new Class[]{Settings.class};
        $this$migrate$iv.migrate(classArray);
        this.log.info("Completed UpgradeTask002!");
    }
}

