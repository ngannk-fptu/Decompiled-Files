/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.comparator;

import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import java.util.Comparator;

public class SpaceComparator
implements Comparator<TinySpace> {
    public static final SpaceComparator SPACE_COMPARATOR = new SpaceComparator();

    @Override
    public int compare(TinySpace space1, TinySpace space2) {
        String name2;
        String name1 = space1.getName();
        int result = name1.compareToIgnoreCase(name2 = space2.getName());
        if (result == 0) {
            String key1 = space1.getKey();
            String key2 = space2.getKey();
            result = key1.compareToIgnoreCase(key2);
        }
        return result;
    }
}

