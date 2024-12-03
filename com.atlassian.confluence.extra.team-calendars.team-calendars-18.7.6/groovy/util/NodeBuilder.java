/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.util.BuilderSupport;
import groovy.util.Node;
import java.util.Map;

public class NodeBuilder
extends BuilderSupport {
    public static NodeBuilder newInstance() {
        return new NodeBuilder();
    }

    @Override
    protected void setParent(Object parent, Object child) {
    }

    @Override
    protected Object createNode(Object name) {
        return new Node(this.getCurrentNode(), name);
    }

    @Override
    protected Object createNode(Object name, Object value) {
        return new Node(this.getCurrentNode(), name, value);
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        return new Node(this.getCurrentNode(), name, attributes);
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        return new Node(this.getCurrentNode(), name, attributes, value);
    }

    protected Node getCurrentNode() {
        return (Node)this.getCurrent();
    }
}

