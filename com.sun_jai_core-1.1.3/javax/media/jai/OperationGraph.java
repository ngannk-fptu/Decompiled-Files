/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import javax.media.jai.JaiI18N;
import javax.media.jai.PartialOrderNode;

class OperationGraph
implements Serializable {
    Vector operations = new Vector();
    Vector orderedOperations;
    boolean isChanged = true;
    private boolean lookupByName = false;

    OperationGraph() {
    }

    OperationGraph(boolean lookupByName) {
        this.lookupByName = lookupByName;
    }

    private boolean compare(PartialOrderNode poNode, Object op) {
        if (this.lookupByName) {
            return poNode.getName().equalsIgnoreCase((String)op);
        }
        return poNode.getData() == op;
    }

    void addOp(PartialOrderNode poNode) {
        this.operations.addElement(poNode);
        this.isChanged = true;
    }

    synchronized boolean removeOp(Object op) {
        boolean retval = false;
        PartialOrderNode poNode = this.lookupOp(op);
        if (poNode != null && (retval = this.operations.removeElement(poNode))) {
            this.isChanged = true;
        }
        return retval;
    }

    PartialOrderNode lookupOp(Object op) {
        int num = this.operations.size();
        for (int i = 0; i < num; ++i) {
            PartialOrderNode poNode = (PartialOrderNode)this.operations.elementAt(i);
            if (!this.compare(poNode, op)) continue;
            PartialOrderNode tempNode = poNode;
            return tempNode;
        }
        return null;
    }

    synchronized boolean setPreference(Object preferred, Object other) {
        boolean retval = false;
        if (preferred == null || other == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (preferred == other) {
            return retval;
        }
        PartialOrderNode preferredPONode = this.lookupOp(preferred);
        PartialOrderNode otherPONode = this.lookupOp(other);
        if (preferredPONode != null && otherPONode != null) {
            preferredPONode.addEdge(otherPONode);
            retval = true;
            this.isChanged = true;
        }
        return retval;
    }

    synchronized boolean unsetPreference(Object preferred, Object other) {
        boolean retval = false;
        if (preferred == null || other == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (preferred == other) {
            return retval;
        }
        PartialOrderNode preferredPONode = this.lookupOp(preferred);
        PartialOrderNode otherPONode = this.lookupOp(other);
        if (preferredPONode != null && otherPONode != null) {
            preferredPONode.removeEdge(otherPONode);
            retval = true;
            this.isChanged = true;
        }
        return retval;
    }

    public synchronized Vector getOrderedOperationList() {
        PartialOrderNode poNode;
        int i;
        if (!this.isChanged) {
            Vector ordered = this.orderedOperations;
            return ordered;
        }
        int num = this.operations.size();
        for (int i2 = 0; i2 < num; ++i2) {
            PartialOrderNode pon = (PartialOrderNode)this.operations.elementAt(i2);
            pon.setCopyInDegree(pon.getInDegree());
        }
        this.orderedOperations = new Vector(num);
        this.isChanged = false;
        PartialOrderNode zeroList = null;
        for (i = 0; i < num; ++i) {
            poNode = (PartialOrderNode)this.operations.elementAt(i);
            if (poNode.getCopyInDegree() != 0) continue;
            poNode.setZeroLink(zeroList);
            zeroList = poNode;
        }
        for (i = 0; i < num; ++i) {
            if (zeroList == null) {
                this.orderedOperations = null;
                return null;
            }
            PartialOrderNode firstNode = zeroList;
            this.orderedOperations.addElement(firstNode);
            zeroList = zeroList.getZeroLink();
            Enumeration neighbors = firstNode.getNeighbors();
            while (neighbors.hasMoreElements()) {
                poNode = (PartialOrderNode)neighbors.nextElement();
                poNode.decrementCopyInDegree();
                if (poNode.getCopyInDegree() != 0) continue;
                poNode.setZeroLink(zeroList);
                zeroList = poNode;
            }
        }
        Vector ordered = this.orderedOperations;
        return ordered;
    }
}

