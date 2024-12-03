/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import java.util.LinkedList;
import java.util.List;

public class ListUtils {
    public static <T> List<T> removeNullItems(List<T> list) {
        if (list == null) {
            return null;
        }
        LinkedList<T> out = new LinkedList<T>();
        for (T item : list) {
            if (item == null) continue;
            out.add(item);
        }
        return out;
    }

    private ListUtils() {
    }
}

