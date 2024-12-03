/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.BuildNumber
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.upgrade.BuildNumber;
import java.util.Objects;

public interface BootstrapDatabaseAccessor {
    public BootstrapDatabaseData getBootstrapData();

    public static class BootstrapDatabaseData {
        private final ZduStatus zduStatus;
        private final BuildNumber finalizedBuildNumber;

        public BootstrapDatabaseData(ZduStatus zduStatus, BuildNumber finalizedBuildNumber) {
            this.zduStatus = zduStatus;
            this.finalizedBuildNumber = finalizedBuildNumber;
        }

        public ZduStatus getZduStatus() {
            return this.zduStatus;
        }

        public BuildNumber getFinalizedBuildNumber() {
            return this.finalizedBuildNumber;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BootstrapDatabaseData that = (BootstrapDatabaseData)o;
            return this.zduStatus.equals(that.zduStatus) && Objects.equals(this.finalizedBuildNumber, that.finalizedBuildNumber);
        }

        public int hashCode() {
            return Objects.hash(this.zduStatus, this.finalizedBuildNumber);
        }

        public String toString() {
            return "BootstrapDatabaseData{zduStatus=" + this.zduStatus + ", finalizedBuildNumber=" + this.finalizedBuildNumber + "}";
        }
    }
}

