/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.Node;

public interface HTMLCollection {
    public int getLength();

    public Node item(int var1);

    public Node namedItem(String var1);
}

