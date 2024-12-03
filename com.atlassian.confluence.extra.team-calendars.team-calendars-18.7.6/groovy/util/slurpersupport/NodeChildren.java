/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import groovy.lang.Buildable;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.util.slurpersupport.FilteredNodeChildren;
import groovy.util.slurpersupport.GPathResult;
import groovy.util.slurpersupport.NoChildren;
import groovy.util.slurpersupport.Node;
import groovy.util.slurpersupport.NodeChild;
import groovy.util.slurpersupport.NodeIterator;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class NodeChildren
extends GPathResult {
    private int size = -1;

    public NodeChildren(GPathResult parent, String name, String namespacePrefix, Map<String, String> namespaceTagHints) {
        super(parent, name, namespacePrefix, namespaceTagHints);
    }

    public NodeChildren(GPathResult parent, String name, Map<String, String> namespaceTagHints) {
        this(parent, name, "*", namespaceTagHints);
    }

    public NodeChildren(GPathResult parent, Map<String, String> namespaceTagHints) {
        this(parent, "*", namespaceTagHints);
    }

    @Override
    public Iterator childNodes() {
        return new Iterator(){
            private final Iterator iter;
            private Iterator childIter;
            {
                this.iter = NodeChildren.this.nodeIterator();
                this.childIter = this.nextChildIter();
            }

            @Override
            public boolean hasNext() {
                return this.childIter != null;
            }

            public Object next() {
                while (this.childIter != null) {
                    try {
                        if (!this.childIter.hasNext()) continue;
                        Object e = this.childIter.next();
                        return e;
                    }
                    finally {
                        if (this.childIter.hasNext()) continue;
                        this.childIter = this.nextChildIter();
                    }
                }
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private Iterator nextChildIter() {
                while (this.iter.hasNext()) {
                    Node node = (Node)this.iter.next();
                    Iterator result = node.childNodes();
                    if (!result.hasNext()) continue;
                    return result;
                }
                return null;
            }
        };
    }

    @Override
    public Iterator iterator() {
        return new Iterator(){
            final Iterator iter;
            {
                this.iter = NodeChildren.this.nodeIterator();
            }

            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public Object next() {
                return new NodeChild((Node)this.iter.next(), NodeChildren.this.pop(), NodeChildren.this.namespaceTagHints);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Iterator nodeIterator() {
        if ("*".equals(this.name)) {
            return this.parent.childNodes();
        }
        return new NodeIterator(this.parent.childNodes()){

            @Override
            protected Object getNextNode(Iterator iter) {
                while (iter.hasNext()) {
                    Node node = (Node)iter.next();
                    if (!NodeChildren.this.name.equals(node.name()) || !"*".equals(NodeChildren.this.namespacePrefix) && (!"".equals(NodeChildren.this.namespacePrefix) || !"".equals(node.namespaceURI())) && !node.namespaceURI().equals(NodeChildren.this.namespaceMap.get(NodeChildren.this.namespacePrefix))) continue;
                    return node;
                }
                return null;
            }
        };
    }

    @Override
    public GPathResult parents() {
        throw new GroovyRuntimeException("parents() not implemented yet");
    }

    @Override
    public synchronized int size() {
        if (this.size == -1) {
            Iterator iter = this.iterator();
            this.size = 0;
            while (iter.hasNext()) {
                iter.next();
                ++this.size;
            }
        }
        return this.size;
    }

    @Override
    public String text() {
        StringBuilder buf = new StringBuilder();
        Iterator iter = this.nodeIterator();
        while (iter.hasNext()) {
            buf.append(((Node)iter.next()).text());
        }
        return buf.toString();
    }

    @Override
    public GPathResult find(Closure closure) {
        for (Object node : this) {
            if (!DefaultTypeTransformation.castToBoolean(closure.call(new Object[]{node}))) continue;
            return (GPathResult)node;
        }
        return new NoChildren(this, this.name, this.namespaceTagHints);
    }

    @Override
    public GPathResult findAll(Closure closure) {
        return new FilteredNodeChildren((GPathResult)this, closure, (Map<String, String>)this.namespaceTagHints);
    }

    @Override
    public void build(GroovyObject builder) {
        Iterator iter = this.nodeIterator();
        while (iter.hasNext()) {
            Object next = iter.next();
            if (next instanceof Buildable) {
                ((Buildable)next).build(builder);
                continue;
            }
            ((Node)next).build(builder, this.namespaceMap, this.namespaceTagHints);
        }
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        Iterator iter = this.nodeIterator();
        while (iter.hasNext()) {
            ((Node)iter.next()).writeTo(out);
        }
        return out;
    }

    @Override
    protected void replaceNode(Closure newValue) {
        for (NodeChild result : this) {
            result.replaceNode(newValue);
        }
    }

    @Override
    protected void replaceBody(Object newValue) {
        for (NodeChild result : this) {
            result.replaceBody(newValue);
        }
    }

    @Override
    protected void appendNode(Object newValue) {
        for (NodeChild result : this) {
            result.appendNode(newValue);
        }
    }
}

