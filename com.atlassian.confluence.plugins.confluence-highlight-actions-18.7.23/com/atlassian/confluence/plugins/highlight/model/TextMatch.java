/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.model;

import com.atlassian.confluence.plugins.highlight.model.TextNode;
import java.util.List;

public class TextMatch {
    private final int firstNodeStartIndex;
    private final int lastNodeEndIndex;
    private final List<TextNode> matchingNodes;

    public TextMatch(int firstNodeStartIndex, int lastNodeEndIndex, List<TextNode> matchingNodes) {
        this.firstNodeStartIndex = firstNodeStartIndex;
        this.lastNodeEndIndex = lastNodeEndIndex;
        this.matchingNodes = matchingNodes;
    }

    public TextNode getLastMatchingItem() {
        return this.matchingNodes.get(this.matchingNodes.size() - 1);
    }

    public int getFirstNodeStartIndex() {
        return this.firstNodeStartIndex;
    }

    public int getLastNodeEndIndex() {
        return this.lastNodeEndIndex;
    }

    public List<TextNode> getMatchingNodes() {
        return this.matchingNodes;
    }
}

