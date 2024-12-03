/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CollectionUtils {
    public static <T> boolean isNullOrEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        LinkedList<T> merged = new LinkedList<T>();
        if (list1 != null) {
            merged.addAll(list1);
        }
        if (list2 != null) {
            merged.addAll(list2);
        }
        return merged;
    }

    public static String join(Collection<String> toJoin, String separator) {
        if (CollectionUtils.isNullOrEmpty(toJoin)) {
            return "";
        }
        StringBuilder joinedString = new StringBuilder();
        int currentIndex = 0;
        for (String s : toJoin) {
            if (s != null) {
                joinedString.append(s);
            }
            if (currentIndex++ == toJoin.size() - 1) continue;
            joinedString.append(separator);
        }
        return joinedString.toString();
    }
}

