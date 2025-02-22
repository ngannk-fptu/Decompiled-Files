/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer.dom3;

import org.w3c.dom.DOMLocator;
import org.w3c.dom.Node;

final class DOMLocatorImpl
implements DOMLocator {
    private final int fColumnNumber;
    private final int fLineNumber;
    private final Node fRelatedNode;
    private final String fUri;
    private final int fByteOffset;
    private final int fUtf16Offset;

    DOMLocatorImpl() {
        this.fColumnNumber = -1;
        this.fLineNumber = -1;
        this.fRelatedNode = null;
        this.fUri = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
    }

    DOMLocatorImpl(int lineNumber, int columnNumber, String uri) {
        this.fLineNumber = lineNumber;
        this.fColumnNumber = columnNumber;
        this.fUri = uri;
        this.fRelatedNode = null;
        this.fByteOffset = -1;
        this.fUtf16Offset = -1;
    }

    DOMLocatorImpl(int lineNumber, int columnNumber, int utf16Offset, String uri) {
        this.fLineNumber = lineNumber;
        this.fColumnNumber = columnNumber;
        this.fUri = uri;
        this.fUtf16Offset = utf16Offset;
        this.fRelatedNode = null;
        this.fByteOffset = -1;
    }

    DOMLocatorImpl(int lineNumber, int columnNumber, int byteoffset, Node relatedData, String uri) {
        this.fLineNumber = lineNumber;
        this.fColumnNumber = columnNumber;
        this.fByteOffset = byteoffset;
        this.fRelatedNode = relatedData;
        this.fUri = uri;
        this.fUtf16Offset = -1;
    }

    DOMLocatorImpl(int lineNumber, int columnNumber, int byteoffset, Node relatedData, String uri, int utf16Offset) {
        this.fLineNumber = lineNumber;
        this.fColumnNumber = columnNumber;
        this.fByteOffset = byteoffset;
        this.fRelatedNode = relatedData;
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
        return this.fRelatedNode;
    }

    @Override
    public int getByteOffset() {
        return this.fByteOffset;
    }

    @Override
    public int getUtf16Offset() {
        return this.fUtf16Offset;
    }
}

