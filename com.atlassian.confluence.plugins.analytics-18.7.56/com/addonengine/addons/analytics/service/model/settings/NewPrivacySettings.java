/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model.settings;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000b\u001a\u00020\fH\u00d6\u0001J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u000f"}, d2={"Lcom/addonengine/addons/analytics/service/model/settings/NewPrivacySettings;", "", "enabled", "", "(Z)V", "getEnabled", "()Z", "component1", "copy", "equals", "other", "hashCode", "", "toString", "", "analytics"})
public final class NewPrivacySettings {
    private final boolean enabled;

    public NewPrivacySettings(boolean enabled) {
        this.enabled = enabled;
    }

    public final boolean getEnabled() {
        return this.enabled;
    }

    public final boolean component1() {
        return this.enabled;
    }

    @NotNull
    public final NewPrivacySettings copy(boolean enabled) {
        return new NewPrivacySettings(enabled);
    }

    public static /* synthetic */ NewPrivacySettings copy$default(NewPrivacySettings newPrivacySettings, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = newPrivacySettings.enabled;
        }
        return newPrivacySettings.copy(bl);
    }

    @NotNull
    public String toString() {
        return "NewPrivacySettings(enabled=" + this.enabled + ')';
    }

    public int hashCode() {
        int n = this.enabled ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        return n;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NewPrivacySettings)) {
            return false;
        }
        NewPrivacySettings newPrivacySettings = (NewPrivacySettings)other;
        return this.enabled == newPrivacySettings.enabled;
    }
}

