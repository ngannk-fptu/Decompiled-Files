/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import java.text.Collator;
import java.util.Comparator;

public class ContentEntityObjectTitleComparator
implements Comparator {
    public static final ContentEntityObjectTitleComparator INSTANCE = new ContentEntityObjectTitleComparator();
    private Collator collator = Collator.getInstance();

    private ContentEntityObjectTitleComparator() {
    }

    public int compare(Object o1, Object o2) {
        String title1 = ((ContentEntityObject)o1).getTitle();
        String title2 = ((ContentEntityObject)o2).getTitle();
        return this.collator.compare(title1 == null ? "" : title1, title2 == null ? "" : title2);
    }

    public static ContentEntityObjectTitleComparator getInstance() {
        return INSTANCE;
    }
}

