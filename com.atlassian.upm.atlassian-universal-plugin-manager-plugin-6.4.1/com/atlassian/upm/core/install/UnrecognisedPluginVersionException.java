/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.install;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.spi.PluginInstallException;

public class UnrecognisedPluginVersionException
extends PluginInstallException {
    private final String version;

    public UnrecognisedPluginVersionException(String version) {
        super("Cannot install plugin with unsupported version '" + version + "'", Option.some("upm.pluginInstall.error.unrecognised.plugin.version"), false);
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }
}

