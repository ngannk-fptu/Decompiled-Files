/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.labels.Labelling;
import java.text.Collator;
import java.util.Comparator;

public class LabellingComparator
implements Comparator {
    private Collator collator = Collator.getInstance();

    public int compare(Object o1, Object o2) {
        return this.collator.compare(((Labelling)o1).getLableable().getTitle(), ((Labelling)o2).getLableable().getTitle());
    }
}

