/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2={"Lcom/addonengine/addons/analytics/AddonInfo;", "", "()V", "addonKey", "", "addonPath", "appName", "analytics"})
public final class AddonInfo {
    @NotNull
    public static final AddonInfo INSTANCE = new AddonInfo();
    @NotNull
    public static final String addonKey = "com.addonengine.analytics";
    @NotNull
    public static final String addonPath = "confanalytics";
    @NotNull
    public static final String appName = "Analytics for Confluence";

    private AddonInfo() {
    }
}

