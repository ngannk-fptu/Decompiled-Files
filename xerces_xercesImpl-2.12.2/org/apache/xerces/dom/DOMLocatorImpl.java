/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.w3c.dom.DOMLocator;
import org.w3c.dom.Node;

public class DOMLocatorImpl
implements DOMLocator {
    public int fColumnNumber = -1;
    public int fLineNumber = -1;
    public Node fRelatedNode = null;
    public String fUri = null;
    public int fByteOffset = -1;
    public int fUtf16Offset = -1;

    public DOMLocatorImpl() {
    }

    public DOMLocatorImpl(int n, int n2, String string) {
        this.fLineNumber = n;
        this.fColumnNumber = n2;
        this.fUri = string;
    }

    public DOMLocatorImpl(int n, int n2, int n3, String string) {
        this.fLineNumber = n;
        this.fColumnNumber = n2;
        this.fUri = string;
        this.fUtf16Offset = n3;
    }

    public DOMLocatorImpl(int n, int n2, int n3, Node node, String string) {
        this.fLineNumber = n;
        this.fColumnNumber = n2;
        this.fByteOffset = n3;
        this.fRelatedNode = node;
        this.fUri = string;
    }

    public DOMLocatorImpl(int n, int n2, int n3, Node node, String string, int n4) {
        this.fLineNumber = n;
        this.fColumnNumber = n2;
        this.fByteOffset = n3;
        this.fRelatedNode = node;
        this.fUri = string;
        this.fUtf16Offset = n4;
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

