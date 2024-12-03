/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import java.text.Collator;
import java.util.Comparator;

public class ContentComparator
implements Comparator {
    private Collator collator = Collator.getInstance();

    public int compare(Object o1, Object o2) {
        return this.collator.compare(((ContentEntityObject)o1).getTitle(), ((ContentEntityObject)o2).getTitle());
    }
}

