/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.install;

import com.atlassian.upm.spi.PluginInstallException;

public class InvalidDescriptorException
extends PluginInstallException {
    public InvalidDescriptorException() {
        super("Invalid atlassian-plugin.xml descriptor", false);
    }

    public InvalidDescriptorException(Throwable cause) {
        super("Unable to read atlassian-plugin.xml descriptor", cause, false);
    }
}

