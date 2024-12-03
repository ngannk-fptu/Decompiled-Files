/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  io.micrometer.core.instrument.Tag
 */
package com.atlassian.util.profiling.micrometer.util;

import com.atlassian.annotations.Internal;
import io.micrometer.core.instrument.Tag;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Internal
abstract class TagComparator {
    private static final List<String> orderedPrefixes = Arrays.asList("fromPluginKey", "invokerPluginKey");
    private static final List<String> orderedSuffixes = Arrays.asList("subCategory", "statistic");
    private static final Comparator<String> tagKeyComparator = Comparator.comparing(TagComparator::rankByPrefix).thenComparing(TagComparator::rankBySuffix);
    static final Comparator<Tag> tagComparator = Comparator.comparing(Tag::getKey, tagKeyComparator);

    TagComparator() {
    }

    private static int rankByPrefix(String key) {
        return orderedPrefixes.contains(key) ? orderedPrefixes.indexOf(key) : Integer.MAX_VALUE;
    }

    private static int rankBySuffix(String key) {
        return orderedSuffixes.indexOf(key);
    }
}

