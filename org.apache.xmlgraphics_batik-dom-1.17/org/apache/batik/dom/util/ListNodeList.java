/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.util;

import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ListNodeList
implements NodeList {
    protected List list;

    public ListNodeList(List list) {
        this.list = list;
    }

    @Override
    public Node item(int index) {
        if (index < 0 || index > this.list.size()) {
            return null;
        }
        return (Node)this.list.get(index);
    }

    @Override
    public int getLength() {
        return this.list.size();
    }
}

