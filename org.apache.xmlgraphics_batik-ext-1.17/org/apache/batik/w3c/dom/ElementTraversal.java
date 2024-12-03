/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.w3c.dom;

import org.w3c.dom.Element;

public interface ElementTraversal {
    public Element getFirstElementChild();

    public Element getLastElementChild();

    public Element getNextElementSibling();

    public Element getPreviousElementSibling();

    public int getChildElementCount();
}

