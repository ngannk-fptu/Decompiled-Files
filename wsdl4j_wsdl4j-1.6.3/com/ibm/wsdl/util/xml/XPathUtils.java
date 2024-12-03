/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.util.xml;

import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class XPathUtils {
    private static Node getPreviousTypedNode(Node node, short nodeType) {
        for (node = node.getPreviousSibling(); node != null && node.getNodeType() != nodeType; node = node.getPreviousSibling()) {
        }
        return node;
    }

    private static Node getNextTypedNode(Node node, short nodeType) {
        for (node = node.getNextSibling(); node != null && node.getNodeType() != nodeType; node = node.getNextSibling()) {
        }
        return node;
    }

    private static String getValue(Node node, short nodeType) {
        switch (nodeType) {
            case 1: {
                return ((Element)node).getTagName();
            }
            case 3: {
                return ((Text)node).getData();
            }
            case 7: {
                return ((ProcessingInstruction)node).getData();
            }
        }
        return "";
    }

    private static short getNodeType(Node node) {
        return node != null ? (short)node.getNodeType() : (short)-1;
    }

    private static String getXPathFromVector(Vector path) {
        StringBuffer strBuf = new StringBuffer();
        int length = path.size();
        for (int i = 0; i < length; ++i) {
            String step;
            boolean hasMatchingSiblings;
            Node tempNode = (Node)path.elementAt(i);
            short nodeType = XPathUtils.getNodeType(tempNode);
            String targetValue = XPathUtils.getValue(tempNode, nodeType);
            int position = 1;
            tempNode = XPathUtils.getPreviousTypedNode(tempNode, nodeType);
            while (tempNode != null) {
                if (nodeType == 1) {
                    if (XPathUtils.getValue(tempNode, nodeType).equals(targetValue)) {
                        ++position;
                    }
                } else {
                    ++position;
                }
                tempNode = XPathUtils.getPreviousTypedNode(tempNode, nodeType);
            }
            boolean bl = hasMatchingSiblings = position > 1;
            if (!hasMatchingSiblings) {
                tempNode = (Node)path.elementAt(i);
                tempNode = XPathUtils.getNextTypedNode(tempNode, nodeType);
                while (!hasMatchingSiblings && tempNode != null) {
                    if (nodeType == 1) {
                        if (XPathUtils.getValue(tempNode, nodeType).equals(targetValue)) {
                            hasMatchingSiblings = true;
                            continue;
                        }
                        tempNode = XPathUtils.getNextTypedNode(tempNode, nodeType);
                        continue;
                    }
                    hasMatchingSiblings = true;
                }
            }
            switch (nodeType) {
                case 3: {
                    step = "text()";
                    break;
                }
                case 7: {
                    step = "processing-instruction()";
                    break;
                }
                default: {
                    step = targetValue;
                }
            }
            if (step != null && step.length() > 0) {
                strBuf.append('/' + step);
            }
            if (!hasMatchingSiblings) continue;
            strBuf.append("[" + position + "]");
        }
        return strBuf.toString();
    }

    private static Vector getVectorPathFromNode(Node node) {
        Vector<Node> path = new Vector<Node>();
        while (node != null) {
            path.insertElementAt(node, 0);
            node = node.getParentNode();
        }
        return path;
    }

    public static String getXPathExprFromNode(Node node) throws IllegalArgumentException {
        short nodeType = XPathUtils.getNodeType(node);
        switch (nodeType) {
            case 1: 
            case 3: 
            case 7: {
                return XPathUtils.getXPathFromVector(XPathUtils.getVectorPathFromNode(node));
            }
            case 9: {
                return "/";
            }
        }
        throw new IllegalArgumentException("Only works for element, text, document, and PI nodes.");
    }
}

