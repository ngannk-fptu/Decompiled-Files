/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.plugins.rest.common.expand.entity;

import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import com.google.common.collect.Lists;
import java.util.LinkedList;
import java.util.List;

public class ListWrapperCallBacks {
    public static <T> ListWrapperCallback<T> identity(List<T> items) {
        return indexes -> items;
    }

    public static <T> ListWrapperCallback<T> ofList(List<T> items) {
        return ListWrapperCallBacks.ofList(items, Integer.MAX_VALUE);
    }

    public static <T> ListWrapperCallback<T> ofList(List<T> items, int maxResults) {
        return indexes -> {
            LinkedList toReturn = Lists.newLinkedList();
            for (Integer i : indexes.getIndexes(items.size())) {
                if (i < items.size()) {
                    toReturn.add(items.get(i));
                }
                if (toReturn.size() != maxResults) continue;
                break;
            }
            return toReturn;
        };
    }
}

