/*
 * Decompiled with CFR 0.152.
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
import org.outerj.daisy.diff.output.DiffOutput;
import org.outerj.daisy.diff.output.Differ;
import org.xml.sax.SAXException;

public class HTMLDiffer
implements Differ {
    private DiffOutput output;

    public HTMLDiffer(DiffOutput dm) {
        this.output = dm;
    }

    @Override
    public void diff(TextNodeComparator leftComparator, TextNodeComparator rightComparator) throws SAXException {
        this.diff(leftComparator, rightComparator, null);
    }

    @Override
    public void diff(TextNodeComparator leftComparator, TextNodeComparator rightComparator, IProgressMonitor progressMonitor) throws SAXException {
        LCSSettings settings = new LCSSettings();
        settings.setUseGreedyMethod(false);
        RangeDifference[] differences = RangeDifferencer.findDifferences(progressMonitor, settings, (IRangeComparator)leftComparator, (IRangeComparator)rightComparator);
        List<RangeDifference> pdifferences = this.preProcess(differences);
        int currentIndexLeft = 0;
        int currentIndexRight = 0;
        for (RangeDifference d : pdifferences) {
            if (d.leftStart() > currentIndexLeft) {
                rightComparator.handlePossibleChangedPart(currentIndexLeft, d.leftStart(), currentIndexRight, d.rightStart(), leftComparator, progressMonitor);
            }
            if (d.leftLength() > 0) {
                rightComparator.markAsDeleted(d.leftStart(), d.leftEnd(), leftComparator, d.rightStart());
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
            int leftStart = differences[i].leftStart();
            int leftEnd = differences[i].leftEnd();
            int rightStart = differences[i].rightStart();
            int rightEnd = differences[i].rightEnd();
            int kind = differences[i].kind();
            int leftLength = leftEnd - leftStart;
            int rightLength = rightEnd - rightStart;
            while (i + 1 < differences.length && differences[i + 1].kind() == kind) {
                int[] nArray = new int[]{leftLength, differences[i + 1].leftLength(), rightLength, differences[i + 1].rightLength()};
                if (!(HTMLDiffer.score(nArray) > (double)(differences[i + 1].leftStart() - leftEnd))) break;
                leftEnd = differences[i + 1].leftEnd();
                rightEnd = differences[i + 1].rightEnd();
                leftLength = leftEnd - leftStart;
                rightLength = rightEnd - rightStart;
                ++i;
            }
            newRanges.add(new RangeDifference(kind, rightStart, rightLength, leftStart, leftLength));
        }
        return newRanges;
    }

    public static double score(int ... numbers) {
        if (numbers[0] == 0 && numbers[1] == 0 || numbers[2] == 0 && numbers[3] == 0) {
            return 0.0;
        }
        double d = 0.0;
        int[] arr$ = numbers;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; ++i$) {
            double number;
            for (number = (double)arr$[i$]; number > 3.0; number *= 0.5) {
                d += 3.0;
                number -= 3.0;
            }
            d += number;
        }
        return d / (1.5 * (double)numbers.length);
    }
}

