/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.Assert
 *  org.eclipse.core.runtime.IProgressMonitor
 */
package org.eclipse.compare.rangedifferencer;

import java.util.ArrayList;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;

class OldDifferencer {
    private static final RangeDifference[] EMPTY_RESULT = new RangeDifference[0];

    OldDifferencer() {
    }

    public static RangeDifference[] findDifferences(IProgressMonitor pm, IRangeComparator left, IRangeComparator right) {
        int upper;
        int row;
        int diagLen;
        Assert.isTrue((boolean)right.getClass().equals(left.getClass()));
        int rightSize = right.getRangeCount();
        int leftSize = left.getRangeCount();
        int maxDiagonal = diagLen = 2 * Math.max(rightSize, leftSize);
        int[] lastDiagonal = new int[diagLen + 1];
        int origin = diagLen / 2;
        LinkedRangeDifference[] script = new LinkedRangeDifference[diagLen + 1];
        for (row = 0; row < rightSize && row < leftSize && OldDifferencer.rangesEqual(right, row, left, row); ++row) {
        }
        lastDiagonal[origin] = row;
        script[origin] = null;
        int lower = row == rightSize ? origin + 1 : origin - 1;
        int n = upper = row == leftSize ? origin - 1 : origin + 1;
        if (lower > upper) {
            return EMPTY_RESULT;
        }
        for (int d = 1; d <= maxDiagonal; ++d) {
            if (pm != null) {
                pm.worked(1);
            }
            if (right.skipRangeComparison(d, maxDiagonal, left)) {
                return EMPTY_RESULT;
            }
            for (int k = lower; k <= upper; k += 2) {
                int col;
                LinkedRangeDifference edit;
                if (pm != null && pm.isCanceled()) {
                    return EMPTY_RESULT;
                }
                if (k == origin - d || k != origin + d && lastDiagonal[k + 1] >= lastDiagonal[k - 1]) {
                    row = lastDiagonal[k + 1] + 1;
                    edit = new LinkedRangeDifference(script[k + 1], 1);
                } else {
                    row = lastDiagonal[k - 1];
                    edit = new LinkedRangeDifference(script[k - 1], 0);
                }
                edit.fRightStart = row;
                edit.fLeftStart = col;
                Assert.isTrue((k >= 0 && k <= maxDiagonal ? 1 : 0) != 0);
                script[k] = edit;
                for (col = row + k - origin; row < rightSize && col < leftSize && OldDifferencer.rangesEqual(right, row, left, col); ++row, ++col) {
                }
                Assert.isTrue((k >= 0 && k <= maxDiagonal ? 1 : 0) != 0);
                lastDiagonal[k] = row;
                if (row == rightSize && col == leftSize) {
                    return OldDifferencer.createDifferencesRanges(script[k]);
                }
                if (row == rightSize) {
                    lower = k + 2;
                }
                if (col != leftSize) continue;
                upper = k - 2;
            }
            --lower;
            ++upper;
        }
        Assert.isTrue((boolean)false);
        return null;
    }

    private static boolean rangesEqual(IRangeComparator a, int ai, IRangeComparator b, int bi) {
        return a.rangesEqual(ai, b, bi);
    }

    private static RangeDifference[] createDifferencesRanges(LinkedRangeDifference start) {
        LinkedRangeDifference ep = OldDifferencer.reverseDifferences(start);
        ArrayList<RangeDifference> result = new ArrayList<RangeDifference>();
        RangeDifference es = null;
        while (ep != null) {
            es = new RangeDifference(2);
            if (ep.isInsert()) {
                es.fRightStart = ep.fRightStart + 1;
                es.fLeftStart = ep.fLeftStart;
                LinkedRangeDifference b = ep;
                do {
                    ep = ep.getNext();
                    ++es.fLeftLength;
                } while (ep != null && ep.isInsert() && ep.fRightStart == b.fRightStart);
            } else {
                boolean change;
                es.fRightStart = ep.fRightStart;
                es.fLeftStart = ep.fLeftStart;
                LinkedRangeDifference a = ep;
                do {
                    a = ep;
                    ep = ep.getNext();
                    ++es.fRightLength;
                } while (ep != null && ep.isDelete() && ep.fRightStart == a.fRightStart + 1);
                boolean bl = change = ep != null && ep.isInsert() && ep.fRightStart == a.fRightStart;
                if (change) {
                    LinkedRangeDifference b = ep;
                    do {
                        ep = ep.getNext();
                        ++es.fLeftLength;
                    } while (ep != null && ep.isInsert() && ep.fRightStart == b.fRightStart);
                } else {
                    es.fLeftLength = 0;
                }
                ++es.fLeftStart;
            }
            --es.fRightStart;
            --es.fLeftStart;
            result.add(es);
        }
        return result.toArray(EMPTY_RESULT);
    }

    private static LinkedRangeDifference reverseDifferences(LinkedRangeDifference start) {
        LinkedRangeDifference ep = null;
        for (LinkedRangeDifference ahead = start; ahead != null; ahead = ahead.getNext()) {
            LinkedRangeDifference behind = ep;
            ep = ahead;
            ep.setNext(behind);
        }
        return ep;
    }

    private static class LinkedRangeDifference
    extends RangeDifference {
        static final int INSERT = 0;
        static final int DELETE = 1;
        LinkedRangeDifference fNext;

        LinkedRangeDifference() {
            super(5);
            this.fNext = null;
        }

        LinkedRangeDifference(LinkedRangeDifference next, int operation) {
            super(operation);
            this.fNext = next;
        }

        LinkedRangeDifference getNext() {
            return this.fNext;
        }

        boolean isDelete() {
            return this.kind() == 1;
        }

        boolean isInsert() {
            return this.kind() == 0;
        }

        void setNext(LinkedRangeDifference next) {
            this.fNext = next;
        }
    }
}

