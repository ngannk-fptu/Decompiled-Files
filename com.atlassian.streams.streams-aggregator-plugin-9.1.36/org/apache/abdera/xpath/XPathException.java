/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.xpath;

public class XPathException
extends RuntimeException {
    private static final long serialVersionUID = 7373747391262088925L;

    public XPathException() {
    }

    public XPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public XPathException(String message) {
        super(message);
    }

    public XPathException(Throwable cause) {
        super(cause);
    }
}

