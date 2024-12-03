/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server.settings.model;

import kotlin.Metadata;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0007H\u00c6\u0003J'\u0010\u0012\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00032\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0018"}, d2={"Lcom/addonengine/addons/analytics/store/server/settings/model/DataRetentionSetting;", "", "customised", "", "months", "", "effectiveFrom", "", "(ZIJ)V", "getCustomised", "()Z", "getEffectiveFrom", "()J", "getMonths", "()I", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "toString", "", "analytics"})
public final class DataRetentionSetting {
    private final boolean customised;
    private final int months;
    private final long effectiveFrom;

    public DataRetentionSetting(@JsonProperty(value="customised") boolean customised, @JsonProperty(value="months") int months, @JsonProperty(value="effectiveFrom") long effectiveFrom) {
        this.customised = customised;
        this.months = months;
        this.effectiveFrom = effectiveFrom;
    }

    public final boolean getCustomised() {
        return this.customised;
    }

    public final int getMonths() {
        return this.months;
    }

    public final long getEffectiveFrom() {
        return this.effectiveFrom;
    }

    public final boolean component1() {
        return this.customised;
    }

    public final int component2() {
        return this.months;
    }

    public final long component3() {
        return this.effectiveFrom;
    }

    @NotNull
    public final DataRetentionSetting copy(@JsonProperty(value="customised") boolean customised, @JsonProperty(value="months") int months, @JsonProperty(value="effectiveFrom") long effectiveFrom) {
        return new DataRetentionSetting(customised, months, effectiveFrom);
    }

    public static /* synthetic */ DataRetentionSetting copy$default(DataRetentionSetting dataRetentionSetting, boolean bl, int n, long l, int n2, Object object) {
        if ((n2 & 1) != 0) {
            bl = dataRetentionSetting.customised;
        }
        if ((n2 & 2) != 0) {
            n = dataRetentionSetting.months;
        }
        if ((n2 & 4) != 0) {
            l = dataRetentionSetting.effectiveFrom;
        }
        return dataRetentionSetting.copy(bl, n, l);
    }

    @NotNull
    public String toString() {
        return "DataRetentionSetting(customised=" + this.customised + ", months=" + this.months + ", effectiveFrom=" + this.effectiveFrom + ')';
    }

    public int hashCode() {
        int n = this.customised ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        int result = n;
        result = result * 31 + Integer.hashCode(this.months);
        result = result * 31 + Long.hashCode(this.effectiveFrom);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DataRetentionSetting)) {
            return false;
        }
        DataRetentionSetting dataRetentionSetting = (DataRetentionSetting)other;
        if (this.customised != dataRetentionSetting.customised) {
            return false;
        }
        if (this.months != dataRetentionSetting.months) {
            return false;
        }
        return this.effectiveFrom == dataRetentionSetting.effectiveFrom;
    }
}

