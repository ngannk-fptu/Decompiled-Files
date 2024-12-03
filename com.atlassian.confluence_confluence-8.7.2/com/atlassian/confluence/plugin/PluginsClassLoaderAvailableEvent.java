/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.util.Assert;

@SuppressFBWarnings(value={"SE_NO_SERIALVERSIONID"})
public final class PluginsClassLoaderAvailableEvent
extends ConfluenceEvent {
    private final ClassLoader pluginsClassLoader;

    public PluginsClassLoaderAvailableEvent(Object source, ClassLoader pluginsClassLoader) {
        super(source);
        Assert.notNull((Object)pluginsClassLoader, (String)"pluginsClassLoader must not be null");
        this.pluginsClassLoader = pluginsClassLoader;
    }

    public ClassLoader getPluginsClassLoader() {
        return this.pluginsClassLoader;
    }
}

