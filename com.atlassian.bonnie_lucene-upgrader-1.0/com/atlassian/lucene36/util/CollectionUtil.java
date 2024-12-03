/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.SorterTemplate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CollectionUtil {
    private CollectionUtil() {
    }

    private static <T> SorterTemplate getSorter(final List<T> list, final Comparator<? super T> comp) {
        if (!(list instanceof RandomAccess)) {
            throw new IllegalArgumentException("CollectionUtil can only sort random access lists in-place.");
        }
        return new SorterTemplate(){
            private T pivot;

            protected void swap(int i, int j) {
                Collections.swap(list, i, j);
            }

            protected int compare(int i, int j) {
                return comp.compare(list.get(i), list.get(j));
            }

            protected void setPivot(int i) {
                this.pivot = list.get(i);
            }

            protected int comparePivot(int j) {
                return comp.compare(this.pivot, list.get(j));
            }
        };
    }

    private static <T extends Comparable<? super T>> SorterTemplate getSorter(final List<T> list) {
        if (!(list instanceof RandomAccess)) {
            throw new IllegalArgumentException("CollectionUtil can only sort random access lists in-place.");
        }
        return new SorterTemplate(){
            private T pivot;

            protected void swap(int i, int j) {
                Collections.swap(list, i, j);
            }

            protected int compare(int i, int j) {
                return ((Comparable)list.get(i)).compareTo(list.get(j));
            }

            protected void setPivot(int i) {
                this.pivot = (Comparable)list.get(i);
            }

            protected int comparePivot(int j) {
                return this.pivot.compareTo(list.get(j));
            }
        };
    }

    public static <T> void quickSort(List<T> list, Comparator<? super T> comp) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        CollectionUtil.getSorter(list, comp).quickSort(0, size - 1);
    }

    public static <T extends Comparable<? super T>> void quickSort(List<T> list) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        CollectionUtil.getSorter(list).quickSort(0, size - 1);
    }

    public static <T> void mergeSort(List<T> list, Comparator<? super T> comp) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        CollectionUtil.getSorter(list, comp).mergeSort(0, size - 1);
    }

    public static <T extends Comparable<? super T>> void mergeSort(List<T> list) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        CollectionUtil.getSorter(list).mergeSort(0, size - 1);
    }

    public static <T> void insertionSort(List<T> list, Comparator<? super T> comp) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        CollectionUtil.getSorter(list, comp).insertionSort(0, size - 1);
    }

    public static <T extends Comparable<? super T>> void insertionSort(List<T> list) {
        int size = list.size();
        if (size <= 1) {
            return;
        }
        CollectionUtil.getSorter(list).insertionSort(0, size - 1);
    }
}

