/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.implementations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XPath2NodeFilter
implements NodeFilter {
    private final boolean hasUnionFilter;
    private final boolean hasSubtractFilter;
    private final boolean hasIntersectFilter;
    private final Set<Node> unionNodes;
    private final Set<Node> subtractNodes;
    private final Set<Node> intersectNodes;
    private int inSubtract = -1;
    private int inIntersect = -1;
    private int inUnion = -1;

    XPath2NodeFilter(List<NodeList> unionNodes, List<NodeList> subtractNodes, List<NodeList> intersectNodes) {
        this.hasUnionFilter = !unionNodes.isEmpty();
        this.unionNodes = XPath2NodeFilter.convertNodeListToSet(unionNodes);
        this.hasSubtractFilter = !subtractNodes.isEmpty();
        this.subtractNodes = XPath2NodeFilter.convertNodeListToSet(subtractNodes);
        this.hasIntersectFilter = !intersectNodes.isEmpty();
        this.intersectNodes = XPath2NodeFilter.convertNodeListToSet(intersectNodes);
    }

    @Override
    public int isNodeInclude(Node currentNode) {
        int result = 1;
        if (this.hasSubtractFilter && XPath2NodeFilter.rooted(currentNode, this.subtractNodes)) {
            result = -1;
        } else if (this.hasIntersectFilter && !XPath2NodeFilter.rooted(currentNode, this.intersectNodes)) {
            result = 0;
        }
        if (result == 1) {
            return 1;
        }
        if (this.hasUnionFilter) {
            if (XPath2NodeFilter.rooted(currentNode, this.unionNodes)) {
                return 1;
            }
            result = 0;
        }
        return result;
    }

    @Override
    public int isNodeIncludeDO(Node n, int level) {
        int result = 1;
        if (this.hasSubtractFilter) {
            if (this.inSubtract == -1 || level <= this.inSubtract) {
                this.inSubtract = XPath2NodeFilter.inList(n, this.subtractNodes) ? level : -1;
            }
            if (this.inSubtract != -1) {
                result = -1;
            }
        }
        if (result != -1 && this.hasIntersectFilter && (this.inIntersect == -1 || level <= this.inIntersect)) {
            if (!XPath2NodeFilter.inList(n, this.intersectNodes)) {
                this.inIntersect = -1;
                result = 0;
            } else {
                this.inIntersect = level;
            }
        }
        if (level <= this.inUnion) {
            this.inUnion = -1;
        }
        if (result == 1) {
            return 1;
        }
        if (this.hasUnionFilter) {
            if (this.inUnion == -1 && XPath2NodeFilter.inList(n, this.unionNodes)) {
                this.inUnion = level;
            }
            if (this.inUnion != -1) {
                return 1;
            }
            result = 0;
        }
        return result;
    }

    static boolean rooted(Node currentNode, Set<Node> nodeList) {
        if (nodeList.isEmpty()) {
            return false;
        }
        if (nodeList.contains(currentNode)) {
            return true;
        }
        for (Node rootNode : nodeList) {
            if (!XMLUtils.isDescendantOrSelf(rootNode, currentNode)) continue;
            return true;
        }
        return false;
    }

    static boolean inList(Node currentNode, Set<Node> nodeList) {
        return nodeList.contains(currentNode);
    }

    private static Set<Node> convertNodeListToSet(List<NodeList> l) {
        HashSet<Node> result = new HashSet<Node>();
        for (NodeList rootNodes : l) {
            int length = rootNodes.getLength();
            for (int i = 0; i < length; ++i) {
                Node rootNode = rootNodes.item(i);
                result.add(rootNode);
            }
        }
        return result;
    }
}

