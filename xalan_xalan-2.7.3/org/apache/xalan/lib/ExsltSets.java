/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib;

import java.util.HashMap;
import org.apache.xalan.lib.ExsltBase;
import org.apache.xml.utils.DOMHelper;
import org.apache.xpath.NodeSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExsltSets
extends ExsltBase {
    public static NodeList leading(NodeList nl1, NodeList nl2) {
        if (nl2.getLength() == 0) {
            return nl1;
        }
        NodeSet ns1 = new NodeSet(nl1);
        NodeSet leadNodes = new NodeSet();
        Node endNode = nl2.item(0);
        if (!ns1.contains(endNode)) {
            return leadNodes;
        }
        for (int i = 0; i < nl1.getLength(); ++i) {
            Node testNode = nl1.item(i);
            if (!DOMHelper.isNodeAfter(testNode, endNode) || DOMHelper.isNodeTheSame(testNode, endNode)) continue;
            leadNodes.addElement(testNode);
        }
        return leadNodes;
    }

    public static NodeList trailing(NodeList nl1, NodeList nl2) {
        if (nl2.getLength() == 0) {
            return nl1;
        }
        NodeSet ns1 = new NodeSet(nl1);
        NodeSet trailNodes = new NodeSet();
        Node startNode = nl2.item(0);
        if (!ns1.contains(startNode)) {
            return trailNodes;
        }
        for (int i = 0; i < nl1.getLength(); ++i) {
            Node testNode = nl1.item(i);
            if (!DOMHelper.isNodeAfter(startNode, testNode) || DOMHelper.isNodeTheSame(startNode, testNode)) continue;
            trailNodes.addElement(testNode);
        }
        return trailNodes;
    }

    public static NodeList intersection(NodeList nl1, NodeList nl2) {
        NodeSet ns1 = new NodeSet(nl1);
        NodeSet ns2 = new NodeSet(nl2);
        NodeSet inter = new NodeSet();
        inter.setShouldCacheNodes(true);
        for (int i = 0; i < ns1.getLength(); ++i) {
            Node n = ns1.elementAt(i);
            if (!ns2.contains(n)) continue;
            inter.addElement(n);
        }
        return inter;
    }

    public static NodeList difference(NodeList nl1, NodeList nl2) {
        NodeSet ns1 = new NodeSet(nl1);
        NodeSet ns2 = new NodeSet(nl2);
        NodeSet diff = new NodeSet();
        diff.setShouldCacheNodes(true);
        for (int i = 0; i < ns1.getLength(); ++i) {
            Node n = ns1.elementAt(i);
            if (ns2.contains(n)) continue;
            diff.addElement(n);
        }
        return diff;
    }

    public static NodeList distinct(NodeList nl) {
        NodeSet dist = new NodeSet();
        dist.setShouldCacheNodes(true);
        HashMap<String, Node> stringTable = new HashMap<String, Node>();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node currNode = nl.item(i);
            String key = ExsltSets.toString(currNode);
            if (key == null) {
                dist.addElement(currNode);
                continue;
            }
            if (stringTable.containsKey(key)) continue;
            stringTable.put(key, currNode);
            dist.addElement(currNode);
        }
        return dist;
    }

    public static boolean hasSameNode(NodeList nl1, NodeList nl2) {
        NodeSet ns1 = new NodeSet(nl1);
        NodeSet ns2 = new NodeSet(nl2);
        for (int i = 0; i < ns1.getLength(); ++i) {
            if (!ns2.contains(ns1.elementAt(i))) continue;
            return true;
        }
        return false;
    }
}

