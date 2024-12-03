/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.ancestor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.compare.internal.LCSSettings;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.outerj.daisy.diff.html.dom.Node;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.dom.TextNode;

public class TextOnlyComparator
implements IRangeComparator {
    private List<TextNode> leafs = new ArrayList<TextNode>();

    public TextOnlyComparator(TagNode tree) {
        this.addRecursive(tree);
    }

    private void addRecursive(TagNode tree) {
        for (Node child : tree) {
            if (child instanceof TagNode) {
                TagNode tagnode = (TagNode)child;
                this.addRecursive(tagnode);
                continue;
            }
            if (!(child instanceof TextNode)) continue;
            TextNode textnode = (TextNode)child;
            this.leafs.add(textnode);
        }
    }

    @Override
    public int getRangeCount() {
        return this.leafs.size();
    }

    @Override
    public boolean rangesEqual(int owni, IRangeComparator otherComp, int otheri) {
        TextOnlyComparator other;
        try {
            other = (TextOnlyComparator)otherComp;
        }
        catch (ClassCastException e) {
            return false;
        }
        return this.getLeaf(owni).isSameText(other.getLeaf(otheri));
    }

    private TextNode getLeaf(int owni) {
        return this.leafs.get(owni);
    }

    @Override
    public boolean skipRangeComparison(int arg0, int arg1, IRangeComparator arg2) {
        return false;
    }

    public double getMatchRatio(TextOnlyComparator other, IProgressMonitor progressMonitor) {
        LCSSettings settings = new LCSSettings();
        settings.setUseGreedyMethod(true);
        settings.setPowLimit(1.5);
        settings.setTooLong(22500.0);
        RangeDifference[] differences = RangeDifferencer.findDifferences(progressMonitor, settings, (IRangeComparator)other, (IRangeComparator)this);
        int distanceOther = 0;
        for (RangeDifference d : differences) {
            distanceOther += d.leftLength();
        }
        int distanceThis = 0;
        for (RangeDifference d : differences) {
            distanceThis += d.rightLength();
        }
        return ((0.0 + (double)distanceOther) / (double)other.getRangeCount() + (0.0 + (double)distanceThis) / (double)this.getRangeCount()) / 2.0;
    }
}

