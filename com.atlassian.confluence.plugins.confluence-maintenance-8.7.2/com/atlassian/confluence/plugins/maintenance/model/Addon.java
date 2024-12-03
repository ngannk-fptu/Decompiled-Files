/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 */
package com.atlassian.confluence.plugins.maintenance.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public final class Addon {
    private final String name;
    private final String vendorName;
    private final String icon;
    private final boolean readOnlyModeCompatible;
    private final boolean disabled;

    Addon(String name, String vendorName, String icon, boolean readOnlyModeCompatible, boolean disabled) {
        this.name = name;
        this.vendorName = vendorName;
        this.icon = icon;
        this.readOnlyModeCompatible = readOnlyModeCompatible;
        this.disabled = disabled;
    }

    public String getName() {
        return this.name;
    }

    public String getVendorName() {
        return this.vendorName;
    }

    public String getIcon() {
        return this.icon;
    }

    public boolean isReadOnlyModeCompatible() {
        return this.readOnlyModeCompatible;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public static class Builder {
        private String name;
        private String vendorName;
        private String icon;
        private boolean readOnlyModeCompatible;
        private boolean disabled;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder readOnlyModeCompatible(boolean readOnlyModeCompatible) {
            this.readOnlyModeCompatible = readOnlyModeCompatible;
            return this;
        }

        public Builder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Addon build() {
            return new Addon(this.name, this.vendorName, this.icon, this.readOnlyModeCompatible, this.disabled);
        }
    }
}

