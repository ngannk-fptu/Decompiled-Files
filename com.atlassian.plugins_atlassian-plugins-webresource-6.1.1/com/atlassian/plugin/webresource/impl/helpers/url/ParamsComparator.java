/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 */
package com.atlassian.plugin.webresource.impl.helpers.url;

import com.atlassian.plugin.webresource.impl.config.Config;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;

public final class ParamsComparator
implements Comparator<Map<String, String>> {
    private static final HashSet<String> PARAMS_SORT_ORDER_SET = new HashSet<String>(Config.PARAMS_SORT_ORDER);
    private boolean isAdditionalSortingRequired;

    @Override
    public int compare(Map<String, String> params1, Map<String, String> params2) {
        Sets.SetView keys1 = Sets.intersection(params1.keySet(), PARAMS_SORT_ORDER_SET);
        Sets.SetView keys2 = Sets.intersection(params2.keySet(), PARAMS_SORT_ORDER_SET);
        if (keys1.size() == 1 && keys2.size() == 1) {
            int key2Index;
            String key1 = (String)Iterables.getOnlyElement((Iterable)keys1);
            String key2 = (String)Iterables.getOnlyElement((Iterable)keys2);
            int key1Index = Config.PARAMS_SORT_ORDER.indexOf(key1);
            if (key1Index != (key2Index = Config.PARAMS_SORT_ORDER.indexOf(key2))) {
                return key1Index - key2Index;
            }
        }
        if (keys1.size() == keys2.size()) {
            this.isAdditionalSortingRequired |= true;
            return 0;
        }
        return keys1.size() - keys2.size();
    }

    public boolean isAdditionalSortingRequired() {
        return this.isAdditionalSortingRequired;
    }
}

