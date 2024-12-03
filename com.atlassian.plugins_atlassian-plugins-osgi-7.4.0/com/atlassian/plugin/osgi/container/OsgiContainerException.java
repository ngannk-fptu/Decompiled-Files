/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginException
 */
package com.atlassian.plugin.osgi.container;

import com.atlassian.plugin.PluginException;

public class OsgiContainerException
extends PluginException {
    public OsgiContainerException() {
    }

    public OsgiContainerException(String s) {
        super(s);
    }

    public OsgiContainerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public OsgiContainerException(Throwable throwable) {
        super(throwable);
    }
}

