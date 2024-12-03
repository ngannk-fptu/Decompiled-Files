/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

final class PartialOrderNode
implements Cloneable,
Serializable {
    protected String name;
    protected Object nodeData;
    protected int inDegree = 0;
    protected int copyInDegree = 0;
    protected PartialOrderNode zeroLink = null;
    Vector neighbors = new Vector();

    PartialOrderNode(Object nodeData, String name) {
        this.nodeData = nodeData;
        this.name = name;
    }

    Object getData() {
        return this.nodeData;
    }

    String getName() {
        return this.name;
    }

    int getInDegree() {
        return this.inDegree;
    }

    int getCopyInDegree() {
        return this.copyInDegree;
    }

    void setCopyInDegree(int copyInDegree) {
        this.copyInDegree = copyInDegree;
    }

    PartialOrderNode getZeroLink() {
        return this.zeroLink;
    }

    void setZeroLink(PartialOrderNode poNode) {
        this.zeroLink = poNode;
    }

    Enumeration getNeighbors() {
        return this.neighbors.elements();
    }

    void addEdge(PartialOrderNode poNode) {
        this.neighbors.addElement(poNode);
        poNode.incrementInDegree();
    }

    void removeEdge(PartialOrderNode poNode) {
        this.neighbors.removeElement(poNode);
        poNode.decrementInDegree();
    }

    void incrementInDegree() {
        ++this.inDegree;
    }

    void incrementCopyInDegree() {
        ++this.copyInDegree;
    }

    void decrementInDegree() {
        --this.inDegree;
    }

    void decrementCopyInDegree() {
        --this.copyInDegree;
    }
}

