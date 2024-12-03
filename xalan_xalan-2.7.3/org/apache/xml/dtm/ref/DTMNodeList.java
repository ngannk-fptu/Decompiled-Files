/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMNodeListBase;
import org.w3c.dom.Node;

public class DTMNodeList
extends DTMNodeListBase {
    private DTMIterator m_iter;

    private DTMNodeList() {
    }

    public DTMNodeList(DTMIterator dtmIterator) {
        if (dtmIterator != null) {
            int pos = dtmIterator.getCurrentPos();
            try {
                this.m_iter = dtmIterator.cloneWithReset();
            }
            catch (CloneNotSupportedException cnse) {
                this.m_iter = dtmIterator;
            }
            this.m_iter.setShouldCacheNodes(true);
            this.m_iter.runTo(-1);
            this.m_iter.setCurrentPos(pos);
        }
    }

    public DTMIterator getDTMIterator() {
        return this.m_iter;
    }

    @Override
    public Node item(int index) {
        if (this.m_iter != null) {
            int handle = this.m_iter.item(index);
            if (handle == -1) {
                return null;
            }
            return this.m_iter.getDTM(handle).getNode(handle);
        }
        return null;
    }

    @Override
    public int getLength() {
        return this.m_iter != null ? this.m_iter.getLength() : 0;
    }
}

