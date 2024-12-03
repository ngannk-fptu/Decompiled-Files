/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.ref.DTMNodeListBase;
import org.w3c.dom.Node;

public class DTMChildIterNodeList
extends DTMNodeListBase {
    private int m_firstChild;
    private DTM m_parentDTM;

    private DTMChildIterNodeList() {
    }

    public DTMChildIterNodeList(DTM parentDTM, int parentHandle) {
        this.m_parentDTM = parentDTM;
        this.m_firstChild = parentDTM.getFirstChild(parentHandle);
    }

    @Override
    public Node item(int index) {
        int handle = this.m_firstChild;
        while (--index >= 0 && handle != -1) {
            handle = this.m_parentDTM.getNextSibling(handle);
        }
        if (handle == -1) {
            return null;
        }
        return this.m_parentDTM.getNode(handle);
    }

    @Override
    public int getLength() {
        int count = 0;
        int handle = this.m_firstChild;
        while (handle != -1) {
            ++count;
            handle = this.m_parentDTM.getNextSibling(handle);
        }
        return count;
    }
}

