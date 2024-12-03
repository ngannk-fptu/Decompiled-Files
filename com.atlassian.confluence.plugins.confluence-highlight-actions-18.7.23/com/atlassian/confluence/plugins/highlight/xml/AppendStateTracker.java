/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTracker;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import org.w3c.dom.Node;

public class AppendStateTracker
implements ModificationStateTracker {
    private static Set<String> allowedTags = ImmutableSet.of((Object)"root", (Object)"div", (Object)"p", (Object)"span", (Object)"strong", (Object)"em", (Object[])new String[]{"u", "i", "small", "ol", "ul", "li", "table", "tbody", "thead", "tr", "th", "td", "h1", "h2", "h3", "h4", "h5", "h6", "sub", "sup", "blockquote"});
    private static Set<String> allowedAcTags = ImmutableSet.of((Object)"ac:layout-cell", (Object)"ac:rich-text-body", (Object)"ac:task-body", (Object)"ac:inline-comment-marker");
    Deque<Boolean> normalTags = new ArrayDeque<Boolean>();
    Deque<Boolean> acTags = new ArrayDeque<Boolean>();

    public AppendStateTracker() {
        this.acTags.push(Boolean.TRUE);
        this.normalTags.push(Boolean.TRUE);
    }

    @Override
    public boolean shouldProcessText(Node node) {
        return node.getNodeType() == 3 && this.acTags.peek() != false;
    }

    private boolean isAcTag(String tagName) {
        return tagName.startsWith("ac:");
    }

    @Override
    public void forward(String tagName) {
        if (this.isAcTag(tagName)) {
            this.acTags.push(allowedAcTags.contains(tagName));
        } else {
            this.normalTags.push(this.normalTags.peek() != false && allowedTags.contains(tagName));
        }
    }

    @Override
    public void back(String tagName) {
        if (this.isAcTag(tagName)) {
            this.acTags.pop();
        } else {
            this.normalTags.pop();
        }
    }

    @Override
    public boolean allowInsertion() {
        return this.acTags.peek() != false && this.normalTags.peek() != false;
    }
}

