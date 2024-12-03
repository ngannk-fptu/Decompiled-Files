/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

import java.util.Comparator;
import java.util.Locale;

public class LocaleComparator
implements Comparator<Locale> {
    @Override
    public int compare(Locale l1, Locale l2) {
        if (l1 == null && l2 == null) {
            return 0;
        }
        if (l2 == null) {
            return -1;
        }
        if (l1 == null) {
            return 1;
        }
        String displayName1 = l1.getDisplayName();
        String displayName2 = l2.getDisplayName();
        if (displayName1 == null) {
            return -1;
        }
        if (displayName2 == null) {
            return 1;
        }
        return displayName1.toLowerCase().compareTo(displayName2.toLowerCase());
    }
}

