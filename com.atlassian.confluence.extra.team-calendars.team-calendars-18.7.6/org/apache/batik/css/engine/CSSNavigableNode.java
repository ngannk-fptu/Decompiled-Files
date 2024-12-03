/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine;

import org.w3c.dom.Node;

public interface CSSNavigableNode {
    public Node getCSSParentNode();

    public Node getCSSPreviousSibling();

    public Node getCSSNextSibling();

    public Node getCSSFirstChild();

    public Node getCSSLastChild();

    public boolean isHiddenFromSelectors();
}

