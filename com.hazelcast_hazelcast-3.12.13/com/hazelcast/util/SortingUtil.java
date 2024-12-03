/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.internal.util.ResultSet;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.PagingPredicateAccessor;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.util.IterationType;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class SortingUtil {
    private SortingUtil() {
    }

    public static int compare(Comparator<Map.Entry> comparator, IterationType iterationType, Map.Entry entry1, Map.Entry entry2) {
        Object comparable2;
        Object comparable1;
        if (comparator != null) {
            int result = comparator.compare(entry1, entry2);
            if (result != 0) {
                return result;
            }
            return SortingUtil.compareIntegers(entry1.getKey().hashCode(), entry2.getKey().hashCode());
        }
        switch (iterationType) {
            case KEY: {
                comparable1 = entry1.getKey();
                comparable2 = entry2.getKey();
                break;
            }
            case VALUE: {
                comparable1 = entry1.getValue();
                comparable2 = entry2.getValue();
                break;
            }
            default: {
                if (entry1 instanceof Comparable && entry2 instanceof Comparable) {
                    comparable1 = entry1;
                    comparable2 = entry2;
                    break;
                }
                comparable1 = entry1.getKey();
                comparable2 = entry2.getKey();
            }
        }
        SortingUtil.checkIfComparable(comparable1);
        SortingUtil.checkIfComparable(comparable2);
        int result = ((Comparable)comparable1).compareTo(comparable2);
        if (result != 0) {
            return result;
        }
        return SortingUtil.compareIntegers(entry1.getKey().hashCode(), entry2.getKey().hashCode());
    }

    private static void checkIfComparable(Object comparable) {
        if (comparable instanceof Comparable) {
            return;
        }
        throw new IllegalArgumentException("Not comparable " + comparable);
    }

    private static int compareIntegers(int i1, int i2) {
        if (i1 > i2) {
            return 1;
        }
        if (i2 > i1) {
            return -1;
        }
        return 0;
    }

    public static Comparator<Map.Entry> newComparator(final Comparator<Map.Entry> comparator, final IterationType iterationType) {
        return new Comparator<Map.Entry>(){

            @Override
            public int compare(Map.Entry entry1, Map.Entry entry2) {
                return SortingUtil.compare(comparator, iterationType, entry1, entry2);
            }
        };
    }

    private static Comparator<QueryableEntry> newComparator(final PagingPredicate pagingPredicate) {
        return new Comparator<QueryableEntry>(){

            @Override
            public int compare(QueryableEntry entry1, QueryableEntry entry2) {
                return SortingUtil.compare(pagingPredicate.getComparator(), pagingPredicate.getIterationType(), entry1, entry2);
            }
        };
    }

    public static List<QueryableEntry> getSortedSubList(List<QueryableEntry> list, PagingPredicate pagingPredicate, Map.Entry<Integer, Map.Entry> nearestAnchorEntry) {
        if (pagingPredicate == null || list.isEmpty()) {
            return list;
        }
        Comparator<QueryableEntry> comparator = SortingUtil.newComparator(pagingPredicate);
        Collections.sort(list, comparator);
        int nearestPage = nearestAnchorEntry.getKey();
        int pageSize = pagingPredicate.getPageSize();
        int page = pagingPredicate.getPage();
        long totalSize = (long)pageSize * ((long)page - (long)nearestPage);
        if ((long)list.size() > totalSize) {
            list = list.subList(0, (int)totalSize);
        }
        return list;
    }

    public static ResultSet getSortedQueryResultSet(List<Map.Entry> list, PagingPredicate pagingPredicate, IterationType iterationType) {
        if (list.isEmpty()) {
            return new ResultSet();
        }
        Comparator<Map.Entry> comparator = SortingUtil.newComparator(pagingPredicate.getComparator(), iterationType);
        Collections.sort(list, comparator);
        Map.Entry<Integer, Map.Entry> nearestAnchorEntry = PagingPredicateAccessor.getNearestAnchorEntry(pagingPredicate);
        int nearestPage = nearestAnchorEntry.getKey();
        int page = pagingPredicate.getPage();
        int pageSize = pagingPredicate.getPageSize();
        long begin = (long)pageSize * ((long)page - (long)nearestPage - 1L);
        int size = list.size();
        if (begin > (long)size) {
            return new ResultSet();
        }
        long end = begin + (long)pageSize;
        if (end > (long)size) {
            end = size;
        }
        SortingUtil.setAnchor(list, pagingPredicate, nearestPage);
        List<Map.Entry> subList = list.subList((int)begin, (int)end);
        return new ResultSet(subList, iterationType);
    }

    public static boolean compareAnchor(PagingPredicate pagingPredicate, QueryableEntry queryEntry, Map.Entry<Integer, Map.Entry> nearestAnchorEntry) {
        IterationType iterationType;
        if (pagingPredicate == null) {
            return true;
        }
        Map.Entry anchor = nearestAnchorEntry.getValue();
        if (anchor == null) {
            return true;
        }
        Comparator<Map.Entry> comparator = pagingPredicate.getComparator();
        return SortingUtil.compare(comparator, iterationType = pagingPredicate.getIterationType(), anchor, queryEntry) < 0;
    }

    private static void setAnchor(List<Map.Entry> list, PagingPredicate pagingPredicate, int nearestPage) {
        if (list.isEmpty()) {
            return;
        }
        int size = list.size();
        int pageSize = pagingPredicate.getPageSize();
        int page = pagingPredicate.getPage();
        for (int i = pageSize; i <= size && nearestPage < page; i += pageSize) {
            Map.Entry anchor = list.get(i - 1);
            PagingPredicateAccessor.setAnchor(pagingPredicate, ++nearestPage, anchor);
        }
    }
}

