/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.atlassian.confluence.plugins.createcontent.model;

import com.google.common.base.Objects;

public class BlueprintState {
    public static final BlueprintState FULLY_ENABLED = new BlueprintState(false, false, false, false);
    public static final BlueprintState FULLY_DISABLED = new BlueprintState(true, true, true, true);
    private final boolean disabledGlobally;
    private final boolean disabledInSpace;
    private final boolean disabledInPluginSystem;
    private final boolean disabledByWebInterfaceManager;

    private BlueprintState(boolean disabledGlobally, boolean disabledInSpace, boolean disabledInPluginSystem, boolean disabledByWebInterfaceManager) {
        this.disabledGlobally = disabledGlobally;
        this.disabledInSpace = disabledInSpace;
        this.disabledInPluginSystem = disabledInPluginSystem;
        this.disabledByWebInterfaceManager = disabledByWebInterfaceManager;
    }

    public boolean isDisabledGlobally() {
        return this.disabledGlobally;
    }

    public boolean isDisabledInSpace() {
        return this.disabledInSpace;
    }

    public boolean isDisabledInPluginSystem() {
        return this.disabledInPluginSystem;
    }

    public boolean isDisabledByWebInterfaceManager() {
        return this.disabledByWebInterfaceManager;
    }

    public boolean in(BlueprintState ... blueprintStates) {
        for (BlueprintState blueprintState : blueprintStates) {
            if (!this.equals(blueprintState)) continue;
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.disabledGlobally, this.disabledInSpace, this.disabledInPluginSystem, this.disabledByWebInterfaceManager});
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BlueprintState)) {
            return false;
        }
        BlueprintState that = (BlueprintState)obj;
        return this.disabledGlobally == that.disabledGlobally && this.disabledInSpace == that.disabledInSpace && this.disabledInPluginSystem == that.disabledInPluginSystem && this.disabledByWebInterfaceManager == that.disabledByWebInterfaceManager;
    }

    public static class Builder {
        private boolean disabledGlobally;
        private boolean disabledInSpace;
        private boolean disabledInPluginSystem;
        private boolean disabledByWebInterfaceManager;

        public Builder disabledGlobally(boolean disabledGlobally) {
            this.disabledGlobally = disabledGlobally;
            return this;
        }

        public Builder disabledInSpace(boolean disabledInSpace) {
            this.disabledInSpace = disabledInSpace;
            return this;
        }

        public Builder disabledInPluginSystem(boolean disabledInPluginSystem) {
            this.disabledInPluginSystem = disabledInPluginSystem;
            return this;
        }

        public Builder disabledByWebInterfaceManager(boolean disabledByWebInterfaceManager) {
            this.disabledByWebInterfaceManager = disabledByWebInterfaceManager;
            return this;
        }

        public BlueprintState build() {
            return new BlueprintState(this.disabledGlobally, this.disabledInSpace, this.disabledInPluginSystem, this.disabledByWebInterfaceManager);
        }
    }
}

