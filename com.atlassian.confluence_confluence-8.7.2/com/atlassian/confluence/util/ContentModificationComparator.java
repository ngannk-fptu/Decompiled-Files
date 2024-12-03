/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import java.util.Comparator;

public class ContentModificationComparator
implements Comparator {
    public int compare(Object a, Object b) {
        long bTime;
        ContentEntityObject contentA = (ContentEntityObject)a;
        ContentEntityObject contentB = (ContentEntityObject)b;
        long aTime = contentA.getLastModificationDate() != null ? contentA.getLastModificationDate().getTime() : contentA.getCreationDate().getTime();
        long l = bTime = contentB.getLastModificationDate() != null ? contentB.getLastModificationDate().getTime() : contentB.getCreationDate().getTime();
        if (aTime == bTime) {
            return 0;
        }
        if (aTime < bTime) {
            return -1;
        }
        return 1;
    }
}

