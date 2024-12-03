/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMImplementationImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLDOMImplementation;
import org.w3c.dom.html.HTMLDocument;

public final class HTMLDOMImplementationImpl
extends DOMImplementationImpl
implements HTMLDOMImplementation {
    private static final HTMLDOMImplementation _instance = new HTMLDOMImplementationImpl();

    private HTMLDOMImplementationImpl() {
    }

    @Override
    public HTMLDocument createHTMLDocument(String title) throws DOMException {
        if (title == null) {
            throw new NullPointerException("HTM014 Argument 'title' is null.");
        }
        HTMLDocumentImpl doc = new HTMLDocumentImpl();
        doc.setTitle(title);
        return doc;
    }

    public static HTMLDOMImplementation getHTMLDOMImplementation() {
        return _instance;
    }
}

