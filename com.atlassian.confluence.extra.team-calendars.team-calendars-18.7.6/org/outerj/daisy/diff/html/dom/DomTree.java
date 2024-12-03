/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom;

import java.util.List;
import org.outerj.daisy.diff.html.dom.BodyNode;
import org.outerj.daisy.diff.html.dom.TextNode;

public interface DomTree {
    public List<TextNode> getTextNodes();

    public BodyNode getBodyNode();
}

