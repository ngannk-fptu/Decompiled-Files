/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.install;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.spi.PluginInstallException;

public class UnknownPluginTypeException
extends PluginInstallException {
    public UnknownPluginTypeException(String message) {
        super(message, Option.some("upm.pluginInstall.error.unknown.plugin.type"), false);
    }
}

