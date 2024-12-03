/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service;

import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.migration.agent.model.stats.ServerStats;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;

@ParametersAreNonnullByDefault
public interface StatsStoringService {
    @Nonnull
    public Optional<Stored<ServerStats>> loadServerStats(Supplier<ConfluenceInfo> var1);

    public void storeServerStats(ServerStats var1);

    public void storeBandwidthKBS(long var1);

    public static class Stored<T> {
        private final T data;
        private final Instant storedTime;

        @Generated
        public Stored(T data, Instant storedTime) {
            this.data = data;
            this.storedTime = storedTime;
        }

        @Generated
        public T getData() {
            return this.data;
        }

        @Generated
        public Instant getStoredTime() {
            return this.storedTime;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Stored)) {
                return false;
            }
            Stored other = (Stored)o;
            if (!other.canEqual(this)) {
                return false;
            }
            T this$data = this.getData();
            T other$data = other.getData();
            if (this$data == null ? other$data != null : !this$data.equals(other$data)) {
                return false;
            }
            Instant this$storedTime = this.getStoredTime();
            Instant other$storedTime = other.getStoredTime();
            return !(this$storedTime == null ? other$storedTime != null : !((Object)this$storedTime).equals(other$storedTime));
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof Stored;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            T $data = this.getData();
            result = result * 59 + ($data == null ? 43 : $data.hashCode());
            Instant $storedTime = this.getStoredTime();
            result = result * 59 + ($storedTime == null ? 43 : ((Object)$storedTime).hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "StatsStoringService.Stored(data=" + this.getData() + ", storedTime=" + this.getStoredTime() + ")";
        }
    }
}

