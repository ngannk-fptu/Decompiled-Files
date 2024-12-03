/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.IProgressMonitor
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

public final class TextOnlyComparator
implements IRangeComparator {
    private TextNode[] leafs;

    public TextOnlyComparator(TagNode tree) {
        ArrayList<TextNode> leafs = new ArrayList<TextNode>();
        TextOnlyComparator.addRecursive(tree, leafs);
        this.leafs = leafs.toArray(new TextNode[leafs.size()]);
    }

    private static void addRecursive(TagNode tree, List<TextNode> leafs) {
        for (Node child : tree) {
            if (child instanceof TagNode) {
                TagNode tagnode = (TagNode)child;
                TextOnlyComparator.addRecursive(tagnode, leafs);
                continue;
            }
            if (!(child instanceof TextNode)) continue;
            TextNode textnode = (TextNode)child;
            leafs.add(textnode);
        }
    }

    @Override
    public int getRangeCount() {
        return this.leafs.length;
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
        return this.leafs[owni].isSameText(other.leafs[otheri]);
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

