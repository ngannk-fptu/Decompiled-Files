/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.core;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartialList<T> {
    private static final PartialList EMPTY = new PartialList(0, 0, 0, Collections.emptyList());
    private final int available;
    private final int start;
    private final int count;
    private final List<T> list;

    public static <T> PartialList<T> empty() {
        return EMPTY;
    }

    public static <T> PartialList<T> forAll(Iterable<T> iterable) {
        ArrayList all = Lists.newArrayList(iterable);
        if (all.isEmpty()) {
            return PartialList.empty();
        }
        return new PartialList<T>(all.size(), 0, all);
    }

    public PartialList(int available, int start, List<T> list) {
        this(available, start, list.size(), list);
    }

    public PartialList(int available, int start, int count, Iterable<T> list) {
        this.available = available;
        this.start = start;
        this.count = count;
        this.list = Collections.unmodifiableList(Lists.newArrayList(list));
    }

    public int getAvailable() {
        return this.available;
    }

    public int getStart() {
        return this.start;
    }

    public int getCount() {
        return this.count;
    }

    public List<T> getList() {
        return this.list;
    }
}

