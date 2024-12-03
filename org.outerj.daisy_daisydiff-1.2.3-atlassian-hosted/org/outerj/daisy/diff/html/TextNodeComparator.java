/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.IProgressMonitor
 */
package org.outerj.daisy.diff.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.outerj.daisy.diff.html.ancestor.AncestorComparator;
import org.outerj.daisy.diff.html.ancestor.AncestorComparatorResult;
import org.outerj.daisy.diff.html.dom.BodyNode;
import org.outerj.daisy.diff.html.dom.DomTree;
import org.outerj.daisy.diff.html.dom.Node;
import org.outerj.daisy.diff.html.dom.TagNode;
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

    public void markAsNew(int start, int end, ModificationType outputFormat) {
        if (end <= start) {
            return;
        }
        if (this.whiteAfterLastChangedPart) {
            this.getTextNode(start).setWhiteBefore(false);
        }
        ArrayList<Modification> nextLastModified = new ArrayList<Modification>();
        for (int i = start; i < end; ++i) {
            Modification mod = new Modification(ModificationType.ADDED, outputFormat);
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

    public void markAsNew(int start, int end) {
        this.markAsNew(start, end, ModificationType.ADDED);
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
                Modification mod = new Modification(ModificationType.CHANGED, ModificationType.CHANGED);
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

    /*
     * Enabled aggressive block sorting
     */
    public void markAsDeleted(int start, int end, TextNodeComparator oldComp, int before, int after, ModificationType outputFormat) {
        if (end <= start) {
            return;
        }
        this.whiteAfterLastChangedPart = before > 0 && this.getTextNode(before - 1).isWhiteAfter();
        ArrayList<Modification> nextLastModified = new ArrayList<Modification>();
        for (int i = start; i < end; ++i) {
            Modification mod = new Modification(ModificationType.REMOVED, outputFormat);
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
            oldComp.getTextNode(i).setModification(mod);
        }
        oldComp.getTextNode(start).getModification().setFirstOfID(true);
        List<Node> deletedNodes = oldComp.getBodyNode().getMinimalDeletedSet(this.deletedID);
        Node prevLeaf = null;
        if (before > 0) {
            prevLeaf = this.getTextNode(before - 1);
        }
        Node nextLeaf = null;
        boolean useAfter = false;
        if (after >= this.getRangeCount()) {
            useAfter = false;
        } else {
            LastCommonParentResult orderResult = this.getTextNode(before).getLastCommonParent(this.getTextNode(after));
            List<TagNode> check = this.getTextNode(before).getParentTree();
            Collections.reverse(check);
            for (TagNode curr : check) {
                if (curr == orderResult.getLastCommonParent()) break;
                if (!curr.isBlockLevel()) continue;
                useAfter = true;
                break;
            }
            if (!useAfter) {
                check = this.getTextNode(after).getParentTree();
                Collections.reverse(check);
                for (TagNode curr : check) {
                    if (curr == orderResult.getLastCommonParent()) break;
                    if (!curr.isBlockLevel()) continue;
                    useAfter = true;
                    break;
                }
            }
        }
        if (useAfter) {
            nextLeaf = this.getTextNode(after);
        } else if (before < this.getRangeCount()) {
            nextLeaf = this.getTextNode(before);
        }
        while (true) {
            boolean splitOccured;
            LastCommonParentResult nextResult;
            LastCommonParentResult prevResult;
            if (deletedNodes.size() <= 0) {
                this.lastModified = nextLastModified;
                ++this.deletedID;
                return;
            }
            if (prevLeaf != null) {
                prevResult = prevLeaf.getLastCommonParent(deletedNodes.get(0));
            } else {
                prevResult = new LastCommonParentResult();
                prevResult.setLastCommonParent(this.getBodyNode());
                prevResult.setIndexInLastCommonParent(-1);
            }
            if (nextLeaf != null) {
                nextResult = nextLeaf.getLastCommonParent(deletedNodes.get(deletedNodes.size() - 1));
            } else {
                nextResult = new LastCommonParentResult();
                nextResult.setLastCommonParent(this.getBodyNode());
                nextResult.setIndexInLastCommonParent(this.getBodyNode().getNbChildren());
            }
            if (prevResult.getLastCommonParentDepth() == nextResult.getLastCommonParentDepth()) {
                if (deletedNodes.get(0).getParent() == deletedNodes.get(deletedNodes.size() - 1).getParent() && prevResult.getLastCommonParent() == nextResult.getLastCommonParent()) {
                    prevResult.setLastCommonParentDepth(prevResult.getLastCommonParentDepth() + 1);
                } else {
                    double distanceNext;
                    double distancePrev = deletedNodes.get(0).getParent().getMatchRatio(prevResult.getLastCommonParent(), null);
                    if (distancePrev < (distanceNext = deletedNodes.get(deletedNodes.size() - 1).getParent().getMatchRatio(nextResult.getLastCommonParent(), null))) {
                        prevResult.setLastCommonParentDepth(prevResult.getLastCommonParentDepth() + 1);
                    } else {
                        nextResult.setLastCommonParentDepth(nextResult.getLastCommonParentDepth() + 1);
                    }
                }
            }
            if (prevResult.getLastCommonParentDepth() > nextResult.getLastCommonParentDepth()) {
                if (prevResult.isSplittingNeeded()) {
                    prevLeaf.getParent().splitUntill(prevResult.getLastCommonParent(), prevLeaf, true);
                }
                prevLeaf = deletedNodes.remove(0).copyTree();
                prevLeaf.setParent(prevResult.getLastCommonParent());
                prevResult.getLastCommonParent().addChild(prevResult.getIndexInLastCommonParent() + 1, prevLeaf);
                continue;
            }
            if (prevResult.getLastCommonParentDepth() >= nextResult.getLastCommonParentDepth()) {
                throw new IllegalStateException();
            }
            if (nextResult.isSplittingNeeded() && (splitOccured = nextLeaf.getParent().splitUntill(nextResult.getLastCommonParent(), nextLeaf, false))) {
                nextResult.setIndexInLastCommonParent(nextResult.getIndexInLastCommonParent() + 1);
            }
            nextLeaf = deletedNodes.remove(deletedNodes.size() - 1).copyTree();
            nextLeaf.setParent(nextResult.getLastCommonParent());
            nextResult.getLastCommonParent().addChild(nextResult.getIndexInLastCommonParent(), nextLeaf);
        }
    }

    public void markAsDeleted(int start, int end, TextNodeComparator oldComp, int before, int after) {
        this.markAsDeleted(start, end, oldComp, before, after, ModificationType.REMOVED);
    }

    public void expandWhiteSpace() {
        this.getBodyNode().expandWhiteSpace();
    }

    @Override
    public Iterator<TextNode> iterator() {
        return this.textNodes.iterator();
    }

    public void setStartDeletedID(long aDeletedID) {
        this.deletedID = aDeletedID;
    }

    public void setStartChangedID(long aChangedID) {
        this.changedID = aChangedID;
    }

    public void setStartNewID(long aNewID) {
        this.newID = aNewID;
    }

    public long getChangedID() {
        return this.changedID;
    }

    public long getDeletedID() {
        return this.deletedID;
    }

    public long getNewID() {
        return this.newID;
    }

    public List<Modification> getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(List<Modification> aLastModified) {
        this.lastModified = new ArrayList<Modification>(aLastModified);
    }
}

