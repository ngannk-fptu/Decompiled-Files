/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.spaces.Space;
import java.text.Collator;
import java.util.Comparator;

public class SpaceComparator
implements Comparator<Space> {
    private static final Collator collator = Collator.getInstance();

    @Override
    public int compare(Space space1, Space space2) {
        String name1 = space1.getName();
        String name2 = space2.getName();
        int nameComparison = 0;
        if (name1 != null && name2 != null) {
            nameComparison = collator.compare(name1, name2);
        }
        if (nameComparison != 0) {
            return nameComparison;
        }
        return collator.compare(space1.getKey(), space2.getKey());
    }
}

