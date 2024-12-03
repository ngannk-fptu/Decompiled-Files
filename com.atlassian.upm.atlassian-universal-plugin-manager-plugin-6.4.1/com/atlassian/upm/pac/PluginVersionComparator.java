/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.pac;

import com.atlassian.upm.pac.AvailableAddonWithVersion;
import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;

public class PluginVersionComparator
implements Comparator<AvailableAddonWithVersion> {
    @Override
    public int compare(AvailableAddonWithVersion plugin1, AvailableAddonWithVersion plugin2) {
        if (plugin1 == null || plugin2 == null) {
            if (plugin1 == null) {
                return plugin2 == null ? 0 : -1;
            }
            return 1;
        }
        int result = StringUtils.compare((String)plugin1.getAddon().getName(), (String)plugin2.getAddon().getName());
        if (result != 0) {
            return result;
        }
        return StringUtils.compare((String)plugin1.getAddon().getKey(), (String)plugin2.getAddon().getKey());
    }
}

