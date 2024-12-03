/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.xml.MacroStateTracker;
import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTrackerV2;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import org.w3c.dom.Node;

public class MarkStateTracker
implements ModificationStateTrackerV2 {
    private static Set<String> allowedTags = ImmutableSet.of((Object)"root", (Object)"div", (Object)"p", (Object)"span", (Object)"strong", (Object)"em", (Object[])new String[]{"u", "i", "small", "ol", "ul", "li", "table", "tbody", "thead", "tr", "th", "td", "h1", "h2", "h3", "h4", "h5", "h6", "sub", "sup", "blockquote", "a", "code", "pre", "big"});
    private static Set<String> allowedAcTags = ImmutableSet.of((Object)"ac:layout-cell", (Object)"ac:rich-text-body", (Object)"ac:task-body", (Object)"ac:inline-comment-marker", (Object)"ac:structured-macro", (Object)"ac:parameter", (Object[])new String[0]);
    private static Set<String> inheritAllowedAcTags = ImmutableSet.of((Object)"ac:link");
    private static Set<String> textAcTags = ImmutableSet.of((Object)"ac:plain-text-link-body", (Object)"ac:plain-text-body", (Object)"ac:link-body");
    private Deque<Boolean> allowed = new ArrayDeque<Boolean>();
    private Deque<Boolean> text = new ArrayDeque<Boolean>();
    private MacroStateTracker macroStateTracker;
    private boolean isInheritAllowanceEnable;

    public MarkStateTracker() {
        this.allowed.push(Boolean.TRUE);
        this.text.push(Boolean.TRUE);
        this.macroStateTracker = new MacroStateTracker();
    }

    @Override
    public boolean shouldProcessText(Node node) {
        boolean shouldProcess;
        boolean bl = shouldProcess = (node.getNodeType() == 3 || node.getNodeType() == 4) && this.text.peek() != false;
        shouldProcess = this.isInheritAllowanceEnable ? shouldProcess && !this.allowed.contains(false) : shouldProcess;
        shouldProcess = shouldProcess && this.macroStateTracker.shouldProcessText(node);
        return shouldProcess;
    }

    private boolean isAcTag(String tagName) {
        return tagName.startsWith("ac:");
    }

    @Override
    public void forward(Node node, String tagName) {
        if (inheritAllowedAcTags.contains(tagName)) {
            this.isInheritAllowanceEnable = true;
        }
        this.macroStateTracker.forward(node, tagName);
        if (this.isAcTag(tagName)) {
            this.text.push(textAcTags.contains(tagName) || allowedAcTags.contains(tagName));
        }
        this.allowed.push(allowedTags.contains(tagName) || allowedAcTags.contains(tagName));
    }

    @Override
    public void back(Node node, String tagName) {
        if (inheritAllowedAcTags.contains(tagName)) {
            this.isInheritAllowanceEnable = false;
        }
        this.macroStateTracker.back(node, tagName);
        if (this.isAcTag(tagName)) {
            this.text.pop();
        }
        this.allowed.pop();
    }

    @Override
    public boolean allowInsertion() {
        return this.allowed.peek();
    }
}

