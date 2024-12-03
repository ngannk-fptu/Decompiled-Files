/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.model;

import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\bH\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00032\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2={"Lcom/addonengine/addons/analytics/store/model/DataRetentionSettingsData;", "", "exists", "", "customised", "months", "", "effectiveFrom", "Ljava/time/Instant;", "(ZZILjava/time/Instant;)V", "getCustomised", "()Z", "getEffectiveFrom", "()Ljava/time/Instant;", "getExists", "getMonths", "()I", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "", "analytics"})
public final class DataRetentionSettingsData {
    private final boolean exists;
    private final boolean customised;
    private final int months;
    @NotNull
    private final Instant effectiveFrom;

    public DataRetentionSettingsData(boolean exists, boolean customised, int months, @NotNull Instant effectiveFrom) {
        Intrinsics.checkNotNullParameter((Object)effectiveFrom, (String)"effectiveFrom");
        this.exists = exists;
        this.customised = customised;
        this.months = months;
        this.effectiveFrom = effectiveFrom;
    }

    public final boolean getExists() {
        return this.exists;
    }

    public final boolean getCustomised() {
        return this.customised;
    }

    public final int getMonths() {
        return this.months;
    }

    @NotNull
    public final Instant getEffectiveFrom() {
        return this.effectiveFrom;
    }

    public final boolean component1() {
        return this.exists;
    }

    public final boolean component2() {
        return this.customised;
    }

    public final int component3() {
        return this.months;
    }

    @NotNull
    public final Instant component4() {
        return this.effectiveFrom;
    }

    @NotNull
    public final DataRetentionSettingsData copy(boolean exists, boolean customised, int months, @NotNull Instant effectiveFrom) {
        Intrinsics.checkNotNullParameter((Object)effectiveFrom, (String)"effectiveFrom");
        return new DataRetentionSettingsData(exists, customised, months, effectiveFrom);
    }

    public static /* synthetic */ DataRetentionSettingsData copy$default(DataRetentionSettingsData dataRetentionSettingsData, boolean bl, boolean bl2, int n, Instant instant, int n2, Object object) {
        if ((n2 & 1) != 0) {
            bl = dataRetentionSettingsData.exists;
        }
        if ((n2 & 2) != 0) {
            bl2 = dataRetentionSettingsData.customised;
        }
        if ((n2 & 4) != 0) {
            n = dataRetentionSettingsData.months;
        }
        if ((n2 & 8) != 0) {
            instant = dataRetentionSettingsData.effectiveFrom;
        }
        return dataRetentionSettingsData.copy(bl, bl2, n, instant);
    }

    @NotNull
    public String toString() {
        return "DataRetentionSettingsData(exists=" + this.exists + ", customised=" + this.customised + ", months=" + this.months + ", effectiveFrom=" + this.effectiveFrom + ')';
    }

    public int hashCode() {
        int n;
        int result;
        int n2 = this.exists ? 1 : 0;
        if (n2 != 0) {
            n2 = result = 1;
        }
        if ((n = this.customised) != 0) {
            n = 1;
        }
        result = result * 31 + n;
        result = result * 31 + Integer.hashCode(this.months);
        result = result * 31 + this.effectiveFrom.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DataRetentionSettingsData)) {
            return false;
        }
        DataRetentionSettingsData dataRetentionSettingsData = (DataRetentionSettingsData)other;
        if (this.exists != dataRetentionSettingsData.exists) {
            return false;
        }
        if (this.customised != dataRetentionSettingsData.customised) {
            return false;
        }
        if (this.months != dataRetentionSettingsData.months) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.effectiveFrom, (Object)dataRetentionSettingsData.effectiveFrom);
    }
}

