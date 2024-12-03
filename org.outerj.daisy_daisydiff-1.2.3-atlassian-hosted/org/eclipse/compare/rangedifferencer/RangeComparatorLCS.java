/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.IProgressMonitor
 *  org.eclipse.core.runtime.OperationCanceledException
 *  org.eclipse.core.runtime.SubMonitor
 */
package org.eclipse.compare.rangedifferencer;

import java.util.ArrayList;
import org.eclipse.compare.internal.CompareMessages;
import org.eclipse.compare.internal.LCS;
import org.eclipse.compare.internal.LCSSettings;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

class RangeComparatorLCS
extends LCS {
    private final IRangeComparator comparator1;
    private final IRangeComparator comparator2;
    private static final RangeDifference[] EMPTY_RESULT = new RangeDifference[0];
    private int[][] lcs;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RangeDifference[] findDifferences(IProgressMonitor pm, LCSSettings settings, IRangeComparator left, IRangeComparator right) {
        RangeComparatorLCS lcs = new RangeComparatorLCS(left, right);
        SubMonitor monitor = SubMonitor.convert((IProgressMonitor)pm, (String)CompareMessages.RangeComparatorLCS_0, (int)100);
        try {
            lcs.longestCommonSubsequence(monitor.newChild(95), settings);
            RangeDifference[] rangeDifferenceArray = lcs.getDifferences(monitor.newChild(5));
            return rangeDifferenceArray;
        }
        finally {
            if (pm != null) {
                pm.done();
            }
        }
    }

    public RangeComparatorLCS(IRangeComparator comparator1, IRangeComparator comparator2) {
        this.comparator1 = comparator1;
        this.comparator2 = comparator2;
    }

    @Override
    protected int getLength1() {
        return this.comparator1.getRangeCount();
    }

    @Override
    protected int getLength2() {
        return this.comparator2.getRangeCount();
    }

    @Override
    protected void initializeLcs(int lcsLength) {
        this.lcs = new int[2][lcsLength];
    }

    @Override
    protected boolean isRangeEqual(int i1, int i2) {
        return this.comparator1.rangesEqual(i1, this.comparator2, i2);
    }

    @Override
    protected void setLcs(int sl1, int sl2) {
        this.lcs[0][sl1] = sl1 + 1;
        this.lcs[1][sl1] = sl2 + 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RangeDifference[] getDifferences(SubMonitor subMonitor) {
        try {
            ArrayList<RangeDifference> differences = new ArrayList<RangeDifference>();
            int length = this.getLength();
            if (length == 0) {
                differences.add(new RangeDifference(2, 0, this.comparator2.getRangeCount(), 0, this.comparator1.getRangeCount()));
            } else {
                subMonitor.beginTask(null, length);
                int index2 = 0;
                int s1 = -1;
                int s2 = -1;
                for (int index1 = 0; index1 < this.lcs[0].length && index2 < this.lcs[1].length; ++index1, ++index2) {
                    int l2;
                    int l1;
                    if (subMonitor != null && subMonitor.isCanceled()) {
                        RangeDifference[] rangeDifferenceArray = EMPTY_RESULT;
                        return rangeDifferenceArray;
                    }
                    while ((l1 = this.lcs[0][index1]) == 0 && ++index1 < this.lcs[0].length) {
                    }
                    if (index1 >= this.lcs[0].length) break;
                    while ((l2 = this.lcs[1][index2]) == 0 && ++index2 < this.lcs[1].length) {
                    }
                    if (index2 >= this.lcs[1].length) break;
                    int end1 = l1 - 1;
                    int end2 = l2 - 1;
                    if (s1 == -1 && (end1 != 0 || end2 != 0)) {
                        differences.add(new RangeDifference(2, 0, end2, 0, end1));
                    } else if (end1 != s1 + 1 || end2 != s2 + 1) {
                        int leftStart = s1 + 1;
                        int leftLength = end1 - leftStart;
                        int rightStart = s2 + 1;
                        int rightLength = end2 - rightStart;
                        differences.add(new RangeDifference(2, rightStart, rightLength, leftStart, leftLength));
                    }
                    s1 = end1;
                    s2 = end2;
                    this.worked(subMonitor, 1);
                }
                if (s1 != -1 && (s1 + 1 < this.comparator1.getRangeCount() || s2 + 1 < this.comparator2.getRangeCount())) {
                    int leftStart = s1 < this.comparator1.getRangeCount() ? s1 + 1 : s1;
                    int rightStart = s2 < this.comparator2.getRangeCount() ? s2 + 1 : s2;
                    differences.add(new RangeDifference(2, rightStart, this.comparator2.getRangeCount() - (s2 + 1), leftStart, this.comparator1.getRangeCount() - (s1 + 1)));
                }
            }
            RangeDifference[] rangeDifferenceArray = differences.toArray(new RangeDifference[differences.size()]);
            return rangeDifferenceArray;
        }
        finally {
            subMonitor.done();
        }
    }

    private void worked(SubMonitor subMonitor, int work) {
        if (subMonitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        subMonitor.worked(work);
    }
}

