/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.IProgressMonitor
 */
package org.outerj.daisy.diff.html;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.compare.internal.LCSSettings;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.modification.ModificationType;
import org.outerj.daisy.diff.output.DiffOutput;
import org.outerj.daisy.diff.output.Differ;
import org.xml.sax.SAXException;

public class HTMLDiffer
implements Differ {
    private DiffOutput output;

    public HTMLDiffer(DiffOutput dm) {
        this.output = dm;
    }

    public void diff(TextNodeComparator ancestorComparator, TextNodeComparator leftComparator, TextNodeComparator rightComparator, IProgressMonitor progressMonitor, boolean useGreedyMethod) throws SAXException {
        LCSSettings settings = new LCSSettings();
        settings.setUseGreedyMethod(useGreedyMethod);
        RangeDifference[] differences = RangeDifferencer.findDifferences(settings, ancestorComparator, (IRangeComparator)leftComparator, (IRangeComparator)rightComparator);
        List<RangeDifference> pdifferences = this.preProcess(differences);
        int currentIndexAncestor = 0;
        int currentIndexLeft = 0;
        int currentIndexRight = 0;
        for (RangeDifference d : pdifferences) {
            int tempKind = d.kind();
            if (tempKind == 4) continue;
            if (d.leftStart() > currentIndexLeft) {
                ancestorComparator.handlePossibleChangedPart(currentIndexLeft, d.leftStart(), currentIndexAncestor, d.ancestorStart(), leftComparator, progressMonitor);
            }
            if (d.rightStart() > currentIndexRight) {
                ancestorComparator.handlePossibleChangedPart(currentIndexRight, d.rightStart(), currentIndexAncestor, d.ancestorStart(), rightComparator, progressMonitor);
            }
            if ((tempKind == 1 || tempKind == 3) && d.leftLength() > 0) {
                ancestorComparator.markAsDeleted(d.leftStart(), d.leftEnd(), leftComparator, d.ancestorStart(), d.ancestorEnd(), ModificationType.ADDED);
            }
            if ((tempKind == 1 || tempKind == 2) && d.rightLength() > 0) {
                ancestorComparator.markAsDeleted(d.rightStart(), d.rightEnd(), rightComparator, d.ancestorStart(), d.ancestorEnd(), ModificationType.ADDED);
            }
            ancestorComparator.markAsNew(d.ancestorStart(), d.ancestorEnd(), ModificationType.REMOVED);
            currentIndexAncestor = d.ancestorEnd();
            currentIndexLeft = d.leftEnd();
            currentIndexRight = d.rightEnd();
        }
        if (currentIndexLeft < leftComparator.getRangeCount()) {
            ancestorComparator.handlePossibleChangedPart(currentIndexLeft, leftComparator.getRangeCount(), currentIndexAncestor, ancestorComparator.getRangeCount(), leftComparator, progressMonitor);
        }
        if (currentIndexRight < rightComparator.getRangeCount()) {
            ancestorComparator.handlePossibleChangedPart(currentIndexRight, rightComparator.getRangeCount(), currentIndexAncestor, ancestorComparator.getRangeCount(), rightComparator, progressMonitor);
        }
        ancestorComparator.expandWhiteSpace();
        this.output.generateOutput(ancestorComparator.getBodyNode());
    }

    @Override
    public void diff(TextNodeComparator leftComparator, TextNodeComparator rightComparator) throws SAXException {
        this.diff(leftComparator, rightComparator, null, false);
    }

    @Override
    public void diff(TextNodeComparator leftComparator, TextNodeComparator rightComparator, IProgressMonitor progressMonitor) throws SAXException {
        this.diff(leftComparator, rightComparator, progressMonitor, false);
    }

    @Override
    public void diff(TextNodeComparator leftComparator, TextNodeComparator rightComparator, IProgressMonitor progressMonitor, boolean useGreedyMethod) throws SAXException {
        LCSSettings settings = new LCSSettings();
        settings.setUseGreedyMethod(useGreedyMethod);
        RangeDifference[] differences = RangeDifferencer.findDifferences(progressMonitor, settings, (IRangeComparator)leftComparator, (IRangeComparator)rightComparator);
        List<RangeDifference> pdifferences = this.preProcess(differences);
        int currentIndexLeft = 0;
        int currentIndexRight = 0;
        for (RangeDifference d : pdifferences) {
            if (d.leftStart() > currentIndexLeft) {
                rightComparator.handlePossibleChangedPart(currentIndexLeft, d.leftStart(), currentIndexRight, d.rightStart(), leftComparator, progressMonitor);
            }
            if (d.leftLength() > 0) {
                rightComparator.markAsDeleted(d.leftStart(), d.leftEnd(), leftComparator, d.rightStart(), d.rightEnd());
            }
            rightComparator.markAsNew(d.rightStart(), d.rightEnd());
            currentIndexLeft = d.leftEnd();
            currentIndexRight = d.rightEnd();
        }
        if (currentIndexLeft < leftComparator.getRangeCount()) {
            rightComparator.handlePossibleChangedPart(currentIndexLeft, leftComparator.getRangeCount(), currentIndexRight, rightComparator.getRangeCount(), leftComparator, progressMonitor);
        }
        rightComparator.expandWhiteSpace();
        this.output.generateOutput(rightComparator.getBodyNode());
    }

    private List<RangeDifference> preProcess(RangeDifference[] differences) {
        LinkedList<RangeDifference> newRanges = new LinkedList<RangeDifference>();
        for (int i = 0; i < differences.length; ++i) {
            int ancestorStart = differences[i].ancestorStart();
            int ancestorEnd = differences[i].ancestorEnd();
            int leftStart = differences[i].leftStart();
            int leftEnd = differences[i].leftEnd();
            int rightStart = differences[i].rightStart();
            int rightEnd = differences[i].rightEnd();
            int kind = differences[i].kind();
            int ancestorLength = ancestorEnd - ancestorStart;
            int leftLength = leftEnd - leftStart;
            int rightLength = rightEnd - rightStart;
            while (i + 1 < differences.length && differences[i + 1].kind() == kind) {
                int[] nArray = new int[]{leftLength, differences[i + 1].leftLength(), rightLength, differences[i + 1].rightLength()};
                if (!(HTMLDiffer.score(nArray) > (double)(differences[i + 1].leftStart() - leftEnd))) break;
                leftEnd = differences[i + 1].leftEnd();
                rightEnd = differences[i + 1].rightEnd();
                ancestorEnd = differences[i + 1].ancestorEnd();
                leftLength = leftEnd - leftStart;
                rightLength = rightEnd - rightStart;
                ancestorLength = ancestorEnd - ancestorStart;
                ++i;
            }
            newRanges.add(new RangeDifference(kind, rightStart, rightLength, leftStart, leftLength, ancestorStart, ancestorLength));
        }
        return newRanges;
    }

    public static double score(int ... numbers) {
        if (numbers[0] == 0 && numbers[1] == 0 || numbers[2] == 0 && numbers[3] == 0) {
            return 0.0;
        }
        double d = 0.0;
        int[] nArray = numbers;
        int n = nArray.length;
        for (int i = 0; i < n; ++i) {
            double number;
            for (number = (double)nArray[i]; number > 3.0; number *= 0.5) {
                d += 3.0;
                number -= 3.0;
            }
            d += number;
        }
        return d / (1.5 * (double)numbers.length);
    }
}

