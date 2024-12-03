/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.spaces.SpaceGroup;
import java.text.Collator;
import java.util.Comparator;

@Deprecated
public class SpaceGroupComparator
implements Comparator {
    private static final Collator collator = Collator.getInstance();

    public int compare(Object o1, Object o2) {
        String name1 = ((SpaceGroup)o1).getName();
        String name2 = ((SpaceGroup)o2).getName();
        int nameComparison = 0;
        if (name1 != null && name2 != null) {
            nameComparison = collator.compare(name1, name2);
        }
        if (nameComparison != 0) {
            return nameComparison;
        }
        return collator.compare(((SpaceGroup)o1).getKey(), ((SpaceGroup)o2).getKey());
    }
}

