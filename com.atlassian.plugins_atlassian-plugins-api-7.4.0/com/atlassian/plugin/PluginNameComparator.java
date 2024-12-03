/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.Plugin;
import java.io.Serializable;
import java.util.Comparator;

class PluginNameComparator
implements Comparator<Plugin>,
Serializable {
    static final long serialVersionUID = -2595168544386708474L;

    PluginNameComparator() {
    }

    @Override
    public int compare(Plugin p1, Plugin p2) {
        String name1 = p1.getName();
        String name2 = p2.getName();
        if (name1 == null || name2 == null) {
            if (name1 == null) {
                return name2 == null ? 0 : -1;
            }
            return 1;
        }
        return name1.compareTo(name2);
    }
}

