/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class ListUtils {
    public static List getFirstMatchingObjectsFrom(Iterator it, List criteria, int maxSize) {
        ArrayList targetList = new ArrayList(maxSize > 100 ? 100 : maxSize);
        while (it.hasNext() && targetList.size() < maxSize) {
            Object o = it.next();
            if (!ListUtils.shouldInclude(criteria, targetList, o)) continue;
            targetList.add(o);
        }
        return targetList;
    }

    public static List getFirstMatchingObjectsFrom(Iterator it, Criterion criterion, int maxSize) {
        ArrayList targetList = new ArrayList(maxSize > 100 ? 100 : maxSize);
        while (it.hasNext() && targetList.size() < maxSize) {
            Object o = it.next();
            if (!criterion.test(targetList, o)) continue;
            targetList.add(o);
        }
        return targetList;
    }

    private static boolean shouldInclude(List criteria, List alreadyIncluded, Object o) {
        for (Criterion criterion : criteria) {
            if (criterion.test(alreadyIncluded, o)) continue;
            return false;
        }
        return true;
    }

    public static Set<String> createSetOfNonEmptyElementsFromStringArray(String[] array) {
        if (array == null || array.length == 0) {
            return Collections.emptySet();
        }
        HashSet<String> keys = new HashSet<String>();
        for (int i = 0; i < array.length; ++i) {
            if (!StringUtils.isNotEmpty((CharSequence)array[i])) continue;
            keys.add(array[i].trim());
        }
        return keys;
    }

    public static interface Criterion {
        public boolean test(List var1, Object var2);
    }
}

