/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.duplicatetags.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Node {
    Type tagType;
    boolean isEmptyTag;
    int tagIndex;
    long elementHash;
    String tagName;
    boolean markedAsDuplicate = false;
    Node parentNode;
    Node closingTag;
    List<Node> children = new ArrayList<Node>();

    Node(String tagName, int index, long elementHash, Type tagType, String text) {
        this.tagName = tagName;
        this.tagIndex = index;
        this.elementHash = elementHash;
        this.tagType = tagType;
        this.isEmptyTag = Node.isEmptyTag(tagType, text);
    }

    Node addChild(Node node) {
        this.children.add(node);
        node.parentNode = this;
        return node;
    }

    void addClosingTag(Node node) {
        this.closingTag = node;
        node.parentNode = this;
    }

    public List<Node> getEffectiveChildren() {
        return this.children.stream().filter(child -> !child.isEmptyTag).collect(Collectors.toList());
    }

    private static boolean isEmptyTag(Type type, String text) {
        if (!type.equals((Object)Type.ANY_OTHER_TAG)) {
            return false;
        }
        return text != null && text.trim().isEmpty();
    }

    public static enum Type {
        START_TAG,
        END_TAG,
        ANY_OTHER_TAG;

    }
}

