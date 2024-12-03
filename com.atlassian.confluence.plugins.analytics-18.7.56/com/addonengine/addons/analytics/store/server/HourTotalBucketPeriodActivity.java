/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server;

import com.addonengine.addons.analytics.store.server.BucketPeriodActivity;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/store/server/HourTotalBucketPeriodActivity;", "Lcom/addonengine/addons/analytics/store/server/BucketPeriodActivity;", "bucket", "", "total", "", "(DJ)V", "getBucket", "()D", "getTotal", "()J", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "analytics"})
public final class HourTotalBucketPeriodActivity
implements BucketPeriodActivity {
    private final double bucket;
    private final long total;

    public HourTotalBucketPeriodActivity(double bucket, long total) {
        this.bucket = bucket;
        this.total = total;
    }

    @Override
    public double getBucket() {
        return this.bucket;
    }

    public final long getTotal() {
        return this.total;
    }

    public final double component1() {
        return this.bucket;
    }

    public final long component2() {
        return this.total;
    }

    @NotNull
    public final HourTotalBucketPeriodActivity copy(double bucket, long total) {
        return new HourTotalBucketPeriodActivity(bucket, total);
    }

    public static /* synthetic */ HourTotalBucketPeriodActivity copy$default(HourTotalBucketPeriodActivity hourTotalBucketPeriodActivity, double d, long l, int n, Object object) {
        if ((n & 1) != 0) {
            d = hourTotalBucketPeriodActivity.bucket;
        }
        if ((n & 2) != 0) {
            l = hourTotalBucketPeriodActivity.total;
        }
        return hourTotalBucketPeriodActivity.copy(d, l);
    }

    @NotNull
    public String toString() {
        return "HourTotalBucketPeriodActivity(bucket=" + this.bucket + ", total=" + this.total + ')';
    }

    public int hashCode() {
        int result = Double.hashCode(this.bucket);
        result = result * 31 + Long.hashCode(this.total);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HourTotalBucketPeriodActivity)) {
            return false;
        }
        HourTotalBucketPeriodActivity hourTotalBucketPeriodActivity = (HourTotalBucketPeriodActivity)other;
        if (Double.compare(this.bucket, hourTotalBucketPeriodActivity.bucket) != 0) {
            return false;
        }
        return this.total == hourTotalBucketPeriodActivity.total;
    }
}

