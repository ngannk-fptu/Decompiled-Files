/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.hostcomponents.ComponentRegistrar
 *  com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider
 */
package com.atlassian.confluence.plugin.osgi;

import com.atlassian.plugin.osgi.hostcomponents.ComponentRegistrar;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider;

public class NoopHostComponentProvider
implements HostComponentProvider {
    public void provide(ComponentRegistrar registrar) {
    }
}

