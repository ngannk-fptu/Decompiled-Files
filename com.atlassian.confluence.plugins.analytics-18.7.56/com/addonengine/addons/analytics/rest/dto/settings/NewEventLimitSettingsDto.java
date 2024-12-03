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
package com.addonengine.addons.analytics.rest.dto.settings;

import kotlin.Metadata;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\f\u001a\u00020\rH\u00d6\u0001J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/settings/NewEventLimitSettingsDto;", "", "maxRowCount", "", "(J)V", "getMaxRowCount", "()J", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class NewEventLimitSettingsDto {
    private final long maxRowCount;

    public NewEventLimitSettingsDto(@JsonProperty(value="maxRowCount") long maxRowCount) {
        this.maxRowCount = maxRowCount;
    }

    public final long getMaxRowCount() {
        return this.maxRowCount;
    }

    public final long component1() {
        return this.maxRowCount;
    }

    @NotNull
    public final NewEventLimitSettingsDto copy(@JsonProperty(value="maxRowCount") long maxRowCount) {
        return new NewEventLimitSettingsDto(maxRowCount);
    }

    public static /* synthetic */ NewEventLimitSettingsDto copy$default(NewEventLimitSettingsDto newEventLimitSettingsDto, long l, int n, Object object) {
        if ((n & 1) != 0) {
            l = newEventLimitSettingsDto.maxRowCount;
        }
        return newEventLimitSettingsDto.copy(l);
    }

    @NotNull
    public String toString() {
        return "NewEventLimitSettingsDto(maxRowCount=" + this.maxRowCount + ')';
    }

    public int hashCode() {
        return Long.hashCode(this.maxRowCount);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NewEventLimitSettingsDto)) {
            return false;
        }
        NewEventLimitSettingsDto newEventLimitSettingsDto = (NewEventLimitSettingsDto)other;
        return this.maxRowCount == newEventLimitSettingsDto.maxRowCount;
    }
}

