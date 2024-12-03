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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u00052\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2={"Lcom/addonengine/addons/analytics/service/model/settings/NewDataRetentionSettings;", "", "months", "", "customised", "", "(IZ)V", "getCustomised", "()Z", "getMonths", "()I", "component1", "component2", "copy", "equals", "other", "hashCode", "toString", "", "analytics"})
public final class NewDataRetentionSettings {
    private final int months;
    private final boolean customised;

    public NewDataRetentionSettings(int months, boolean customised) {
        this.months = months;
        this.customised = customised;
    }

    public final int getMonths() {
        return this.months;
    }

    public final boolean getCustomised() {
        return this.customised;
    }

    public final int component1() {
        return this.months;
    }

    public final boolean component2() {
        return this.customised;
    }

    @NotNull
    public final NewDataRetentionSettings copy(int months, boolean customised) {
        return new NewDataRetentionSettings(months, customised);
    }

    public static /* synthetic */ NewDataRetentionSettings copy$default(NewDataRetentionSettings newDataRetentionSettings, int n, boolean bl, int n2, Object object) {
        if ((n2 & 1) != 0) {
            n = newDataRetentionSettings.months;
        }
        if ((n2 & 2) != 0) {
            bl = newDataRetentionSettings.customised;
        }
        return newDataRetentionSettings.copy(n, bl);
    }

    @NotNull
    public String toString() {
        return "NewDataRetentionSettings(months=" + this.months + ", customised=" + this.customised + ')';
    }

    public int hashCode() {
        int result = Integer.hashCode(this.months);
        int n = this.customised ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        result = result * 31 + n;
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NewDataRetentionSettings)) {
            return false;
        }
        NewDataRetentionSettings newDataRetentionSettings = (NewDataRetentionSettings)other;
        if (this.months != newDataRetentionSettings.months) {
            return false;
        }
        return this.customised == newDataRetentionSettings.customised;
    }
}

