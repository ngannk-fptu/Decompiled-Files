/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.pac;

import com.atlassian.upm.core.Plugin;
import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;

public final class SpiPluginComparator
implements Comparator<Plugin> {
    @Override
    public int compare(Plugin plugin1, Plugin plugin2) {
        if (plugin1 == null || plugin2 == null) {
            if (plugin1 == null) {
                return plugin2 == null ? 0 : -1;
            }
            return 1;
        }
        int result = StringUtils.compare((String)plugin1.getName(), (String)plugin2.getName());
        if (result != 0) {
            return result;
        }
        return StringUtils.compare((String)plugin1.getKey(), (String)plugin2.getKey());
    }
}

