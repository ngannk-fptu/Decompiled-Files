/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.xml.XMLUtilities
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDOMImplementation;
import org.apache.batik.dom.GenericDocument;
import org.apache.batik.dom.GenericDocumentType;
import org.apache.batik.xml.XMLUtilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

public class GenericDOMImplementation
extends AbstractDOMImplementation {
    protected static final DOMImplementation DOM_IMPLEMENTATION = new GenericDOMImplementation();

    public static DOMImplementation getDOMImplementation() {
        return DOM_IMPLEMENTATION;
    }

    @Override
    public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
        GenericDocument result = new GenericDocument(doctype, this);
        result.appendChild(result.createElementNS(namespaceURI, qualifiedName));
        return result;
    }

    @Override
    public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) {
        int test;
        if (qualifiedName == null) {
            qualifiedName = "";
        }
        if (((test = XMLUtilities.testXMLQName((String)qualifiedName)) & 1) == 0) {
            throw new DOMException(5, this.formatMessage("xml.name", new Object[]{qualifiedName}));
        }
        if ((test & 2) == 0) {
            throw new DOMException(5, this.formatMessage("invalid.qname", new Object[]{qualifiedName}));
        }
        return new GenericDocumentType(qualifiedName, publicId, systemId);
    }
}

