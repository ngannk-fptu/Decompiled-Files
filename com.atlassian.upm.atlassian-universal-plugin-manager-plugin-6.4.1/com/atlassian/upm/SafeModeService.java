/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginsEnablementState;
import com.atlassian.upm.core.SafeModeAccessor;

public interface SafeModeService
extends SafeModeAccessor {
    public boolean enterSafeMode();

    public void exitSafeMode(boolean var1);

    public void applyConfiguration(PluginsEnablementState var1);

    public static final class PluginModuleStateUpdateException
    extends RuntimeException {
        private final Plugin.Module module;
        private final boolean enabling;

        public PluginModuleStateUpdateException(Plugin.Module module, boolean enabling) {
            this.module = module;
            this.enabling = enabling;
        }

        public Plugin.Module getModule() {
            return this.module;
        }

        public boolean isEnabling() {
            return this.enabling;
        }
    }

    public static final class PluginStateUpdateException
    extends RuntimeException {
        private final Plugin plugin;
        private final boolean enabling;

        public PluginStateUpdateException(Plugin plugin, boolean enabling) {
            this.plugin = plugin;
            this.enabling = enabling;
        }

        public Plugin getPlugin() {
            return this.plugin;
        }

        public boolean isEnabling() {
            return this.enabling;
        }
    }

    public static final class MissingSavedConfigurationException
    extends RuntimeException {
    }
}

