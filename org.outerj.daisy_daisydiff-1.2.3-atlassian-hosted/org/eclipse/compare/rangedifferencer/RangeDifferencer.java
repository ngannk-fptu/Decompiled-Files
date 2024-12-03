/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.Assert
 *  org.eclipse.core.runtime.IProgressMonitor
 *  org.eclipse.core.runtime.SubMonitor
 */
package org.eclipse.compare.rangedifferencer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.compare.internal.CompareMessages;
import org.eclipse.compare.internal.LCSSettings;
import org.eclipse.compare.rangedifferencer.DifferencesIterator;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.OldDifferencer;
import org.eclipse.compare.rangedifferencer.RangeComparatorLCS;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public final class RangeDifferencer {
    private static final RangeDifference[] EMPTY_RESULT = new RangeDifference[0];

    private RangeDifferencer() {
    }

    public static RangeDifference[] findDifferences(LCSSettings settings, IRangeComparator left, IRangeComparator right) {
        return RangeDifferencer.findDifferences((IProgressMonitor)null, settings, left, right);
    }

    public static RangeDifference[] findDifferences(IRangeComparator left, IRangeComparator right) {
        return RangeDifferencer.findDifferences((IProgressMonitor)null, new LCSSettings(), left, right);
    }

    public static RangeDifference[] findDifferences(IProgressMonitor pm, LCSSettings settings, IRangeComparator left, IRangeComparator right) {
        if (!settings.isUseGreedyMethod()) {
            return OldDifferencer.findDifferences(pm, left, right);
        }
        return RangeComparatorLCS.findDifferences(pm, settings, left, right);
    }

    public static RangeDifference[] findDifferences(LCSSettings settings, IRangeComparator ancestor, IRangeComparator left, IRangeComparator right) {
        return RangeDifferencer.findDifferences(null, settings, ancestor, left, right);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RangeDifference[] findDifferences(IProgressMonitor pm, LCSSettings settings, IRangeComparator ancestor, IRangeComparator left, IRangeComparator right) {
        try {
            if (ancestor == null) {
                RangeDifference[] rangeDifferenceArray = RangeDifferencer.findDifferences(pm, settings, left, right);
                return rangeDifferenceArray;
            }
            SubMonitor monitor = SubMonitor.convert((IProgressMonitor)pm, (String)CompareMessages.RangeComparatorLCS_0, (int)100);
            RangeDifference[] leftAncestorScript = null;
            RangeDifference[] rightAncestorScript = RangeDifferencer.findDifferences((IProgressMonitor)monitor.newChild(50), settings, ancestor, right);
            if (rightAncestorScript != null) {
                monitor.setWorkRemaining(100);
                leftAncestorScript = RangeDifferencer.findDifferences((IProgressMonitor)monitor.newChild(50), settings, ancestor, left);
            }
            if (rightAncestorScript == null || leftAncestorScript == null) {
                RangeDifference[] rangeDifferenceArray = null;
                return rangeDifferenceArray;
            }
            DifferencesIterator myIter = new DifferencesIterator(rightAncestorScript);
            DifferencesIterator yourIter = new DifferencesIterator(leftAncestorScript);
            ArrayList<RangeDifference> diff3 = new ArrayList<RangeDifference>();
            diff3.add(new RangeDifference(5));
            int changeRangeStart = 0;
            int changeRangeEnd = 0;
            monitor.setWorkRemaining(rightAncestorScript.length + leftAncestorScript.length);
            while (myIter.fDifference != null || yourIter.fDifference != null) {
                myIter.removeAll();
                yourIter.removeAll();
                DifferencesIterator startThread = myIter.fDifference == null ? yourIter : (yourIter.fDifference == null ? myIter : (myIter.fDifference.fLeftStart <= yourIter.fDifference.fLeftStart ? myIter : yourIter));
                changeRangeStart = startThread.fDifference.fLeftStart;
                changeRangeEnd = startThread.fDifference.leftEnd();
                startThread.next();
                monitor.worked(1);
                DifferencesIterator other = startThread.other(myIter, yourIter);
                while (other.fDifference != null && other.fDifference.fLeftStart <= changeRangeEnd) {
                    int newMax = other.fDifference.leftEnd();
                    other.next();
                    monitor.worked(1);
                    if (newMax < changeRangeEnd) continue;
                    changeRangeEnd = newMax;
                    other = other.other(myIter, yourIter);
                }
                diff3.add(RangeDifferencer.createRangeDifference3(myIter, yourIter, diff3, right, left, changeRangeStart, changeRangeEnd));
            }
            diff3.remove(0);
            RangeDifference[] rangeDifferenceArray = diff3.toArray(EMPTY_RESULT);
            return rangeDifferenceArray;
        }
        finally {
            if (pm != null) {
                pm.done();
            }
        }
    }

    public static RangeDifference[] findRanges(LCSSettings settings, IRangeComparator left, IRangeComparator right) {
        return RangeDifferencer.findRanges((IProgressMonitor)null, settings, left, right);
    }

    public static RangeDifference[] findRanges(IProgressMonitor pm, LCSSettings settings, IRangeComparator left, IRangeComparator right) {
        RangeDifference rd;
        RangeDifference[] in = RangeDifferencer.findDifferences(pm, settings, left, right);
        ArrayList<RangeDifference> out = new ArrayList<RangeDifference>();
        int mstart = 0;
        int ystart = 0;
        for (int i = 0; i < in.length; ++i) {
            RangeDifference es = in[i];
            rd = new RangeDifference(0, mstart, es.rightStart() - mstart, ystart, es.leftStart() - ystart);
            if (rd.maxLength() != 0) {
                out.add(rd);
            }
            out.add(es);
            mstart = es.rightEnd();
            ystart = es.leftEnd();
        }
        rd = new RangeDifference(0, mstart, right.getRangeCount() - mstart, ystart, left.getRangeCount() - ystart);
        if (rd.maxLength() > 0) {
            out.add(rd);
        }
        return out.toArray(EMPTY_RESULT);
    }

    public static RangeDifference[] findRanges(LCSSettings settings, IRangeComparator ancestor, IRangeComparator left, IRangeComparator right) {
        return RangeDifferencer.findRanges(null, settings, ancestor, left, right);
    }

    public static RangeDifference[] findRanges(IProgressMonitor pm, LCSSettings settings, IRangeComparator ancestor, IRangeComparator left, IRangeComparator right) {
        RangeDifference rd;
        if (ancestor == null) {
            return RangeDifferencer.findRanges(pm, settings, left, right);
        }
        RangeDifference[] in = RangeDifferencer.findDifferences(pm, settings, ancestor, left, right);
        ArrayList<RangeDifference> out = new ArrayList<RangeDifference>();
        int mstart = 0;
        int ystart = 0;
        int astart = 0;
        for (int i = 0; i < in.length; ++i) {
            RangeDifference es = in[i];
            rd = new RangeDifference(0, mstart, es.rightStart() - mstart, ystart, es.leftStart() - ystart, astart, es.ancestorStart() - astart);
            if (rd.maxLength() > 0) {
                out.add(rd);
            }
            out.add(es);
            mstart = es.rightEnd();
            ystart = es.leftEnd();
            astart = es.ancestorEnd();
        }
        rd = new RangeDifference(0, mstart, right.getRangeCount() - mstart, ystart, left.getRangeCount() - ystart, astart, ancestor.getRangeCount() - astart);
        if (rd.maxLength() > 0) {
            out.add(rd);
        }
        return out.toArray(EMPTY_RESULT);
    }

    private static RangeDifference createRangeDifference3(DifferencesIterator myIter, DifferencesIterator yourIter, List diff3, IRangeComparator right, IRangeComparator left, int changeRangeStart, int changeRangeEnd) {
        int leftEnd;
        int leftStart;
        RangeDifference l;
        RangeDifference f;
        int rightEnd;
        int rightStart;
        int kind = 5;
        RangeDifference last = (RangeDifference)diff3.get(diff3.size() - 1);
        Assert.isTrue((myIter.getCount() != 0 || yourIter.getCount() != 0 ? 1 : 0) != 0);
        if (myIter.getCount() == 0) {
            rightStart = changeRangeStart - last.ancestorEnd() + last.rightEnd();
            rightEnd = changeRangeEnd - last.ancestorEnd() + last.rightEnd();
            kind = 3;
        } else {
            f = (RangeDifference)myIter.fRange.get(0);
            l = (RangeDifference)myIter.fRange.get(myIter.fRange.size() - 1);
            rightStart = changeRangeStart - f.fLeftStart + f.fRightStart;
            rightEnd = changeRangeEnd - l.leftEnd() + l.rightEnd();
        }
        if (yourIter.getCount() == 0) {
            leftStart = changeRangeStart - last.ancestorEnd() + last.leftEnd();
            leftEnd = changeRangeEnd - last.ancestorEnd() + last.leftEnd();
            kind = 2;
        } else {
            f = (RangeDifference)yourIter.fRange.get(0);
            l = (RangeDifference)yourIter.fRange.get(yourIter.fRange.size() - 1);
            leftStart = changeRangeStart - f.fLeftStart + f.fRightStart;
            leftEnd = changeRangeEnd - l.leftEnd() + l.rightEnd();
        }
        if (kind == 5) {
            kind = RangeDifferencer.rangeSpansEqual(right, rightStart, rightEnd - rightStart, left, leftStart, leftEnd - leftStart) ? 4 : 1;
        }
        return new RangeDifference(kind, rightStart, rightEnd - rightStart, leftStart, leftEnd - leftStart, changeRangeStart, changeRangeEnd - changeRangeStart);
    }

    private static boolean rangeSpansEqual(IRangeComparator right, int rightStart, int rightLen, IRangeComparator left, int leftStart, int leftLen) {
        if (rightLen == leftLen) {
            int i = 0;
            for (i = 0; i < rightLen && RangeDifferencer.rangesEqual(right, rightStart + i, left, leftStart + i); ++i) {
            }
            if (i == rightLen) {
                return true;
            }
        }
        return false;
    }

    private static boolean rangesEqual(IRangeComparator a, int ai, IRangeComparator b, int bi) {
        return a.rangesEqual(ai, b, bi);
    }
}

