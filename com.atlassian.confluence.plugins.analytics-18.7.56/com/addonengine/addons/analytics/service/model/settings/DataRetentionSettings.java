/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model.settings;

import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\bH\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00032\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2={"Lcom/addonengine/addons/analytics/service/model/settings/DataRetentionSettings;", "", "active", "", "customised", "months", "", "minDate", "Ljava/time/Instant;", "(ZZILjava/time/Instant;)V", "getActive", "()Z", "getCustomised", "getMinDate", "()Ljava/time/Instant;", "getMonths", "()I", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "", "analytics"})
public final class DataRetentionSettings {
    private final boolean active;
    private final boolean customised;
    private final int months;
    @NotNull
    private final Instant minDate;

    public DataRetentionSettings(boolean active, boolean customised, int months, @NotNull Instant minDate) {
        Intrinsics.checkNotNullParameter((Object)minDate, (String)"minDate");
        this.active = active;
        this.customised = customised;
        this.months = months;
        this.minDate = minDate;
    }

    public final boolean getActive() {
        return this.active;
    }

    public final boolean getCustomised() {
        return this.customised;
    }

    public final int getMonths() {
        return this.months;
    }

    @NotNull
    public final Instant getMinDate() {
        return this.minDate;
    }

    public final boolean component1() {
        return this.active;
    }

    public final boolean component2() {
        return this.customised;
    }

    public final int component3() {
        return this.months;
    }

    @NotNull
    public final Instant component4() {
        return this.minDate;
    }

    @NotNull
    public final DataRetentionSettings copy(boolean active, boolean customised, int months, @NotNull Instant minDate) {
        Intrinsics.checkNotNullParameter((Object)minDate, (String)"minDate");
        return new DataRetentionSettings(active, customised, months, minDate);
    }

    public static /* synthetic */ DataRetentionSettings copy$default(DataRetentionSettings dataRetentionSettings, boolean bl, boolean bl2, int n, Instant instant, int n2, Object object) {
        if ((n2 & 1) != 0) {
            bl = dataRetentionSettings.active;
        }
        if ((n2 & 2) != 0) {
            bl2 = dataRetentionSettings.customised;
        }
        if ((n2 & 4) != 0) {
            n = dataRetentionSettings.months;
        }
        if ((n2 & 8) != 0) {
            instant = dataRetentionSettings.minDate;
        }
        return dataRetentionSettings.copy(bl, bl2, n, instant);
    }

    @NotNull
    public String toString() {
        return "DataRetentionSettings(active=" + this.active + ", customised=" + this.customised + ", months=" + this.months + ", minDate=" + this.minDate + ')';
    }

    public int hashCode() {
        int n;
        int result;
        int n2 = this.active ? 1 : 0;
        if (n2 != 0) {
            n2 = result = 1;
        }
        if ((n = this.customised) != 0) {
            n = 1;
        }
        result = result * 31 + n;
        result = result * 31 + Integer.hashCode(this.months);
        result = result * 31 + this.minDate.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DataRetentionSettings)) {
            return false;
        }
        DataRetentionSettings dataRetentionSettings = (DataRetentionSettings)other;
        if (this.active != dataRetentionSettings.active) {
            return false;
        }
        if (this.customised != dataRetentionSettings.customised) {
            return false;
        }
        if (this.months != dataRetentionSettings.months) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.minDate, (Object)dataRetentionSettings.minDate);
    }
}

