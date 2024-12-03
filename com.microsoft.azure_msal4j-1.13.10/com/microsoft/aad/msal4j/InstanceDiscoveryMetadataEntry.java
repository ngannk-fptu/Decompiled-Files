/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

class InstanceDiscoveryMetadataEntry {
    @JsonProperty(value="preferred_network")
    String preferredNetwork;
    @JsonProperty(value="preferred_cache")
    String preferredCache;
    @JsonProperty(value="aliases")
    Set<String> aliases;

    public static InstanceDiscoveryMetadataEntryBuilder builder() {
        return new InstanceDiscoveryMetadataEntryBuilder();
    }

    String preferredNetwork() {
        return this.preferredNetwork;
    }

    String preferredCache() {
        return this.preferredCache;
    }

    Set<String> aliases() {
        return this.aliases;
    }

    public InstanceDiscoveryMetadataEntry() {
    }

    public InstanceDiscoveryMetadataEntry(String preferredNetwork, String preferredCache, Set<String> aliases) {
        this.preferredNetwork = preferredNetwork;
        this.preferredCache = preferredCache;
        this.aliases = aliases;
    }

    public static class InstanceDiscoveryMetadataEntryBuilder {
        private String preferredNetwork;
        private String preferredCache;
        private Set<String> aliases;

        InstanceDiscoveryMetadataEntryBuilder() {
        }

        public InstanceDiscoveryMetadataEntryBuilder preferredNetwork(String preferredNetwork) {
            this.preferredNetwork = preferredNetwork;
            return this;
        }

        public InstanceDiscoveryMetadataEntryBuilder preferredCache(String preferredCache) {
            this.preferredCache = preferredCache;
            return this;
        }

        public InstanceDiscoveryMetadataEntryBuilder aliases(Set<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public InstanceDiscoveryMetadataEntry build() {
            return new InstanceDiscoveryMetadataEntry(this.preferredNetwork, this.preferredCache, this.aliases);
        }

        public String toString() {
            return "InstanceDiscoveryMetadataEntry.InstanceDiscoveryMetadataEntryBuilder(preferredNetwork=" + this.preferredNetwork + ", preferredCache=" + this.preferredCache + ", aliases=" + this.aliases + ")";
        }
    }
}

