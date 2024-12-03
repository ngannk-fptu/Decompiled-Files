/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.upm.core.install;

import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.core.install.AbstractConnectHandlerRegistry;
import com.atlassian.upm.spi.PluginControlHandler;
import java.util.Set;
import org.osgi.framework.BundleContext;

public class ConnectPluginControlHandlerRegistryImpl
extends AbstractConnectHandlerRegistry<PluginControlHandler>
implements PluginControlHandlerRegistry {
    public ConnectPluginControlHandlerRegistryImpl(BundleContext bundleContext, Set<PluginControlHandler> internalHandlers) {
        super(bundleContext, internalHandlers);
    }

    @Override
    public Class<PluginControlHandler> getHandlerClass() {
        return PluginControlHandler.class;
    }
}

