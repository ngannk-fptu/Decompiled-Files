/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;

public class ConcreteGraphicsNodeRableFactory
implements GraphicsNodeRableFactory {
    @Override
    public GraphicsNodeRable createGraphicsNodeRable(GraphicsNode node) {
        return (GraphicsNodeRable)node.getGraphicsNodeRable(true);
    }
}

