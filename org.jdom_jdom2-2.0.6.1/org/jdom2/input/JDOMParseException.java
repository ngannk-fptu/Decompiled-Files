/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.xml.sax.SAXParseException;

public class JDOMParseException
extends JDOMException {
    private static final long serialVersionUID = 200L;
    private final Document partialDocument;

    public JDOMParseException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public JDOMParseException(String message, Throwable cause, Document partialDocument) {
        super(message, cause);
        this.partialDocument = partialDocument;
    }

    public Document getPartialDocument() {
        return this.partialDocument;
    }

    public String getPublicId() {
        return this.getCause() instanceof SAXParseException ? ((SAXParseException)this.getCause()).getPublicId() : null;
    }

    public String getSystemId() {
        return this.getCause() instanceof SAXParseException ? ((SAXParseException)this.getCause()).getSystemId() : null;
    }

    public int getLineNumber() {
        return this.getCause() instanceof SAXParseException ? ((SAXParseException)this.getCause()).getLineNumber() : -1;
    }

    public int getColumnNumber() {
        return this.getCause() instanceof SAXParseException ? ((SAXParseException)this.getCause()).getColumnNumber() : -1;
    }
}

