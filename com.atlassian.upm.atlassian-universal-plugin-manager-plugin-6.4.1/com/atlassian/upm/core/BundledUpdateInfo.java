/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class BundledUpdateInfo {
    @JsonProperty
    private final int platformTargetBuildNumber;
    @JsonProperty
    private final Collection<UpdateItem> updateItems;

    @JsonCreator
    public BundledUpdateInfo(@JsonProperty(value="platformTargetBuildNumber") int platformTargetBuildNumber, @JsonProperty(value="updateItems") Collection<UpdateItem> updateItems) {
        this.platformTargetBuildNumber = Objects.requireNonNull(Integer.valueOf(platformTargetBuildNumber), "platformTargetBuildNumber");
        this.updateItems = Collections.unmodifiableCollection(Objects.requireNonNull(updateItems, "updateItems"));
    }

    public int getPlatformTargetBuildNumber() {
        return this.platformTargetBuildNumber;
    }

    @JsonIgnore
    public Iterable<UpdateItem> getUpdateItems() {
        return this.updateItems;
    }

    public String toString() {
        String updateItemsToString = this.updateItems.stream().map(UpdateItem::toString).collect(Collectors.joining(", "));
        return "BundledUpdateInfo(" + this.platformTargetBuildNumber + ", " + updateItemsToString + ")";
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object other) {
        return other instanceof BundledUpdateInfo && other.toString().equals(this.toString());
    }

    public static class UpdateItem {
        @JsonProperty
        private final String pluginKey;
        @JsonProperty
        private final String name;
        @JsonProperty
        private final String version;
        @JsonProperty
        private final URI uri;

        @JsonCreator
        public UpdateItem(@JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="name") String name, @JsonProperty(value="version") String version, @JsonProperty(value="uri") URI uri) {
            this.pluginKey = pluginKey;
            this.name = name;
            this.version = version;
            this.uri = uri;
        }

        public String getPluginKey() {
            return this.pluginKey;
        }

        public String getName() {
            return this.name;
        }

        public String getVersion() {
            return this.version;
        }

        public URI getUri() {
            return this.uri;
        }

        public String toString() {
            return "UpdateItem(" + this.pluginKey + ", " + this.name + ", " + this.version + ", " + this.uri + ")";
        }

        public int hashCode() {
            return this.toString().hashCode();
        }

        public boolean equals(Object other) {
            return other instanceof UpdateItem && other.toString().equals(this.toString());
        }
    }
}

