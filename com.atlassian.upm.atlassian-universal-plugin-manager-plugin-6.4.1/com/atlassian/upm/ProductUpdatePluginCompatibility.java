/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.upm.core.Plugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ProductUpdatePluginCompatibility {
    private final Iterable<Plugin> compatible;
    private final Iterable<Plugin> updateRequired;
    private final Iterable<Plugin> updateRequiredAfterProductUpdate;
    private final Iterable<Plugin> incompatible;
    private final Iterable<Plugin> unknown;

    private ProductUpdatePluginCompatibility(Builder builder) {
        this.compatible = Collections.unmodifiableList(builder.compatible);
        this.updateRequired = Collections.unmodifiableList(builder.updateRequired);
        this.updateRequiredAfterProductUpdate = Collections.unmodifiableList(builder.updateRequiredAfterProductUpdate);
        this.incompatible = Collections.unmodifiableList(builder.incompatible);
        this.unknown = Collections.unmodifiableList(builder.unknown);
    }

    public Iterable<Plugin> getCompatible() {
        return this.compatible;
    }

    public Iterable<Plugin> getUpdateRequired() {
        return this.updateRequired;
    }

    public Iterable<Plugin> getUpdateRequiredAfterProductUpdate() {
        return this.updateRequiredAfterProductUpdate;
    }

    public Iterable<Plugin> getIncompatible() {
        return this.incompatible;
    }

    public Iterable<Plugin> getUnknown() {
        return this.unknown;
    }

    public static class Builder {
        private List<Plugin> compatible = new ArrayList<Plugin>();
        private List<Plugin> updateRequired = new ArrayList<Plugin>();
        private List<Plugin> updateRequiredAfterProductUpdate = new ArrayList<Plugin>();
        private List<Plugin> incompatible = new ArrayList<Plugin>();
        private List<Plugin> unknown = new ArrayList<Plugin>();

        public Builder addCompatible(Plugin compatiblePlugin) {
            this.compatible.add(compatiblePlugin);
            return this;
        }

        public Builder addUpdateRequired(Plugin updateRequiredPlugin) {
            this.updateRequired.add(updateRequiredPlugin);
            return this;
        }

        public Builder addUpdateRequiredAfterProductUpdate(Plugin updateRequiredAfterProductUpdatePlugin) {
            this.updateRequiredAfterProductUpdate.add(updateRequiredAfterProductUpdatePlugin);
            return this;
        }

        public Builder addIncompatible(Plugin incompatiblePlugin) {
            this.incompatible.add(incompatiblePlugin);
            return this;
        }

        public Builder addUnknown(Plugin unknownPlugin) {
            this.unknown.add(unknownPlugin);
            return this;
        }

        public ProductUpdatePluginCompatibility build() {
            return new ProductUpdatePluginCompatibility(this);
        }
    }
}

