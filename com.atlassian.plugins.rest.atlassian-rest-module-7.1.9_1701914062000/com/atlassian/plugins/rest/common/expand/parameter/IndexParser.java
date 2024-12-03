/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.rest.common.expand.parameter;

import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

final class IndexParser {
    private static final String INDEX = "-?\\d+";
    private static final String RANGE = "(?:-?\\d+)?:(?:-?\\d+)?";
    private static final Pattern INDEX_PATTERN = Pattern.compile("-?\\d+");
    private static final Pattern RANGE_PATTERN = Pattern.compile("(?:-?\\d+)?:(?:-?\\d+)?");
    public static final Indexes ALL = new RangeIndexes(null, null);
    public static final Indexes EMPTY = new EmptyIndexes();

    private IndexParser() {
    }

    static Indexes parse(String indexes) {
        if (StringUtils.isBlank((CharSequence)indexes)) {
            return ALL;
        }
        if (INDEX_PATTERN.matcher(indexes).matches()) {
            return new SimpleIndexes(Integer.parseInt(indexes));
        }
        if (RANGE_PATTERN.matcher(indexes).matches()) {
            String leftAsString = StringUtils.substringBefore((String)indexes, (String)":");
            String rightAsString = StringUtils.substringAfter((String)indexes, (String)":");
            return new RangeIndexes(StringUtils.isNotBlank((CharSequence)leftAsString) ? Integer.valueOf(Integer.parseInt(leftAsString)) : null, StringUtils.isNotBlank((CharSequence)rightAsString) ? Integer.valueOf(Integer.parseInt(rightAsString)) : null);
        }
        return EMPTY;
    }

    private static int toPositiveIndex(int i, int size) {
        return i < 0 ? i + size : i;
    }

    private static boolean isInBound(int i, int size) {
        int p = IndexParser.toPositiveIndex(i, size);
        return p >= 0 && p < size;
    }

    private static class EmptyIndexes
    implements Indexes {
        private EmptyIndexes() {
        }

        @Override
        public boolean isRange() {
            return false;
        }

        @Override
        public int getMinIndex(int size) {
            return -1;
        }

        @Override
        public int getMaxIndex(int size) {
            return -1;
        }

        @Override
        public boolean contains(int index, int size) {
            return false;
        }

        @Override
        public SortedSet<Integer> getIndexes(int size) {
            return Sets.newTreeSet();
        }
    }

    static class RangeIndexes
    implements Indexes {
        private final Integer left;
        private final Integer right;

        RangeIndexes(Integer left, Integer right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean isRange() {
            return true;
        }

        @Override
        public int getMinIndex(int size) {
            return this.actualLeft(size);
        }

        @Override
        public int getMaxIndex(int size) {
            return this.actualRight(size);
        }

        @Override
        public boolean contains(int index, int size) {
            if (!IndexParser.isInBound(index, size)) {
                return false;
            }
            int p = IndexParser.toPositiveIndex(index, size);
            return p >= this.actualLeft(size) && p <= this.actualRight(size);
        }

        @Override
        public SortedSet<Integer> getIndexes(int size) {
            TreeSet allIndexes = Sets.newTreeSet();
            int actualLeft = this.actualLeft(size);
            int actualRight = this.actualRight(size);
            if (actualLeft != -1 && actualRight != -1) {
                for (int i = actualLeft; i <= actualRight; ++i) {
                    allIndexes.add(i);
                }
            }
            return allIndexes;
        }

        private int actualLeft(int size) {
            if (size == 0) {
                return -1;
            }
            if (this.left == null) {
                return 0;
            }
            int positiveLeft = IndexParser.toPositiveIndex(this.left, size);
            if (positiveLeft < 0) {
                return 0;
            }
            if (positiveLeft >= size) {
                return -1;
            }
            return positiveLeft;
        }

        private int actualRight(int size) {
            if (size == 0) {
                return -1;
            }
            if (this.right == null) {
                return size - 1;
            }
            int positiveRight = IndexParser.toPositiveIndex(this.right, size);
            if (positiveRight < 0) {
                return -1;
            }
            if (positiveRight >= size - 1) {
                return size - 1;
            }
            return positiveRight;
        }
    }

    static class SimpleIndexes
    implements Indexes {
        private final int index;

        SimpleIndexes(int index) {
            this.index = index;
        }

        @Override
        public boolean isRange() {
            return false;
        }

        @Override
        public int getMinIndex(int size) {
            return this.getIndex(size);
        }

        @Override
        public int getMaxIndex(int size) {
            return this.getIndex(size);
        }

        private int getIndex(int size) {
            return IndexParser.isInBound(this.index, size) ? IndexParser.toPositiveIndex(this.index, size) : -1;
        }

        @Override
        public boolean contains(int i, int size) {
            return IndexParser.isInBound(this.index, size) && IndexParser.toPositiveIndex(this.index, size) == i;
        }

        @Override
        public SortedSet<Integer> getIndexes(int size) {
            return IndexParser.isInBound(this.index, size) ? Sets.newTreeSet(Arrays.asList(IndexParser.toPositiveIndex(this.index, size))) : Sets.newTreeSet();
        }
    }
}

