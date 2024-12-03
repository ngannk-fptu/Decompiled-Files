/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLDOMImplementation;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;

public class WMLDOMImplementationImpl
extends DOMImplementationImpl
implements WMLDOMImplementation {
    static final DOMImplementationImpl singleton = new WMLDOMImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    @Override
    protected CoreDocumentImpl createDocument(DocumentType documentType) {
        return new WMLDocumentImpl(documentType);
    }
}

