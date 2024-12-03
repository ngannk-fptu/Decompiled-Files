/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.tag;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.outerj.daisy.diff.output.TextDiffOutput;
import org.outerj.daisy.diff.output.TextDiffer;
import org.outerj.daisy.diff.tag.ArgumentComparator;
import org.outerj.daisy.diff.tag.DelimiterAtom;
import org.outerj.daisy.diff.tag.IAtomSplitter;

public class TagDiffer
implements TextDiffer {
    private TextDiffOutput output;

    public TagDiffer(TextDiffOutput output) {
        this.output = output;
    }

    @Override
    public void diff(IAtomSplitter leftComparator, IAtomSplitter rightComparator) throws Exception {
        RangeDifference[] differences = RangeDifferencer.findDifferences(leftComparator, rightComparator);
        List<RangeDifference> pdifferences = this.preProcess(differences, leftComparator);
        int rightAtom = 0;
        int leftAtom = 0;
        for (int i = 0; i < pdifferences.size(); ++i) {
            this.parseNoChange(leftAtom, pdifferences.get(i).leftStart(), rightAtom, pdifferences.get(i).rightStart(), leftComparator, rightComparator);
            String leftString = leftComparator.substring(pdifferences.get(i).leftStart(), pdifferences.get(i).leftEnd());
            String rightString = rightComparator.substring(pdifferences.get(i).rightStart(), pdifferences.get(i).rightEnd());
            if (pdifferences.get(i).leftLength() > 0) {
                this.output.addRemovedPart(leftString);
            }
            if (pdifferences.get(i).rightLength() > 0) {
                this.output.addAddedPart(rightString);
            }
            rightAtom = pdifferences.get(i).rightEnd();
            leftAtom = pdifferences.get(i).leftEnd();
        }
        if (rightAtom < rightComparator.getRangeCount()) {
            this.parseNoChange(leftAtom, leftComparator.getRangeCount(), rightAtom, rightComparator.getRangeCount(), leftComparator, rightComparator);
        }
    }

    private void parseNoChange(int beginLeft, int endLeft, int beginRight, int endRight, IAtomSplitter leftComparator, IAtomSplitter rightComparator) throws Exception {
        StringBuilder sb = new StringBuilder();
        while (beginLeft < endLeft) {
            while (beginLeft < endLeft && !rightComparator.getAtom(beginRight).hasInternalIdentifiers() && !leftComparator.getAtom(beginLeft).hasInternalIdentifiers()) {
                sb.append(rightComparator.getAtom(beginRight).getFullText());
                ++beginRight;
                ++beginLeft;
            }
            if (sb.length() > 0) {
                this.output.addClearPart(sb.toString());
                sb.setLength(0);
            }
            if (beginLeft >= endLeft) continue;
            ArgumentComparator leftComparator2 = new ArgumentComparator(leftComparator.getAtom(beginLeft).getFullText());
            ArgumentComparator rightComparator2 = new ArgumentComparator(rightComparator.getAtom(beginRight).getFullText());
            RangeDifference[] differences2 = RangeDifferencer.findDifferences(leftComparator2, rightComparator2);
            List<RangeDifference> pdifferences2 = this.preProcess(differences2, 2);
            int rightAtom2 = 0;
            for (int j = 0; j < pdifferences2.size(); ++j) {
                if (rightAtom2 < pdifferences2.get(j).rightStart()) {
                    this.output.addClearPart(rightComparator2.substring(rightAtom2, pdifferences2.get(j).rightStart()));
                }
                if (pdifferences2.get(j).leftLength() > 0) {
                    this.output.addRemovedPart(leftComparator2.substring(pdifferences2.get(j).leftStart(), pdifferences2.get(j).leftEnd()));
                }
                if (pdifferences2.get(j).rightLength() > 0) {
                    this.output.addAddedPart(rightComparator2.substring(pdifferences2.get(j).rightStart(), pdifferences2.get(j).rightEnd()));
                }
                rightAtom2 = pdifferences2.get(j).rightEnd();
            }
            if (rightAtom2 < rightComparator2.getRangeCount()) {
                this.output.addClearPart(rightComparator2.substring(rightAtom2));
            }
            ++beginLeft;
            ++beginRight;
        }
    }

    private List<RangeDifference> preProcess(RangeDifference[] differences, IAtomSplitter leftComparator) {
        LinkedList<RangeDifference> newRanges = new LinkedList<RangeDifference>();
        for (int i = 0; i < differences.length; ++i) {
            int leftStart = differences[i].leftStart();
            int leftEnd = differences[i].leftEnd();
            int rightStart = differences[i].rightStart();
            int rightEnd = differences[i].rightEnd();
            int kind = differences[i].kind();
            int temp = leftEnd;
            boolean connecting = true;
            while (connecting && i + 1 < differences.length && differences[i + 1].kind() == kind) {
                int bridgelength = 0;
                int nbtokens = Math.max(leftEnd - leftStart, rightEnd - rightStart);
                if (nbtokens > 5) {
                    bridgelength = nbtokens > 10 ? 3 : 2;
                }
                while ((leftComparator.getAtom(temp) instanceof DelimiterAtom || bridgelength-- > 0) && temp < differences[i + 1].leftStart()) {
                    ++temp;
                }
                if (temp == differences[i + 1].leftStart()) {
                    leftEnd = differences[i + 1].leftEnd();
                    rightEnd = differences[i + 1].rightEnd();
                    temp = leftEnd;
                    ++i;
                    continue;
                }
                connecting = false;
                if (leftComparator.getAtom(temp) instanceof DelimiterAtom || !leftComparator.getAtom(temp).getFullText().equals(" ")) continue;
                throw new IllegalStateException("space found aiaiai");
            }
            newRanges.add(new RangeDifference(kind, rightStart, rightEnd - rightStart, leftStart, leftEnd - leftStart));
        }
        return newRanges;
    }

    private List<RangeDifference> preProcess(RangeDifference[] differences, int span) {
        LinkedList<RangeDifference> newRanges = new LinkedList<RangeDifference>();
        for (int i = 0; i < differences.length; ++i) {
            int leftStart = differences[i].leftStart();
            int leftEnd = differences[i].leftEnd();
            int rightStart = differences[i].rightStart();
            int rightEnd = differences[i].rightEnd();
            int kind = differences[i].kind();
            while (i + 1 < differences.length && differences[i + 1].kind() == kind && differences[i + 1].leftStart() <= leftEnd + span && differences[i + 1].rightStart() <= rightEnd + span) {
                leftEnd = differences[i + 1].leftEnd();
                rightEnd = differences[i + 1].rightEnd();
                ++i;
            }
            newRanges.add(new RangeDifference(kind, rightStart, rightEnd - rightStart, leftStart, leftEnd - leftStart));
        }
        return newRanges;
    }
}

