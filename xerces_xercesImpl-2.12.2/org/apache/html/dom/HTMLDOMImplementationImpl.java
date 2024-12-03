/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLDOMImplementation;
import org.w3c.dom.html.HTMLDocument;

public class HTMLDOMImplementationImpl
extends DOMImplementationImpl
implements HTMLDOMImplementation {
    private static final HTMLDOMImplementation _instance = new HTMLDOMImplementationImpl();

    private HTMLDOMImplementationImpl() {
    }

    @Override
    public final HTMLDocument createHTMLDocument(String string) throws DOMException {
        if (string == null) {
            throw new NullPointerException("HTM014 Argument 'title' is null.");
        }
        HTMLDocumentImpl hTMLDocumentImpl = new HTMLDocumentImpl();
        hTMLDocumentImpl.setTitle(string);
        return hTMLDocumentImpl;
    }

    public static HTMLDOMImplementation getHTMLDOMImplementation() {
        return _instance;
    }
}

