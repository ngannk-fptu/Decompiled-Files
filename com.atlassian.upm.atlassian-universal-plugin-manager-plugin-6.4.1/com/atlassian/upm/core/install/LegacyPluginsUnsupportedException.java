/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.install;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.spi.PluginInstallException;

public class LegacyPluginsUnsupportedException
extends PluginInstallException {
    public LegacyPluginsUnsupportedException() {
        super("This application does not support installing Plugins 1 plugins dynamically", Option.some("upm.pluginInstall.error.legacy.plugins.unsupported"), false);
    }
}

