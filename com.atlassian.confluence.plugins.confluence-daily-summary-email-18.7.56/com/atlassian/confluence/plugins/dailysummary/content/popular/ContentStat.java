/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.core.ContentEntityObject;
import java.util.Comparator;

public class ContentStat {
    private final ContentEntityObject content;
    private final int count;
    public static final Comparator<ContentStat> comparator = (o1, o2) -> {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return o1.count - o2.count;
    };

    public ContentStat(ContentEntityObject content, int count) {
        this.content = content;
        this.count = count;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public int getCount() {
        return this.count;
    }
}

