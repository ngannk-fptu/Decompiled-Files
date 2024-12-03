/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.install;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.spi.PluginInstallException;

public class XmlPluginsUnsupportedException
extends PluginInstallException {
    public XmlPluginsUnsupportedException() {
        super("This application does not support installing XML plugins", Option.some("upm.pluginInstall.error.xml.plugins.unsupported"), false);
    }
}

