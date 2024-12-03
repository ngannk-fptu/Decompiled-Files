/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.IntroSorter;
import org.apache.lucene.util.TimSorter;

public final class CollectionUtil {
    private CollectionUtil() {
    }

    public static <T> void introSort(List<T> list, Comparator<? super T> comp) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        new ListIntroSorter<T>(list, comp).sort(0, size);
    }

    public static <T extends Comparable<? super T>> void introSort(List<T> list) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        CollectionUtil.introSort(list, ArrayUtil.naturalComparator());
    }

    public static <T> void timSort(List<T> list, Comparator<? super T> comp) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        new ListTimSorter<T>(list, comp, list.size() / 64).sort(0, size);
    }

    public static <T extends Comparable<? super T>> void timSort(List<T> list) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        CollectionUtil.timSort(list, ArrayUtil.naturalComparator());
    }

    private static final class ListTimSorter<T>
    extends TimSorter {
        final List<T> list;
        final Comparator<? super T> comp;
        final T[] tmp;

        ListTimSorter(List<T> list, Comparator<? super T> comp, int maxTempSlots) {
            super(maxTempSlots);
            if (!(list instanceof RandomAccess)) {
                throw new IllegalArgumentException("CollectionUtil can only sort random access lists in-place.");
            }
            this.list = list;
            this.comp = comp;
            this.tmp = maxTempSlots > 0 ? new Object[maxTempSlots] : null;
        }

        @Override
        protected void swap(int i, int j) {
            Collections.swap(this.list, i, j);
        }

        @Override
        protected void copy(int src, int dest) {
            this.list.set(dest, this.list.get(src));
        }

        @Override
        protected void save(int i, int len) {
            for (int j = 0; j < len; ++j) {
                this.tmp[j] = this.list.get(i + j);
            }
        }

        @Override
        protected void restore(int i, int j) {
            this.list.set(j, this.tmp[i]);
        }

        @Override
        protected int compare(int i, int j) {
            return this.comp.compare(this.list.get(i), this.list.get(j));
        }

        @Override
        protected int compareSaved(int i, int j) {
            return this.comp.compare(this.tmp[i], this.list.get(j));
        }
    }

    private static final class ListIntroSorter<T>
    extends IntroSorter {
        T pivot;
        final List<T> list;
        final Comparator<? super T> comp;

        ListIntroSorter(List<T> list, Comparator<? super T> comp) {
            if (!(list instanceof RandomAccess)) {
                throw new IllegalArgumentException("CollectionUtil can only sort random access lists in-place.");
            }
            this.list = list;
            this.comp = comp;
        }

        @Override
        protected void setPivot(int i) {
            this.pivot = this.list.get(i);
        }

        @Override
        protected void swap(int i, int j) {
            Collections.swap(this.list, i, j);
        }

        @Override
        protected int compare(int i, int j) {
            return this.comp.compare(this.list.get(i), this.list.get(j));
        }

        @Override
        protected int comparePivot(int j) {
            return this.comp.compare(this.pivot, this.list.get(j));
        }
    }
}

