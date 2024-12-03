/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.outerj.daisy.diff.html.ancestor.AncestorComparator;
import org.outerj.daisy.diff.html.ancestor.AncestorComparatorResult;
import org.outerj.daisy.diff.html.dom.BodyNode;
import org.outerj.daisy.diff.html.dom.DomTree;
import org.outerj.daisy.diff.html.dom.Node;
import org.outerj.daisy.diff.html.dom.TextNode;
import org.outerj.daisy.diff.html.dom.helper.LastCommonParentResult;
import org.outerj.daisy.diff.html.modification.Modification;
import org.outerj.daisy.diff.html.modification.ModificationType;

public class TextNodeComparator
implements IRangeComparator,
Iterable<TextNode> {
    private List<TextNode> textNodes = new ArrayList<TextNode>(50);
    private List<Modification> lastModified = new ArrayList<Modification>();
    private BodyNode bodyNode;
    private Locale locale;
    private long newID = 0L;
    private long changedID = 0L;
    private boolean changedIDUsed = false;
    private boolean whiteAfterLastChangedPart = false;
    private long deletedID = 0L;

    public TextNodeComparator(DomTree tree, Locale locale) {
        this.locale = locale;
        this.textNodes = tree.getTextNodes();
        this.bodyNode = tree.getBodyNode();
    }

    public BodyNode getBodyNode() {
        return this.bodyNode;
    }

    @Override
    public int getRangeCount() {
        return this.textNodes.size();
    }

    public TextNode getTextNode(int i) {
        return this.textNodes.get(i);
    }

    public void markAsNew(int start, int end) {
        if (end <= start) {
            return;
        }
        if (this.whiteAfterLastChangedPart) {
            this.getTextNode(start).setWhiteBefore(false);
        }
        ArrayList<Modification> nextLastModified = new ArrayList<Modification>();
        for (int i = start; i < end; ++i) {
            Modification mod = new Modification(ModificationType.ADDED);
            mod.setID(this.newID);
            if (this.lastModified.size() > 0) {
                mod.setPrevious(this.lastModified.get(0));
                if (this.lastModified.get(0).getNext() == null) {
                    for (Modification lastMod : this.lastModified) {
                        lastMod.setNext(mod);
                    }
                }
            }
            nextLastModified.add(mod);
            this.getTextNode(i).setModification(mod);
        }
        this.getTextNode(start).getModification().setFirstOfID(true);
        ++this.newID;
        this.lastModified = nextLastModified;
    }

    @Override
    public boolean rangesEqual(int i1, IRangeComparator rangeComp, int i2) {
        TextNodeComparator comp;
        try {
            comp = (TextNodeComparator)rangeComp;
        }
        catch (RuntimeException e) {
            return false;
        }
        return this.getTextNode(i1).isSameText(comp.getTextNode(i2));
    }

    @Override
    public boolean skipRangeComparison(int arg0, int arg1, IRangeComparator arg2) {
        return false;
    }

    public void handlePossibleChangedPart(int leftstart, int leftend, int rightstart, int rightend, TextNodeComparator leftComparator, IProgressMonitor progressMonitor) {
        int i = rightstart;
        int j = leftstart;
        if (this.changedIDUsed) {
            ++this.changedID;
            this.changedIDUsed = false;
        }
        ArrayList<Modification> nextLastModified = new ArrayList<Modification>();
        String changes = null;
        while (i < rightend) {
            AncestorComparator acother;
            AncestorComparator acthis = new AncestorComparator(this.getTextNode(i).getParentTree());
            AncestorComparatorResult result = acthis.getResult(acother = new AncestorComparator(leftComparator.getTextNode(j).getParentTree()), this.locale, progressMonitor);
            if (result.isChanged()) {
                Modification mod = new Modification(ModificationType.CHANGED);
                if (!this.changedIDUsed) {
                    mod.setFirstOfID(true);
                    if (nextLastModified.size() > 0) {
                        this.lastModified = nextLastModified;
                        nextLastModified = new ArrayList();
                    }
                } else if (result.getChanges() != null && !result.getChanges().equals(changes)) {
                    ++this.changedID;
                    mod.setFirstOfID(true);
                    if (nextLastModified.size() > 0) {
                        this.lastModified = nextLastModified;
                        nextLastModified = new ArrayList();
                    }
                }
                if (this.lastModified.size() > 0) {
                    mod.setPrevious(this.lastModified.get(0));
                    if (this.lastModified.get(0).getNext() == null) {
                        for (Modification lastMod : this.lastModified) {
                            lastMod.setNext(mod);
                        }
                    }
                }
                nextLastModified.add(mod);
                mod.setChanges(result.getChanges());
                mod.setHtmlLayoutChanges(result.getHtmlLayoutChanges());
                mod.setID(this.changedID);
                this.getTextNode(i).setModification(mod);
                changes = result.getChanges();
                this.changedIDUsed = true;
            } else if (this.changedIDUsed) {
                ++this.changedID;
                this.changedIDUsed = false;
            }
            ++i;
            ++j;
        }
        if (nextLastModified.size() > 0) {
            this.lastModified = nextLastModified;
        }
    }

    public void markAsDeleted(int leftStart, int leftEnd, TextNodeComparator leftComparator, int rightStart) {
        if (leftEnd <= leftStart) {
            return;
        }
        this.whiteAfterLastChangedPart = rightStart > 0 && this.getTextNode(rightStart - 1).isWhiteAfter();
        ArrayList<Modification> nextLastModified = new ArrayList<Modification>();
        for (int i = leftStart; i < leftEnd; ++i) {
            Modification mod = new Modification(ModificationType.REMOVED);
            mod.setID(this.deletedID);
            if (this.lastModified.size() > 0) {
                mod.setPrevious(this.lastModified.get(0));
                if (this.lastModified.get(0).getNext() == null) {
                    for (Modification lastMod : this.lastModified) {
                        lastMod.setNext(mod);
                    }
                }
            }
            nextLastModified.add(mod);
            leftComparator.getTextNode(i).setModification(mod);
        }
        leftComparator.getTextNode(leftStart).getModification().setFirstOfID(true);
        LinkedList<Node> deletedNodes = new LinkedList<Node>(leftComparator.getBodyNode().getMinimalDeletedSet(this.deletedID));
        Node prevLeaf = null;
        if (rightStart > 0) {
            prevLeaf = this.getTextNode(rightStart - 1);
        }
        Node nextLeaf = null;
        if (rightStart < this.getRangeCount()) {
            nextLeaf = this.getTextNode(rightStart);
        }
        while (!deletedNodes.isEmpty()) {
            LastCommonParentResult nextResult;
            LastCommonParentResult prevResult;
            if (prevLeaf != null) {
                prevResult = prevLeaf.getLastCommonParent(deletedNodes.getFirst());
            } else {
                prevResult = new LastCommonParentResult(this.getBodyNode());
                prevResult.setIndexInLastCommonParent(-1);
            }
            if (nextLeaf != null) {
                nextResult = nextLeaf.getLastCommonParent(deletedNodes.getLast());
            } else {
                nextResult = new LastCommonParentResult(this.getBodyNode());
                nextResult.setIndexInLastCommonParent(this.getBodyNode().getNbChildren());
            }
            if (prevResult.getLastCommonParentDepth() == nextResult.getLastCommonParentDepth()) {
                if (deletedNodes.getFirst().getParent() == deletedNodes.getLast().getParent() && prevResult.getLastCommonParent() == nextResult.getLastCommonParent()) {
                    prevResult.setLastCommonParentDepth(prevResult.getLastCommonParentDepth() + 1);
                } else {
                    double distancePrev = deletedNodes.getFirst().getParent().getMatchRatio(prevResult.getLastCommonParent(), null);
                    double distanceNext = deletedNodes.getLast().getParent().getMatchRatio(nextResult.getLastCommonParent(), null);
                    if (distanceNext <= distancePrev) {
                        nextResult.setLastCommonParentDepth(nextResult.getLastCommonParentDepth() + 1);
                    } else {
                        prevResult.setLastCommonParentDepth(prevResult.getLastCommonParentDepth() + 1);
                    }
                }
            }
            if (prevResult.getLastCommonParentDepth() > nextResult.getLastCommonParentDepth()) {
                if (prevResult.isSplittingNeeded()) {
                    prevLeaf.getParent().splitUntill(prevResult.getLastCommonParent(), prevLeaf, true);
                }
                prevLeaf = deletedNodes.removeFirst().copyTree();
                prevLeaf.setParent(prevResult.getLastCommonParent());
                prevResult.getLastCommonParent().addChild(prevResult.getIndexInLastCommonParent() + 1, prevLeaf);
                continue;
            }
            if (prevResult.getLastCommonParentDepth() < nextResult.getLastCommonParentDepth()) {
                boolean splitOccured;
                if (nextResult.isSplittingNeeded() && (splitOccured = nextLeaf.getParent().splitUntill(nextResult.getLastCommonParent(), nextLeaf, false))) {
                    nextResult.setIndexInLastCommonParent(nextResult.getIndexInLastCommonParent() + 1);
                }
                nextLeaf = deletedNodes.removeLast().copyTree();
                nextLeaf.setParent(nextResult.getLastCommonParent());
                nextResult.getLastCommonParent().addChild(nextResult.getIndexInLastCommonParent(), nextLeaf);
                continue;
            }
            throw new IllegalStateException();
        }
        this.lastModified = nextLastModified;
        ++this.deletedID;
    }

    public void expandWhiteSpace() {
        this.getBodyNode().expandWhiteSpace();
    }

    @Override
    public Iterator<TextNode> iterator() {
        return this.textNodes.iterator();
    }
}

