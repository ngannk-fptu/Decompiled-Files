/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.PSVIDocumentImpl;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;

public class PSVIDOMImplementationImpl
extends DOMImplementationImpl {
    static final PSVIDOMImplementationImpl singleton = new PSVIDOMImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    @Override
    public boolean hasFeature(String string, String string2) {
        return super.hasFeature(string, string2) || string.equalsIgnoreCase("psvi");
    }

    @Override
    protected CoreDocumentImpl createDocument(DocumentType documentType) {
        return new PSVIDocumentImpl(documentType);
    }
}

