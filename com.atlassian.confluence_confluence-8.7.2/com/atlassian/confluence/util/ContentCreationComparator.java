/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import java.util.Comparator;

public class ContentCreationComparator
implements Comparator {
    public int compare(Object a, Object b) {
        long bTime;
        long aTime = ((ContentEntityObject)a).getCreationDate().getTime();
        if (aTime == (bTime = ((ContentEntityObject)b).getCreationDate().getTime())) {
            return 0;
        }
        if (aTime < bTime) {
            return -1;
        }
        return 1;
    }
}

