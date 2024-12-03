/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.upm.core.install;

import com.atlassian.upm.core.install.AbstractConnectHandlerRegistry;
import com.atlassian.upm.core.install.PluginInstallHandlerRegistry;
import com.atlassian.upm.spi.PluginInstallHandler;
import java.util.Set;
import org.osgi.framework.BundleContext;

public class ConnectPluginInstallHandlerRegistryImpl
extends AbstractConnectHandlerRegistry<PluginInstallHandler>
implements PluginInstallHandlerRegistry {
    public ConnectPluginInstallHandlerRegistryImpl(BundleContext bundleContext, Set<PluginInstallHandler> internalHandlers) {
        super(bundleContext, internalHandlers);
    }

    @Override
    public Class<PluginInstallHandler> getHandlerClass() {
        return PluginInstallHandler.class;
    }
}

