/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;

public class StyleReference {
    private GraphicsNode node;
    private String styleAttribute;

    public StyleReference(GraphicsNode node, String styleAttribute) {
        this.node = node;
        this.styleAttribute = styleAttribute;
    }

    public GraphicsNode getGraphicsNode() {
        return this.node;
    }

    public String getStyleAttribute() {
        return this.styleAttribute;
    }
}

