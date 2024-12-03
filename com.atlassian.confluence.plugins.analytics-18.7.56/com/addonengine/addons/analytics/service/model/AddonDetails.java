/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model;

import com.addonengine.addons.analytics.service.model.SampleDataDetails;
import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u001f\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/service/model/AddonDetails;", "", "firstEventAt", "Ljava/time/Instant;", "sampleDataDetails", "Lcom/addonengine/addons/analytics/service/model/SampleDataDetails;", "(Ljava/time/Instant;Lcom/addonengine/addons/analytics/service/model/SampleDataDetails;)V", "getFirstEventAt", "()Ljava/time/Instant;", "getSampleDataDetails", "()Lcom/addonengine/addons/analytics/service/model/SampleDataDetails;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class AddonDetails {
    @NotNull
    private final Instant firstEventAt;
    @Nullable
    private final SampleDataDetails sampleDataDetails;

    public AddonDetails(@NotNull Instant firstEventAt2, @Nullable SampleDataDetails sampleDataDetails) {
        Intrinsics.checkNotNullParameter((Object)firstEventAt2, (String)"firstEventAt");
        this.firstEventAt = firstEventAt2;
        this.sampleDataDetails = sampleDataDetails;
    }

    @NotNull
    public final Instant getFirstEventAt() {
        return this.firstEventAt;
    }

    @Nullable
    public final SampleDataDetails getSampleDataDetails() {
        return this.sampleDataDetails;
    }

    @NotNull
    public final Instant component1() {
        return this.firstEventAt;
    }

    @Nullable
    public final SampleDataDetails component2() {
        return this.sampleDataDetails;
    }

    @NotNull
    public final AddonDetails copy(@NotNull Instant firstEventAt2, @Nullable SampleDataDetails sampleDataDetails) {
        Intrinsics.checkNotNullParameter((Object)firstEventAt2, (String)"firstEventAt");
        return new AddonDetails(firstEventAt2, sampleDataDetails);
    }

    public static /* synthetic */ AddonDetails copy$default(AddonDetails addonDetails, Instant instant, SampleDataDetails sampleDataDetails, int n, Object object) {
        if ((n & 1) != 0) {
            instant = addonDetails.firstEventAt;
        }
        if ((n & 2) != 0) {
            sampleDataDetails = addonDetails.sampleDataDetails;
        }
        return addonDetails.copy(instant, sampleDataDetails);
    }

    @NotNull
    public String toString() {
        return "AddonDetails(firstEventAt=" + this.firstEventAt + ", sampleDataDetails=" + this.sampleDataDetails + ')';
    }

    public int hashCode() {
        int result = this.firstEventAt.hashCode();
        result = result * 31 + (this.sampleDataDetails == null ? 0 : this.sampleDataDetails.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AddonDetails)) {
            return false;
        }
        AddonDetails addonDetails = (AddonDetails)other;
        if (!Intrinsics.areEqual((Object)this.firstEventAt, (Object)addonDetails.firstEventAt)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.sampleDataDetails, (Object)addonDetails.sampleDataDetails);
    }
}

