/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HelperNodeList
implements NodeList {
    private final List<Node> nodes = new ArrayList<Node>();
    private final boolean allNodesMustHaveSameParent;

    public HelperNodeList() {
        this(false);
    }

    public HelperNodeList(boolean allNodesMustHaveSameParent) {
        this.allNodesMustHaveSameParent = allNodesMustHaveSameParent;
    }

    @Override
    public Node item(int index) {
        return this.nodes.get(index);
    }

    @Override
    public int getLength() {
        return this.nodes.size();
    }

    public void appendChild(Node node) throws IllegalArgumentException {
        if (this.allNodesMustHaveSameParent && this.getLength() > 0 && this.item(0).getParentNode() != node.getParentNode()) {
            throw new IllegalArgumentException("Nodes have not the same Parent");
        }
        this.nodes.add(node);
    }

    public Document getOwnerDocument() {
        if (this.getLength() == 0) {
            return null;
        }
        return XMLUtils.getOwnerDocument(this.item(0));
    }
}

