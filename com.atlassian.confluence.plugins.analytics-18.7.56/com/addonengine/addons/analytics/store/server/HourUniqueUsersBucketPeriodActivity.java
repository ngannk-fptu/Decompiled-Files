/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server;

import com.addonengine.addons.analytics.store.server.BucketPeriodActivity;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0005H\u00d6\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/store/server/HourUniqueUsersBucketPeriodActivity;", "Lcom/addonengine/addons/analytics/store/server/BucketPeriodActivity;", "bucket", "", "userKey", "", "(DLjava/lang/String;)V", "getBucket", "()D", "getUserKey", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "analytics"})
public final class HourUniqueUsersBucketPeriodActivity
implements BucketPeriodActivity {
    private final double bucket;
    @NotNull
    private final String userKey;

    public HourUniqueUsersBucketPeriodActivity(double bucket, @NotNull String userKey) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        this.bucket = bucket;
        this.userKey = userKey;
    }

    @Override
    public double getBucket() {
        return this.bucket;
    }

    @NotNull
    public final String getUserKey() {
        return this.userKey;
    }

    public final double component1() {
        return this.bucket;
    }

    @NotNull
    public final String component2() {
        return this.userKey;
    }

    @NotNull
    public final HourUniqueUsersBucketPeriodActivity copy(double bucket, @NotNull String userKey) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        return new HourUniqueUsersBucketPeriodActivity(bucket, userKey);
    }

    public static /* synthetic */ HourUniqueUsersBucketPeriodActivity copy$default(HourUniqueUsersBucketPeriodActivity hourUniqueUsersBucketPeriodActivity, double d, String string, int n, Object object) {
        if ((n & 1) != 0) {
            d = hourUniqueUsersBucketPeriodActivity.bucket;
        }
        if ((n & 2) != 0) {
            string = hourUniqueUsersBucketPeriodActivity.userKey;
        }
        return hourUniqueUsersBucketPeriodActivity.copy(d, string);
    }

    @NotNull
    public String toString() {
        return "HourUniqueUsersBucketPeriodActivity(bucket=" + this.bucket + ", userKey=" + this.userKey + ')';
    }

    public int hashCode() {
        int result = Double.hashCode(this.bucket);
        result = result * 31 + this.userKey.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HourUniqueUsersBucketPeriodActivity)) {
            return false;
        }
        HourUniqueUsersBucketPeriodActivity hourUniqueUsersBucketPeriodActivity = (HourUniqueUsersBucketPeriodActivity)other;
        if (Double.compare(this.bucket, hourUniqueUsersBucketPeriodActivity.bucket) != 0) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.userKey, (Object)hourUniqueUsersBucketPeriodActivity.userKey);
    }
}

