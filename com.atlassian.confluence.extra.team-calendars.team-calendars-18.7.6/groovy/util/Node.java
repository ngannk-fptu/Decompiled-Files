/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.util.NodeBuilder;
import groovy.util.NodeList;
import groovy.util.NodePrinter;
import groovy.xml.QName;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.InvokerHelper;

public class Node
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 4121134753270542643L;
    private Node parent;
    private Object name;
    private Map attributes;
    private Object value;

    public Object clone() {
        Object newValue = this.value;
        if (this.value != null && this.value instanceof NodeList) {
            NodeList nodes = (NodeList)this.value;
            newValue = nodes.clone();
        }
        return new Node(null, this.name, new HashMap(this.attributes), newValue);
    }

    public Node(Node parent, Object name) {
        this(parent, name, new NodeList());
    }

    public Node(Node parent, Object name, Object value) {
        this(parent, name, new HashMap(), value);
    }

    public Node(Node parent, Object name, Map attributes) {
        this(parent, name, attributes, new NodeList());
    }

    public Node(Node parent, Object name, Map attributes, Object value) {
        this.parent = parent;
        this.name = name;
        this.attributes = attributes;
        this.value = value;
        if (parent != null) {
            Node.getParentList(parent).add(this);
        }
    }

    private static List getParentList(Node parent) {
        List parentList;
        Object parentValue = parent.value();
        if (parentValue instanceof List) {
            parentList = (List)parentValue;
        } else {
            parentList = new NodeList();
            parentList.add(parentValue);
            parent.setValue(parentList);
        }
        return parentList;
    }

    public boolean append(Node child) {
        child.setParent(this);
        return Node.getParentList(this).add(child);
    }

    public boolean remove(Node child) {
        child.setParent(null);
        return Node.getParentList(this).remove(child);
    }

    public Node appendNode(Object name, Map attributes) {
        return new Node(this, name, attributes);
    }

    public Node appendNode(Object name) {
        return new Node(this, name);
    }

    public Node appendNode(Object name, Object value) {
        return new Node(this, name, value);
    }

    public Node appendNode(Object name, Map attributes, Object value) {
        return new Node(this, name, attributes, value);
    }

    public Node replaceNode(Closure c) {
        if (this.parent() == null) {
            throw new UnsupportedOperationException("Replacing the root node is not supported");
        }
        this.appendNodes(c);
        Node.getParentList(this.parent()).remove(this);
        this.setParent(null);
        return this;
    }

    public Node replaceNode(Node n) {
        if (this.parent() == null) {
            throw new UnsupportedOperationException("Replacing the root node is not supported");
        }
        List tail = this.getTail();
        this.parent().appendNode(n.name(), n.attributes(), n.value());
        this.parent().children().addAll(tail);
        Node.getParentList(this.parent()).remove(this);
        this.setParent(null);
        return this;
    }

    private List getTail() {
        List list = this.parent().children();
        int afterIndex = list.indexOf(this);
        ArrayList tail = new ArrayList(list.subList(afterIndex + 1, list.size()));
        list.subList(afterIndex + 1, list.size()).clear();
        return tail;
    }

    public void plus(Closure c) {
        if (this.parent() == null) {
            throw new UnsupportedOperationException("Adding sibling nodes to the root node is not supported");
        }
        this.appendNodes(c);
    }

    private void appendNodes(Closure c) {
        List tail = this.getTail();
        for (Node child : Node.buildChildrenFromClosure(c)) {
            this.parent().appendNode(child.name(), child.attributes(), child.value());
        }
        this.parent().children().addAll(tail);
    }

    private static List<Node> buildChildrenFromClosure(Closure c) {
        NodeBuilder b = new NodeBuilder();
        Node newNode = (Node)b.invokeMethod("dummyNode", c);
        return newNode.children();
    }

    protected static void setMetaClass(MetaClass metaClass, Class nodeClass) {
        DelegatingMetaClass newMetaClass = new DelegatingMetaClass(metaClass){

            @Override
            public Object getAttribute(Object object, String attribute) {
                Node n = (Node)object;
                return n.get("@" + attribute);
            }

            @Override
            public void setAttribute(Object object, String attribute, Object newValue) {
                Node n = (Node)object;
                n.attributes().put(attribute, newValue);
            }

            @Override
            public Object getProperty(Object object, String property) {
                if (object instanceof Node) {
                    Node n = (Node)object;
                    return n.get(property);
                }
                return super.getProperty(object, property);
            }

            @Override
            public void setProperty(Object object, String property, Object newValue) {
                if (property.startsWith("@")) {
                    this.setAttribute(object, property.substring(1), newValue);
                    return;
                }
                this.delegate.setProperty(object, property, newValue);
            }
        };
        GroovySystem.getMetaClassRegistry().setMetaClass(nodeClass, newMetaClass);
    }

    public String text() {
        if (this.value instanceof String) {
            return (String)this.value;
        }
        if (this.value instanceof NodeList) {
            return ((NodeList)this.value).text();
        }
        if (this.value instanceof Collection) {
            Collection coll = (Collection)this.value;
            String previousText = null;
            StringBuilder sb = null;
            for (Object child : coll) {
                String childText = null;
                if (child instanceof String) {
                    childText = (String)child;
                } else if (child instanceof Node) {
                    childText = ((Node)child).text();
                }
                if (childText == null) continue;
                if (previousText == null) {
                    previousText = childText;
                    continue;
                }
                if (sb == null) {
                    sb = new StringBuilder();
                    sb.append(previousText);
                }
                sb.append(childText);
            }
            if (sb != null) {
                return sb.toString();
            }
            if (previousText != null) {
                return previousText;
            }
            return "";
        }
        return "" + this.value;
    }

    public Iterator iterator() {
        return this.children().iterator();
    }

    public List children() {
        if (this.value == null) {
            return new NodeList();
        }
        if (this.value instanceof List) {
            return (List)this.value;
        }
        NodeList result = new NodeList();
        result.add(this.value);
        return result;
    }

    public Map attributes() {
        return this.attributes;
    }

    public Object attribute(Object key) {
        return this.attributes != null ? this.attributes.get(key) : null;
    }

    public Object name() {
        return this.name;
    }

    public Object value() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Node parent() {
        return this.parent;
    }

    protected void setParent(Node parent) {
        this.parent = parent;
    }

    public Object get(String key) {
        if (key != null && key.charAt(0) == '@') {
            String attributeName = key.substring(1);
            return this.attributes().get(attributeName);
        }
        if ("..".equals(key)) {
            return this.parent();
        }
        if ("*".equals(key)) {
            return this.children();
        }
        if ("**".equals(key)) {
            return this.depthFirst();
        }
        return this.getByName(key);
    }

    public NodeList getAt(QName name) {
        NodeList answer = new NodeList();
        for (Object child : this.children()) {
            Node childNode;
            Object childNodeName;
            if (!(child instanceof Node) || !name.matches(childNodeName = (childNode = (Node)child).name())) continue;
            answer.add(childNode);
        }
        return answer;
    }

    private NodeList getByName(String name) {
        NodeList answer = new NodeList();
        for (Object child : this.children()) {
            if (!(child instanceof Node)) continue;
            Node childNode = (Node)child;
            Object childNodeName = childNode.name();
            if (childNodeName instanceof QName) {
                QName qn = (QName)childNodeName;
                if (!qn.matches(name)) continue;
                answer.add(childNode);
                continue;
            }
            if (!name.equals(childNodeName)) continue;
            answer.add(childNode);
        }
        return answer;
    }

    public List depthFirst() {
        NodeList answer = new NodeList();
        answer.add(this);
        answer.addAll(this.depthFirstRest());
        return answer;
    }

    private List depthFirstRest() {
        NodeList answer = new NodeList();
        Iterator<Object> iter = InvokerHelper.asIterator(this.value);
        while (iter.hasNext()) {
            Object child = iter.next();
            if (child instanceof Node) {
                Node childNode = (Node)child;
                List children = childNode.depthFirstRest();
                answer.add(childNode);
                if (children.size() <= 1 && (children.size() != 1 || children.get(0) instanceof String)) continue;
                answer.addAll(children);
                continue;
            }
            if (!(child instanceof String)) continue;
            answer.add(child);
        }
        return answer;
    }

    public List breadthFirst() {
        NodeList answer = new NodeList();
        answer.add(this);
        answer.addAll(this.breadthFirstRest());
        return answer;
    }

    private List breadthFirstRest() {
        NodeList answer = new NodeList();
        List nextLevelChildren = this.getDirectChildren();
        while (!nextLevelChildren.isEmpty()) {
            NodeList working = new NodeList((Collection)nextLevelChildren);
            nextLevelChildren = new NodeList();
            for (Object child : working) {
                Node childNode;
                List children;
                answer.add(child);
                if (!(child instanceof Node) || (children = (childNode = (Node)child).getDirectChildren()).size() <= 1 && (children.size() != 1 || children.get(0) instanceof String)) continue;
                nextLevelChildren.addAll(children);
            }
        }
        return answer;
    }

    public List<String> localText() {
        ArrayList<String> answer = new ArrayList<String>();
        Iterator<Object> iter = InvokerHelper.asIterator(this.value);
        while (iter.hasNext()) {
            Object child = iter.next();
            if (child instanceof Node) continue;
            answer.add(child.toString());
        }
        return answer;
    }

    private List getDirectChildren() {
        NodeList answer = new NodeList();
        Iterator<Object> iter = InvokerHelper.asIterator(this.value);
        while (iter.hasNext()) {
            Object child = iter.next();
            if (child instanceof Node) {
                Node childNode = (Node)child;
                answer.add(childNode);
                continue;
            }
            if (!(child instanceof String)) continue;
            answer.add(child);
        }
        return answer;
    }

    public String toString() {
        return this.name + "[attributes=" + this.attributes + "; value=" + this.value + "]";
    }

    public void print(PrintWriter out) {
        new NodePrinter(out).print(this);
    }

    static {
        Node.setMetaClass(GroovySystem.getMetaClassRegistry().getMetaClass(Node.class), Node.class);
    }
}

