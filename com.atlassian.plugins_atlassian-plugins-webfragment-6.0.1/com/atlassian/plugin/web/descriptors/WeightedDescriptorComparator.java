/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WeightedDescriptor
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.web.descriptors.WeightedDescriptor;
import java.util.Comparator;

public class WeightedDescriptorComparator
implements Comparator<WeightedDescriptor> {
    @Override
    public int compare(WeightedDescriptor w1, WeightedDescriptor w2) {
        if (w1.getWeight() < w2.getWeight()) {
            return -1;
        }
        if (w1.getWeight() > w2.getWeight()) {
            return 1;
        }
        return 0;
    }
}

