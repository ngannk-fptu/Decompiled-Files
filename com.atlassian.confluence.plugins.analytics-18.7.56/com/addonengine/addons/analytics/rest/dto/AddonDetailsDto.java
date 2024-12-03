/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest.dto;

import com.addonengine.addons.analytics.rest.dto.SampleDataDetailsDto;
import com.addonengine.addons.analytics.rest.util.ApiDateTime;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u001f\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/AddonDetailsDto;", "", "firstInstalledAt", "Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "sampleDataDetails", "Lcom/addonengine/addons/analytics/rest/dto/SampleDataDetailsDto;", "(Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;Lcom/addonengine/addons/analytics/rest/dto/SampleDataDetailsDto;)V", "getFirstInstalledAt", "()Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "getSampleDataDetails", "()Lcom/addonengine/addons/analytics/rest/dto/SampleDataDetailsDto;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class AddonDetailsDto {
    @NotNull
    private final ApiDateTime firstInstalledAt;
    @Nullable
    private final SampleDataDetailsDto sampleDataDetails;

    public AddonDetailsDto(@NotNull ApiDateTime firstInstalledAt, @Nullable SampleDataDetailsDto sampleDataDetails) {
        Intrinsics.checkNotNullParameter((Object)firstInstalledAt, (String)"firstInstalledAt");
        this.firstInstalledAt = firstInstalledAt;
        this.sampleDataDetails = sampleDataDetails;
    }

    @NotNull
    public final ApiDateTime getFirstInstalledAt() {
        return this.firstInstalledAt;
    }

    @Nullable
    public final SampleDataDetailsDto getSampleDataDetails() {
        return this.sampleDataDetails;
    }

    @NotNull
    public final ApiDateTime component1() {
        return this.firstInstalledAt;
    }

    @Nullable
    public final SampleDataDetailsDto component2() {
        return this.sampleDataDetails;
    }

    @NotNull
    public final AddonDetailsDto copy(@NotNull ApiDateTime firstInstalledAt, @Nullable SampleDataDetailsDto sampleDataDetails) {
        Intrinsics.checkNotNullParameter((Object)firstInstalledAt, (String)"firstInstalledAt");
        return new AddonDetailsDto(firstInstalledAt, sampleDataDetails);
    }

    public static /* synthetic */ AddonDetailsDto copy$default(AddonDetailsDto addonDetailsDto, ApiDateTime apiDateTime, SampleDataDetailsDto sampleDataDetailsDto, int n, Object object) {
        if ((n & 1) != 0) {
            apiDateTime = addonDetailsDto.firstInstalledAt;
        }
        if ((n & 2) != 0) {
            sampleDataDetailsDto = addonDetailsDto.sampleDataDetails;
        }
        return addonDetailsDto.copy(apiDateTime, sampleDataDetailsDto);
    }

    @NotNull
    public String toString() {
        return "AddonDetailsDto(firstInstalledAt=" + this.firstInstalledAt + ", sampleDataDetails=" + this.sampleDataDetails + ')';
    }

    public int hashCode() {
        int result = this.firstInstalledAt.hashCode();
        result = result * 31 + (this.sampleDataDetails == null ? 0 : this.sampleDataDetails.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AddonDetailsDto)) {
            return false;
        }
        AddonDetailsDto addonDetailsDto = (AddonDetailsDto)other;
        if (!Intrinsics.areEqual((Object)this.firstInstalledAt, (Object)addonDetailsDto.firstInstalledAt)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.sampleDataDetails, (Object)addonDetailsDto.sampleDataDetails);
    }
}

