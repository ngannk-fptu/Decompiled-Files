/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.model;

import com.atlassian.confluence.plugins.highlight.model.TextNode;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;

public class TextCollection {
    private final StringBuilder aggregatedText = new StringBuilder();
    private final List<TextNode> positions = new ArrayList<TextNode>();

    public void add(Node node, boolean allowInsertion) {
        this.positions.add(new TextNode(node, this.aggregatedText.length(), allowInsertion));
        this.aggregatedText.append(node.getNodeValue());
    }

    public String getAggregatedText() {
        return this.aggregatedText.toString();
    }

    public List<TextNode> getPositions() {
        return this.positions;
    }
}

