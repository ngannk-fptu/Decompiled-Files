/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.querydsl;

import com.addonengine.addons.analytics.store.server.querydsl.QContent;
import com.addonengine.addons.analytics.store.server.querydsl.QEvent;
import com.addonengine.addons.analytics.store.server.querydsl.QSettings;
import com.addonengine.addons.analytics.store.server.querydsl.QSpaces;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0013\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006\u0017"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/Tables;", "", "()V", "activeObjectsKey", "", "content", "Lcom/addonengine/addons/analytics/store/server/querydsl/QContent;", "getContent", "()Lcom/addonengine/addons/analytics/store/server/querydsl/QContent;", "event", "Lcom/addonengine/addons/analytics/store/server/querydsl/QEvent;", "getEvent", "()Lcom/addonengine/addons/analytics/store/server/querydsl/QEvent;", "sampleEvent", "getSampleEvent", "settings", "Lcom/addonengine/addons/analytics/store/server/querydsl/QSettings;", "getSettings", "()Lcom/addonengine/addons/analytics/store/server/querydsl/QSettings;", "spaces", "Lcom/addonengine/addons/analytics/store/server/querydsl/QSpaces;", "getSpaces", "()Lcom/addonengine/addons/analytics/store/server/querydsl/QSpaces;", "analytics"})
public final class Tables {
    @NotNull
    public static final Tables INSTANCE = new Tables();
    @NotNull
    private static final String activeObjectsKey = "AO_7B47A5";
    @NotNull
    private static final QEvent event = new QEvent("AO_7B47A5_EVENT");
    @NotNull
    private static final QSpaces spaces = new QSpaces("SPACES");
    @NotNull
    private static final QContent content = new QContent("CONTENT");
    @NotNull
    private static final QEvent sampleEvent = event;
    @NotNull
    private static final QSettings settings = new QSettings("AO_7B47A5_SETTINGS");

    private Tables() {
    }

    @NotNull
    public final QEvent getEvent() {
        return event;
    }

    @NotNull
    public final QSpaces getSpaces() {
        return spaces;
    }

    @NotNull
    public final QContent getContent() {
        return content;
    }

    @NotNull
    public final QEvent getSampleEvent() {
        return sampleEvent;
    }

    @NotNull
    public final QSettings getSettings() {
        return settings;
    }
}

