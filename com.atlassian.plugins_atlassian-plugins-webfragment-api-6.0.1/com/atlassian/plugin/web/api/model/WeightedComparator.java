/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.api.model;

import com.atlassian.plugin.web.api.WebFragment;
import java.util.Comparator;

public class WeightedComparator
implements Comparator<WebFragment> {
    public static final WeightedComparator WEIGHTED_FRAGMENT_COMPARATOR = new WeightedComparator();

    @Override
    public int compare(WebFragment w1, WebFragment w2) {
        if (w1.getWeight() < w2.getWeight()) {
            return -1;
        }
        if (w1.getWeight() > w2.getWeight()) {
            return 1;
        }
        return 0;
    }
}

