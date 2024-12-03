/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom;

import org.w3c.dom.Node;

public interface DOMLocator {
    public int getLineNumber();

    public int getColumnNumber();

    public int getByteOffset();

    public int getUtf16Offset();

    public Node getRelatedNode();

    public String getUri();
}

