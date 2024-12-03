/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.impl;

import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.spi.PluginControlHandler;
import java.util.Collections;

public class NoOpPluginControlHandlerRegistry
implements PluginControlHandlerRegistry {
    @Override
    public Iterable<PluginControlHandler> getHandlers() {
        return Collections.emptyList();
    }

    @Override
    public Class<PluginControlHandler> getHandlerClass() {
        return PluginControlHandler.class;
    }
}

