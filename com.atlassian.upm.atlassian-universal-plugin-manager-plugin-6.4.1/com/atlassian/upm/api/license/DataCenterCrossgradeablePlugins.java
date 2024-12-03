/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license;

import java.util.List;
import java.util.Objects;

public interface DataCenterCrossgradeablePlugins {
    public List<CrossgradePluginData> getDataCenterLicenseCrossgradeablePlugins();

    public static class CrossgradePluginData {
        private final String key;
        private final String name;

        public CrossgradePluginData(String key, String name) {
            this.key = Objects.requireNonNull(key);
            this.name = Objects.requireNonNull(name);
        }

        public String getName() {
            return this.name;
        }

        public String getKey() {
            return this.key;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CrossgradePluginData that = (CrossgradePluginData)o;
            return this.key.equals(that.key) && this.name.equals(that.name);
        }

        public int hashCode() {
            return Objects.hash(this.key, this.name);
        }
    }
}

