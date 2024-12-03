/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.core.lease.domain;

import java.time.Duration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class Lease {
    private static final Lease NONE = new Lease(null, Duration.ZERO, false);
    @Nullable
    private final String leaseId;
    private final Duration leaseDuration;
    private final boolean renewable;

    private Lease(@Nullable String leaseId, Duration leaseDuration, boolean renewable) {
        this.leaseId = leaseId;
        this.leaseDuration = leaseDuration;
        this.renewable = renewable;
    }

    @Deprecated
    public static Lease of(String leaseId, long leaseDurationSeconds, boolean renewable) {
        Assert.isTrue((leaseDurationSeconds >= 0L ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return Lease.of(leaseId, Duration.ofSeconds(leaseDurationSeconds), renewable);
    }

    public static Lease of(String leaseId, Duration leaseDuration, boolean renewable) {
        Assert.hasText((String)leaseId, (String)"LeaseId must not be empty");
        Assert.notNull((Object)leaseDuration, (String)"Lease duration must not be null");
        Assert.isTrue((!leaseDuration.isNegative() ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return new Lease(leaseId, leaseDuration, renewable);
    }

    @Deprecated
    public static Lease fromTimeToLive(long leaseDuration) {
        Assert.isTrue((leaseDuration >= 0L ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return new Lease(null, Duration.ofSeconds(leaseDuration), false);
    }

    public static Lease fromTimeToLive(Duration leaseDuration) {
        Assert.notNull((Object)leaseDuration, (String)"Lease duration must not be null");
        Assert.isTrue((!leaseDuration.isNegative() ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return new Lease(null, leaseDuration, false);
    }

    public static Lease none() {
        return NONE;
    }

    public boolean hasLeaseId() {
        return this.leaseId != null;
    }

    @Nullable
    public String getLeaseId() {
        return this.leaseId;
    }

    public Duration getLeaseDuration() {
        return this.leaseDuration;
    }

    public boolean isRenewable() {
        return this.renewable;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lease)) {
            return false;
        }
        Lease lease = (Lease)o;
        if (this.leaseDuration != lease.leaseDuration) {
            return false;
        }
        if (this.renewable != lease.renewable) {
            return false;
        }
        return this.leaseId != null ? this.leaseId.equals(lease.leaseId) : lease.leaseId == null;
    }

    public int hashCode() {
        int result = this.leaseId != null ? this.leaseId.hashCode() : 0;
        result = 31 * result + this.leaseDuration.hashCode();
        result = 31 * result + (this.renewable ? 1 : 0);
        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [leaseId='").append(this.leaseId).append('\'');
        sb.append(", leaseDuration=").append(this.leaseDuration);
        sb.append(", renewable=").append(this.renewable);
        sb.append(']');
        return sb.toString();
    }
}

