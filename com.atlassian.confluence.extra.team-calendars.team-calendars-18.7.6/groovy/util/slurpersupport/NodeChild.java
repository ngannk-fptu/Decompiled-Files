/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.util.slurpersupport.GPathResult;
import groovy.util.slurpersupport.NamespaceAwareHashMap;
import groovy.util.slurpersupport.NoChildren;
import groovy.util.slurpersupport.Node;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class NodeChild
extends GPathResult {
    private final Node node;

    public NodeChild(Node node, GPathResult parent, String namespacePrefix, Map<String, String> namespaceTagHints) {
        super(parent, node.name(), namespacePrefix, namespaceTagHints);
        this.node = node;
        ((NamespaceAwareHashMap)this.node.attributes()).setNamespaceTagHints(namespaceTagHints);
    }

    public NodeChild(Node node, GPathResult parent, Map<String, String> namespaceTagHints) {
        this(node, parent, "*", namespaceTagHints);
    }

    @Override
    public GPathResult parent() {
        if (this.node.parent() != null) {
            return new NodeChild(this.node.parent(), this, this.namespaceTagHints);
        }
        return this;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public String text() {
        return this.node.text();
    }

    public List<String> localText() {
        return this.node.localText();
    }

    public String namespaceURI() {
        return this.node.namespaceURI();
    }

    @Override
    public GPathResult parents() {
        throw new GroovyRuntimeException("parents() not implemented yet");
    }

    @Override
    public Iterator iterator() {
        return new Iterator(){
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return this.hasNext;
            }

            public Object next() {
                try {
                    NodeChild nodeChild = this.hasNext ? NodeChild.this : null;
                    return nodeChild;
                }
                finally {
                    this.hasNext = false;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Iterator nodeIterator() {
        return new Iterator(){
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return this.hasNext;
            }

            public Object next() {
                try {
                    Node node = this.hasNext ? NodeChild.this.node : null;
                    return node;
                }
                finally {
                    this.hasNext = false;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Object getAt(int index) {
        if (index == 0) {
            return this.node;
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    public Map attributes() {
        return this.node.attributes();
    }

    @Override
    public Iterator childNodes() {
        return this.node.childNodes();
    }

    @Override
    public GPathResult find(Closure closure) {
        if (DefaultTypeTransformation.castToBoolean(closure.call(new Object[]{this.node}))) {
            return this;
        }
        return new NoChildren(this, "", this.namespaceTagHints);
    }

    @Override
    public GPathResult findAll(Closure closure) {
        return this.find(closure);
    }

    @Override
    public void build(GroovyObject builder) {
        this.node.build(builder, this.namespaceMap, this.namespaceTagHints);
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        return this.node.writeTo(out);
    }

    @Override
    protected void replaceNode(Closure newValue) {
        this.node.replaceNode(newValue, this);
    }

    @Override
    protected void replaceBody(Object newValue) {
        this.node.replaceBody(newValue);
    }

    @Override
    protected void appendNode(Object newValue) {
        this.node.appendNode(newValue, this);
    }
}

