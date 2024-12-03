/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Attribute
 *  org.jdom.CDATA
 *  org.jdom.Comment
 *  org.jdom.DocType
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.EntityRef
 *  org.jdom.ProcessingInstruction
 *  org.jdom.Text
 *  org.jdom.output.XMLOutputter
 */
package org.apache.velocity.anakia;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.velocity.anakia.XPathCache;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.output.XMLOutputter;

public class NodeList
implements List,
Cloneable {
    private static final AttributeXMLOutputter DEFAULT_OUTPUTTER = new AttributeXMLOutputter();
    private List nodes;

    public NodeList() {
        this.nodes = new ArrayList();
    }

    public NodeList(Document document) {
        this((Object)document);
    }

    public NodeList(Element element) {
        this((Object)element);
    }

    private NodeList(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot construct NodeList with null.");
        }
        this.nodes = new ArrayList(1);
        this.nodes.add(object);
    }

    public NodeList(List nodes) {
        this(nodes, true);
    }

    public NodeList(List nodes, boolean copy) {
        if (nodes == null) {
            throw new IllegalArgumentException("Cannot initialize NodeList with null list");
        }
        this.nodes = copy ? new ArrayList(nodes) : nodes;
    }

    public List getList() {
        return this.nodes;
    }

    public String toString() {
        if (this.nodes.isEmpty()) {
            return "";
        }
        StringWriter sw = new StringWriter(this.nodes.size() * 128);
        try {
            Iterator i = this.nodes.iterator();
            while (i.hasNext()) {
                Object node = i.next();
                if (node instanceof Element) {
                    DEFAULT_OUTPUTTER.output((Element)node, sw);
                    continue;
                }
                if (node instanceof Attribute) {
                    DEFAULT_OUTPUTTER.output((Attribute)node, sw);
                    continue;
                }
                if (node instanceof Text) {
                    DEFAULT_OUTPUTTER.output((Text)node, sw);
                    continue;
                }
                if (node instanceof Document) {
                    DEFAULT_OUTPUTTER.output((Document)node, sw);
                    continue;
                }
                if (node instanceof ProcessingInstruction) {
                    DEFAULT_OUTPUTTER.output((ProcessingInstruction)node, sw);
                    continue;
                }
                if (node instanceof Comment) {
                    DEFAULT_OUTPUTTER.output((Comment)node, sw);
                    continue;
                }
                if (node instanceof CDATA) {
                    DEFAULT_OUTPUTTER.output((CDATA)node, sw);
                    continue;
                }
                if (node instanceof DocType) {
                    DEFAULT_OUTPUTTER.output((DocType)node, sw);
                    continue;
                }
                if (node instanceof EntityRef) {
                    DEFAULT_OUTPUTTER.output((EntityRef)node, sw);
                    continue;
                }
                throw new IllegalArgumentException("Cannot process a " + (node == null ? "null node" : "node of class " + node.getClass().getName()));
            }
        }
        catch (IOException e) {
            throw new Error();
        }
        return sw.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        NodeList clonedList = (NodeList)super.clone();
        clonedList.cloneNodes();
        return clonedList;
    }

    private void cloneNodes() throws CloneNotSupportedException {
        Class<?> listClass = this.nodes.getClass();
        try {
            List clonedNodes = (List)listClass.newInstance();
            clonedNodes.addAll(this.nodes);
            this.nodes = clonedNodes;
        }
        catch (IllegalAccessException e) {
            throw new CloneNotSupportedException("Cannot clone NodeList since there is no accessible no-arg constructor on class " + listClass.getName());
        }
        catch (InstantiationException e) {
            throw new Error();
        }
    }

    public int hashCode() {
        return ((Object)this.nodes).hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof NodeList ? ((Object)((NodeList)o).nodes).equals(this.nodes) : false;
    }

    public NodeList selectNodes(String xpathString) {
        return new NodeList(XPathCache.getXPath(xpathString).applyTo(this.nodes), false);
    }

    public boolean add(Object o) {
        return this.nodes.add(o);
    }

    public void add(int index, Object o) {
        this.nodes.add(index, o);
    }

    public boolean addAll(Collection c) {
        return this.nodes.addAll(c);
    }

    public boolean addAll(int index, Collection c) {
        return this.nodes.addAll(index, c);
    }

    public void clear() {
        this.nodes.clear();
    }

    public boolean contains(Object o) {
        return this.nodes.contains(o);
    }

    public boolean containsAll(Collection c) {
        return this.nodes.containsAll(c);
    }

    public Object get(int index) {
        return this.nodes.get(index);
    }

    public int indexOf(Object o) {
        return this.nodes.indexOf(o);
    }

    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }

    public Iterator iterator() {
        return this.nodes.iterator();
    }

    public int lastIndexOf(Object o) {
        return this.nodes.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        return this.nodes.listIterator();
    }

    public ListIterator listIterator(int index) {
        return this.nodes.listIterator(index);
    }

    public Object remove(int index) {
        return this.nodes.remove(index);
    }

    public boolean remove(Object o) {
        return this.nodes.remove(o);
    }

    public boolean removeAll(Collection c) {
        return this.nodes.removeAll(c);
    }

    public boolean retainAll(Collection c) {
        return this.nodes.retainAll(c);
    }

    public Object set(int index, Object o) {
        return this.nodes.set(index, o);
    }

    public int size() {
        return this.nodes.size();
    }

    public List subList(int fromIndex, int toIndex) {
        return new NodeList(this.nodes.subList(fromIndex, toIndex));
    }

    public Object[] toArray() {
        return this.nodes.toArray();
    }

    public Object[] toArray(Object[] a) {
        return this.nodes.toArray(a);
    }

    private static final class AttributeXMLOutputter
    extends XMLOutputter {
        private AttributeXMLOutputter() {
        }

        public void output(Attribute attribute, Writer out) throws IOException {
            out.write(" ");
            out.write(attribute.getQualifiedName());
            out.write("=");
            out.write("\"");
            out.write(this.escapeAttributeEntities(attribute.getValue()));
            out.write("\"");
        }
    }
}

