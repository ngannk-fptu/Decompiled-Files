/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.Plugin;
import java.util.Collection;

public interface PluginRegistry {

    public static interface ReadWrite
    extends ReadOnly {
        public void clear();

        public void put(Plugin var1);

        public Plugin remove(String var1);
    }

    public static interface ReadOnly {
        public Collection<Plugin> getAll();

        public Plugin get(String var1);
    }
}

