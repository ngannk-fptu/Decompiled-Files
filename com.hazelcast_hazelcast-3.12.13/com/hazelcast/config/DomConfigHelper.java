/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.util.StringUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DomConfigHelper {
    private DomConfigHelper() {
    }

    public static void fillProperties(Node node, Map<String, Comparable> properties, boolean domLevel3) {
        if (properties == null) {
            return;
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            if (n.getNodeType() == 3 || n.getNodeType() == 8) continue;
            String name = DomConfigHelper.cleanNodeName(n);
            String propertyName = "property".equals(name) ? DomConfigHelper.getTextContent(n.getAttributes().getNamedItem("name"), domLevel3).trim() : name;
            String value = DomConfigHelper.getTextContent(n, domLevel3).trim();
            properties.put(propertyName, (Comparable)((Object)value));
        }
    }

    public static void fillProperties(Node node, Properties properties, boolean domLevel3) {
        if (properties == null) {
            return;
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(n);
            String propertyName = "property".equals(name) ? DomConfigHelper.getTextContent(n.getAttributes().getNamedItem("name"), domLevel3).trim() : name;
            String value = DomConfigHelper.getTextContent(n, domLevel3).trim();
            properties.setProperty(propertyName, value);
        }
    }

    public static Iterable<Node> childElements(Node node) {
        return new IterableNodeList(node, 1);
    }

    public static Iterable<Node> asElementIterable(NodeList list) {
        return new IterableNodeList(list, 1);
    }

    public static String cleanNodeName(Node node) {
        String nodeName = node.getLocalName();
        if (nodeName == null) {
            throw new HazelcastException("Local node name is null for " + node);
        }
        return StringUtil.lowerCaseInternal(nodeName);
    }

    public static String getTextContent(Node node, boolean domLevel3) {
        if (node != null) {
            String text = domLevel3 ? node.getTextContent() : DomConfigHelper.getTextContentOld(node);
            return text != null ? text.trim() : "";
        }
        return "";
    }

    private static String getTextContentOld(Node node) {
        Node child = node.getFirstChild();
        if (child != null) {
            Node next = child.getNextSibling();
            if (next == null) {
                return DomConfigHelper.hasTextContent(child) ? child.getNodeValue() : "";
            }
            StringBuilder buf = new StringBuilder();
            DomConfigHelper.appendTextContents(node, buf);
            return buf.toString();
        }
        return "";
    }

    private static void appendTextContents(Node node, StringBuilder buf) {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!DomConfigHelper.hasTextContent(child)) continue;
            buf.append(child.getNodeValue());
        }
    }

    private static boolean hasTextContent(Node node) {
        short nodeType = node.getNodeType();
        return nodeType != 8 && nodeType != 7;
    }

    public static boolean getBooleanValue(String value) {
        return Boolean.parseBoolean(StringUtil.lowerCaseInternal(value));
    }

    public static int getIntegerValue(String parameterName, String value) {
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new InvalidConfigurationException(String.format("Invalid integer value for parameter %s: %s", parameterName, value));
        }
    }

    public static int getIntegerValue(String parameterName, String value, int defaultValue) {
        if (StringUtil.isNullOrEmpty(value)) {
            return defaultValue;
        }
        return DomConfigHelper.getIntegerValue(parameterName, value);
    }

    public static long getLongValue(String parameterName, String value) {
        try {
            return Long.parseLong(value);
        }
        catch (Exception e) {
            throw new InvalidConfigurationException(String.format("Invalid long integer value for parameter %s: %s", parameterName, value));
        }
    }

    public static long getLongValue(String parameterName, String value, long defaultValue) {
        if (StringUtil.isNullOrEmpty(value)) {
            return defaultValue;
        }
        return DomConfigHelper.getLongValue(parameterName, value);
    }

    public static double getDoubleValue(String parameterName, String value) {
        try {
            return Double.parseDouble(value);
        }
        catch (Exception e) {
            throw new InvalidConfigurationException(String.format("Invalid long integer value for parameter %s: %s", parameterName, value));
        }
    }

    public static double getDoubleValue(String parameterName, String value, double defaultValue) {
        if (StringUtil.isNullOrEmpty(value)) {
            return defaultValue;
        }
        return DomConfigHelper.getDoubleValue(parameterName, value);
    }

    public static String getAttribute(Node node, String attName, boolean domLevel3) {
        Node attNode = node.getAttributes().getNamedItem(attName);
        if (attNode == null) {
            return null;
        }
        return DomConfigHelper.getTextContent(attNode, domLevel3);
    }

    private static class IterableNodeList
    implements Iterable<Node> {
        private final NodeList wrapped;
        private final int maximum;
        private final short nodeType;

        IterableNodeList(Node parent, short nodeType) {
            this(parent.getChildNodes(), nodeType);
        }

        IterableNodeList(NodeList wrapped, short nodeType) {
            this.wrapped = wrapped;
            this.nodeType = nodeType;
            this.maximum = wrapped.getLength();
        }

        @Override
        public Iterator<Node> iterator() {
            return new Iterator<Node>(){
                private int index;
                private Node next;

                @Override
                public boolean hasNext() {
                    this.next = null;
                    while (this.index < maximum) {
                        Node item = wrapped.item(this.index);
                        if (nodeType == 0 || item.getNodeType() == nodeType) {
                            this.next = item;
                            return true;
                        }
                        ++this.index;
                    }
                    return false;
                }

                @Override
                public Node next() {
                    if (this.hasNext()) {
                        ++this.index;
                        return this.next;
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}

