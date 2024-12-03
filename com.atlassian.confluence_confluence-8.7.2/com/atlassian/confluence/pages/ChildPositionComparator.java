/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.pages.NaturalStringComparator;
import com.atlassian.confluence.pages.Page;
import java.util.Comparator;

public class ChildPositionComparator
implements Comparator<Page> {
    public static final ChildPositionComparator INSTANCE = new ChildPositionComparator();
    private static final NaturalStringComparator NATURAL_COMPARATOR = new NaturalStringComparator();

    @Override
    public int compare(Page page0, Page page1) {
        int result = 0;
        result = null != page0.getPosition() && page0.getPosition().equals(page1.getPosition()) ? 0 : (page0.getPosition() == null ? (page1.getPosition() == null ? 0 : 1) : (page1.getPosition() == null ? -1 : page0.getPosition().compareTo(page1.getPosition())));
        if (result == 0) {
            result = NATURAL_COMPARATOR.compare(page0.getTitle(), page1.getTitle());
        }
        return result;
    }
}

