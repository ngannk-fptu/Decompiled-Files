/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.DTMDOMException;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.WrappedRuntimeException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class DTMNodeIterator
implements NodeIterator {
    private DTMIterator dtm_iter;
    private boolean valid = true;

    public DTMNodeIterator(DTMIterator dtmIterator) {
        try {
            this.dtm_iter = (DTMIterator)dtmIterator.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw new WrappedRuntimeException(cnse);
        }
    }

    public DTMIterator getDTMIterator() {
        return this.dtm_iter;
    }

    @Override
    public void detach() {
        this.valid = false;
    }

    @Override
    public boolean getExpandEntityReferences() {
        return false;
    }

    @Override
    public NodeFilter getFilter() {
        throw new DTMDOMException(9);
    }

    @Override
    public Node getRoot() {
        int handle = this.dtm_iter.getRoot();
        return this.dtm_iter.getDTM(handle).getNode(handle);
    }

    @Override
    public int getWhatToShow() {
        return this.dtm_iter.getWhatToShow();
    }

    @Override
    public Node nextNode() throws DOMException {
        if (!this.valid) {
            throw new DTMDOMException(11);
        }
        int handle = this.dtm_iter.nextNode();
        if (handle == -1) {
            return null;
        }
        return this.dtm_iter.getDTM(handle).getNode(handle);
    }

    @Override
    public Node previousNode() {
        if (!this.valid) {
            throw new DTMDOMException(11);
        }
        int handle = this.dtm_iter.previousNode();
        if (handle == -1) {
            return null;
        }
        return this.dtm_iter.getDTM(handle).getNode(handle);
    }
}

