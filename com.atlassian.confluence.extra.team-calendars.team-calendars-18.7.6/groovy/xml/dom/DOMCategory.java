/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml.dom;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.IntRange;
import groovy.xml.DOMBuilder;
import groovy.xml.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.XmlGroovyMethods;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DOMCategory {
    private static boolean trimWhitespace = false;
    private static boolean keepIgnorableWhitespace = false;

    public static synchronized boolean isGlobalTrimWhitespace() {
        return trimWhitespace;
    }

    public static synchronized void setGlobalTrimWhitespace(boolean trimWhitespace) {
        DOMCategory.trimWhitespace = trimWhitespace;
    }

    public static synchronized boolean isGlobalKeepIgnorableWhitespace() {
        return keepIgnorableWhitespace;
    }

    public static synchronized void setGlobalKeepIgnorableWhitespace(boolean keepIgnorableWhitespace) {
        DOMCategory.keepIgnorableWhitespace = keepIgnorableWhitespace;
    }

    public static Object get(Element element, String elementName) {
        return DOMCategory.xgetAt(element, elementName);
    }

    public static Object get(NodeList nodeList, String elementName) {
        if (nodeList instanceof Element) {
            return DOMCategory.xgetAt((Element)((Object)nodeList), elementName);
        }
        return DOMCategory.xgetAt(nodeList, elementName);
    }

    public static Object get(NamedNodeMap nodeMap, String elementName) {
        return DOMCategory.xgetAt(nodeMap, elementName);
    }

    private static Object xgetAt(Element element, String elementName) {
        if ("..".equals(elementName)) {
            return DOMCategory.parent(element);
        }
        if ("**".equals(elementName)) {
            return DOMCategory.depthFirst(element);
        }
        if (elementName.startsWith("@")) {
            return element.getAttribute(elementName.substring(1));
        }
        return DOMCategory.getChildElements(element, elementName);
    }

    private static Object xgetAt(NodeList nodeList, String elementName) {
        ArrayList results = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (!(node instanceof Element)) continue;
            DOMCategory.addResult(results, DOMCategory.get((Element)node, elementName));
        }
        if (elementName.startsWith("@")) {
            return results;
        }
        return new NodeListsHolder(results);
    }

    public static NamedNodeMap attributes(Element element) {
        return element.getAttributes();
    }

    private static String xgetAt(NamedNodeMap namedNodeMap, String elementName) {
        Attr a = (Attr)namedNodeMap.getNamedItem(elementName);
        return a.getValue();
    }

    public static int size(NamedNodeMap namedNodeMap) {
        return namedNodeMap.getLength();
    }

    public static Node getAt(Node o, int i) {
        return DOMCategory.nodeGetAt(o, i);
    }

    public static Node getAt(NodeListsHolder o, int i) {
        return DOMCategory.nodeGetAt(o, i);
    }

    public static Node getAt(NodesHolder o, int i) {
        return DOMCategory.nodeGetAt(o, i);
    }

    public static NodeList getAt(Node o, IntRange r) {
        return DOMCategory.nodesGetAt(o, r);
    }

    public static NodeList getAt(NodeListsHolder o, IntRange r) {
        return DOMCategory.nodesGetAt(o, r);
    }

    public static NodeList getAt(NodesHolder o, IntRange r) {
        return DOMCategory.nodesGetAt(o, r);
    }

    private static Node nodeGetAt(Object o, int i) {
        Node n;
        if (o instanceof Element && (n = DOMCategory.xgetAt((Element)o, i)) != null) {
            return n;
        }
        if (o instanceof NodeList) {
            return DOMCategory.xgetAt((NodeList)o, i);
        }
        return null;
    }

    private static NodeList nodesGetAt(Object o, IntRange r) {
        NodeList n;
        if (o instanceof Element && (n = DOMCategory.xgetAt((Element)o, r)) != null) {
            return n;
        }
        if (o instanceof NodeList) {
            return DOMCategory.xgetAt((NodeList)o, r);
        }
        return null;
    }

    private static Node xgetAt(Element element, int i) {
        if (DOMCategory.hasChildElements(element, "*")) {
            NodeList nodeList = DOMCategory.getChildElements(element, "*");
            return DOMCategory.xgetAt(nodeList, i);
        }
        return null;
    }

    private static Node xgetAt(NodeList nodeList, int i) {
        if (i < 0) {
            i += nodeList.getLength();
        }
        if (i >= 0 && i < nodeList.getLength()) {
            return nodeList.item(i);
        }
        return null;
    }

    private static NodeList xgetAt(Element element, IntRange r) {
        if (DOMCategory.hasChildElements(element, "*")) {
            NodeList nodeList = DOMCategory.getChildElements(element, "*");
            return DOMCategory.xgetAt(nodeList, r);
        }
        return null;
    }

    private static NodeList xgetAt(NodeList nodeList, IntRange r) {
        int to;
        int from = r.getFromInt();
        if (from == (to = r.getToInt())) {
            return new NodesHolder(Arrays.asList(DOMCategory.xgetAt(nodeList, from)));
        }
        if (from < 0) {
            from += nodeList.getLength();
        }
        if (to < 0) {
            to += nodeList.getLength();
        }
        if (from > to) {
            r = r.isReverse() ? new IntRange(to, from) : new IntRange(from, to);
            from = r.getFromInt();
            to = r.getToInt();
        }
        ArrayList<Node> nodes = new ArrayList<Node>(to - from + 1);
        if (r.isReverse()) {
            for (int i = to; i >= from; --i) {
                nodes.add(nodeList.item(i));
            }
        } else {
            for (int i = from; i <= to; ++i) {
                nodes.add(nodeList.item(i));
            }
        }
        return new NodesHolder(nodes);
    }

    public static String name(Node node) {
        return node.getNodeName();
    }

    public static Node parent(Node node) {
        return node.getParentNode();
    }

    public static String text(Node node) {
        if (node.getNodeType() == 3 || node.getNodeType() == 4) {
            return node.getNodeValue();
        }
        if (node.hasChildNodes()) {
            return DOMCategory.text(node.getChildNodes());
        }
        return "";
    }

    public static String text(NodeList nodeList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            sb.append(DOMCategory.text(nodeList.item(i)));
        }
        return sb.toString();
    }

    public static List<Node> list(NodeList self) {
        ArrayList<Node> answer = new ArrayList<Node>();
        Iterator<Node> it = XmlGroovyMethods.iterator(self);
        while (it.hasNext()) {
            answer.add(it.next());
        }
        return answer;
    }

    public static NodeList depthFirst(Element self) {
        ArrayList<NodeList> result = new ArrayList<NodeList>();
        result.add(DOMCategory.createNodeList(self));
        result.add(self.getElementsByTagName("*"));
        return new NodeListsHolder(result);
    }

    public static void setValue(Element self, String value) {
        Node firstChild = self.getFirstChild();
        if (firstChild == null) {
            firstChild = self.getOwnerDocument().createTextNode(value);
            self.appendChild(firstChild);
        }
        firstChild.setNodeValue(value);
    }

    public static void putAt(Element self, String property, Object value) {
        if (property.startsWith("@")) {
            String attributeName = property.substring(1);
            Document doc = self.getOwnerDocument();
            Attr newAttr = doc.createAttribute(attributeName);
            newAttr.setValue(value.toString());
            self.setAttributeNode(newAttr);
            return;
        }
        InvokerHelper.setProperty(self, property, value);
    }

    public static Element appendNode(Element self, Object name) {
        return DOMCategory.appendNode(self, name, (String)null);
    }

    public static Element appendNode(Element self, Object name, Map attributes) {
        return DOMCategory.appendNode(self, name, attributes, null);
    }

    public static Element appendNode(Element self, Object name, String value) {
        Element newChild;
        Document doc = self.getOwnerDocument();
        if (name instanceof QName) {
            QName qn = (QName)name;
            newChild = doc.createElementNS(qn.getNamespaceURI(), qn.getQualifiedName());
        } else {
            newChild = doc.createElement(name.toString());
        }
        if (value != null) {
            Text text = doc.createTextNode(value);
            newChild.appendChild(text);
        }
        self.appendChild(newChild);
        return newChild;
    }

    public static Element appendNode(Element self, Object name, Map attributes, String value) {
        Element result = DOMCategory.appendNode(self, name, value);
        Iterator iterator = attributes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry e = o = iterator.next();
            DOMCategory.putAt(result, "@" + e.getKey().toString(), e.getValue());
        }
        return result;
    }

    public static Node replaceNode(NodesHolder self, Closure c) {
        if (self.getLength() <= 0 || self.getLength() > 1) {
            throw new GroovyRuntimeException("replaceNode() can only be used to replace a single element, but was applied to " + self.getLength() + " elements.");
        }
        return DOMCategory.replaceNode(self.item(0), c);
    }

    public static Node replaceNode(Node self, Closure c) {
        if (self.getParentNode() instanceof Document) {
            throw new UnsupportedOperationException("Replacing the root node is not supported");
        }
        DOMCategory.appendNodes(self, c);
        self.getParentNode().removeChild(self);
        return self;
    }

    public static void plus(Element self, Closure c) {
        if (self.getParentNode() instanceof Document) {
            throw new UnsupportedOperationException("Adding sibling nodes to the root node is not supported");
        }
        DOMCategory.appendNodes(self, c);
    }

    private static void appendNodes(Node self, Closure c) {
        Node parent = self.getParentNode();
        Node beforeNode = self.getNextSibling();
        DOMBuilder b = new DOMBuilder(self.getOwnerDocument());
        Element newNodes = (Element)b.invokeMethod("rootNode", c);
        Iterator<Node> iter = XmlGroovyMethods.iterator(DOMCategory.children(newNodes));
        while (iter.hasNext()) {
            parent.insertBefore(iter.next(), beforeNode);
        }
    }

    public static List<String> localText(Element self) {
        ArrayList<String> result = new ArrayList<String>();
        if (self.getNodeType() == 3 || self.getNodeType() == 4) {
            result.add(self.getNodeValue());
        } else if (self.hasChildNodes()) {
            NodeList nodeList = self.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node item = nodeList.item(i);
                if (item.getNodeType() != 3 && item.getNodeType() != 4) continue;
                result.add(item.getNodeValue());
            }
        }
        return result;
    }

    public static void plus(NodeList self, Closure c) {
        for (int i = 0; i < self.getLength(); ++i) {
            DOMCategory.plus((Element)self.item(i), c);
        }
    }

    private static NodeList createNodeList(Element self) {
        ArrayList<Element> first = new ArrayList<Element>();
        first.add(self);
        return new NodesHolder(first);
    }

    public static NodeList breadthFirst(Element self) {
        ArrayList<NodeList> result = new ArrayList<NodeList>();
        NodeList thisLevel = DOMCategory.createNodeList(self);
        while (thisLevel.getLength() > 0) {
            result.add(thisLevel);
            thisLevel = DOMCategory.getNextLevel(thisLevel);
        }
        return new NodeListsHolder(result);
    }

    private static NodeList getNextLevel(NodeList thisLevel) {
        ArrayList<NodeList> result = new ArrayList<NodeList>();
        for (int i = 0; i < thisLevel.getLength(); ++i) {
            Node n = thisLevel.item(i);
            if (!(n instanceof Element)) continue;
            result.add(DOMCategory.getChildElements((Element)n, "*"));
        }
        return new NodeListsHolder(result);
    }

    public static NodeList children(Element self) {
        return DOMCategory.getChildElements(self, "*");
    }

    private static boolean hasChildElements(Element self, String elementName) {
        return DOMCategory.getChildElements(self, elementName).getLength() > 0;
    }

    private static NodeList getChildElements(Element self, String elementName) {
        ArrayList<Node> result = new ArrayList<Node>();
        NodeList nodeList = self.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == 1) {
                Element child = (Element)node;
                if (!"*".equals(elementName) && !child.getTagName().equals(elementName)) continue;
                result.add(child);
                continue;
            }
            if (node.getNodeType() != 3) continue;
            String value = node.getNodeValue();
            if (!DOMCategory.isGlobalKeepIgnorableWhitespace() && value.trim().length() == 0 || DOMCategory.isGlobalTrimWhitespace()) {
                value = value.trim();
            }
            if (!"*".equals(elementName) || value.length() <= 0) continue;
            node.setNodeValue(value);
            result.add(node);
        }
        return new NodesHolder(result);
    }

    public static String toString(Object o) {
        if (o instanceof Node && ((Node)o).getNodeType() == 3) {
            return ((Node)o).getNodeValue();
        }
        if (o instanceof NodeList) {
            return DOMCategory.toString((NodeList)o);
        }
        return o.toString();
    }

    public static Object xpath(Node self, String expression, javax.xml.namespace.QName returnType) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            return xpath.evaluate(expression, self, returnType);
        }
        catch (XPathExpressionException e) {
            throw new GroovyRuntimeException(e);
        }
    }

    public static String xpath(Node self, String expression) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            return xpath.evaluate(expression, self);
        }
        catch (XPathExpressionException e) {
            throw new GroovyRuntimeException(e);
        }
    }

    private static String toString(NodeList self) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Node> it = XmlGroovyMethods.iterator(self);
        while (it.hasNext()) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(it.next().toString());
        }
        sb.append("]");
        return sb.toString();
    }

    public static int size(NodeList self) {
        return self.getLength();
    }

    public static boolean isEmpty(NodeList self) {
        return DOMCategory.size(self) == 0;
    }

    private static void addResult(List results, Object result) {
        if (result != null) {
            if (result instanceof Collection) {
                results.addAll((Collection)result);
            } else {
                results.add(result);
            }
        }
    }

    private static final class NodesHolder
    implements NodeList {
        private List<Node> nodes;

        private NodesHolder(List<Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public int getLength() {
            return this.nodes.size();
        }

        @Override
        public Node item(int index) {
            if (index < 0 || index >= this.getLength()) {
                return null;
            }
            return this.nodes.get(index);
        }
    }

    private static final class NodeListsHolder
    implements NodeList {
        private List<NodeList> nodeLists;

        private NodeListsHolder(List<NodeList> nodeLists) {
            this.nodeLists = nodeLists;
        }

        @Override
        public int getLength() {
            int length = 0;
            for (NodeList nl : this.nodeLists) {
                length += nl.getLength();
            }
            return length;
        }

        @Override
        public Node item(int index) {
            int relativeIndex = index;
            for (NodeList nl : this.nodeLists) {
                if (relativeIndex < nl.getLength()) {
                    return nl.item(relativeIndex);
                }
                relativeIndex -= nl.getLength();
            }
            return null;
        }

        public String toString() {
            return DOMCategory.toString(this);
        }
    }
}

