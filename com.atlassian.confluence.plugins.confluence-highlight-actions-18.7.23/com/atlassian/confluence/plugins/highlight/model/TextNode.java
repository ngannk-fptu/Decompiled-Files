/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.model;

import org.w3c.dom.Node;

public class TextNode {
    private final Node node;
    private final int startIndex;
    private final boolean modifiable;

    public TextNode(Node node, int startIndex, boolean modifiable) {
        this.node = node;
        this.startIndex = startIndex;
        this.modifiable = modifiable;
    }

    public Node getNode() {
        return this.node;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public boolean isModifiable() {
        return this.modifiable;
    }
}

