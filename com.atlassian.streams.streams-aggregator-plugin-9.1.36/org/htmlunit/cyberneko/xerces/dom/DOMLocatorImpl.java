/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.w3c.dom.DOMLocator;
import org.w3c.dom.Node;

public class DOMLocatorImpl
implements DOMLocator {
    public int fColumnNumber = -1;
    public int fLineNumber = -1;
    public String fUri = null;
    public final int fByteOffset = -1;
    public int fUtf16Offset = -1;

    public DOMLocatorImpl() {
    }

    public DOMLocatorImpl(int lineNumber, int columnNumber, int utf16Offset, String uri) {
        this.fLineNumber = lineNumber;
        this.fColumnNumber = columnNumber;
        this.fUri = uri;
        this.fUtf16Offset = utf16Offset;
    }

    @Override
    public int getLineNumber() {
        return this.fLineNumber;
    }

    @Override
    public int getColumnNumber() {
        return this.fColumnNumber;
    }

    @Override
    public String getUri() {
        return this.fUri;
    }

    @Override
    public Node getRelatedNode() {
        return null;
    }

    @Override
    public int getByteOffset() {
        return -1;
    }

    @Override
    public int getUtf16Offset() {
        return this.fUtf16Offset;
    }
}

