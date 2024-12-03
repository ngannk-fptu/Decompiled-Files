/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.comparators.ReverseComparator
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.ContentCreationComparator;
import com.atlassian.confluence.util.ContentEntityObjectTitleComparator;
import com.atlassian.confluence.util.ContentModificationComparator;
import java.util.Comparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;

public class ContentComparatorFactory {
    public static Comparator getComparator(String sortType, boolean reverse) {
        if (!StringUtils.isNotEmpty((CharSequence)sortType)) {
            return null;
        }
        Comparator result = null;
        if ("creation".equalsIgnoreCase(sortType)) {
            result = new ContentCreationComparator();
        } else if ("title".equalsIgnoreCase(sortType)) {
            result = ContentEntityObjectTitleComparator.getInstance();
        } else if ("modified".equalsIgnoreCase(sortType)) {
            result = new ContentModificationComparator();
        } else {
            throw new IllegalArgumentException("Must specify a valid sort type ('creation', 'title' or 'modified').");
        }
        if (reverse) {
            result = new ReverseComparator(result);
        }
        return result;
    }
}

