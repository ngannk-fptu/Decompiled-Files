/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom;

import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.dom.TextNode;

public class SeparatingNode
extends TextNode {
    public SeparatingNode(TagNode parent) {
        super(parent, "");
    }

    public boolean equals(Object other) {
        return other == this;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }
}

