/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.io.IOException;
import java.io.StringReader;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSNamespaceItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XSAnnotationImpl
implements XSAnnotation {
    private String fData = null;
    private SchemaGrammar fGrammar = null;

    public XSAnnotationImpl(String string, SchemaGrammar schemaGrammar) {
        this.fData = string;
        this.fGrammar = schemaGrammar;
    }

    @Override
    public boolean writeAnnotation(Object object, short s) {
        if (s == 1 || s == 3) {
            this.writeToDOM((Node)object, s);
            return true;
        }
        if (s == 2) {
            this.writeToSAX((ContentHandler)object);
            return true;
        }
        return false;
    }

    @Override
    public String getAnnotationString() {
        return this.fData;
    }

    @Override
    public short getType() {
        return 12;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }

    private synchronized void writeToSAX(ContentHandler contentHandler) {
        SAXParser sAXParser = this.fGrammar.getSAXParser();
        StringReader stringReader = new StringReader(this.fData);
        InputSource inputSource = new InputSource(stringReader);
        sAXParser.setContentHandler(contentHandler);
        try {
            sAXParser.parse(inputSource);
        }
        catch (SAXException sAXException) {
        }
        catch (IOException iOException) {
            // empty catch block
        }
        sAXParser.setContentHandler(null);
    }

    private synchronized void writeToDOM(Node node, short s) {
        Document document = s == 1 ? node.getOwnerDocument() : (Document)node;
        DOMParser dOMParser = this.fGrammar.getDOMParser();
        StringReader stringReader = new StringReader(this.fData);
        InputSource inputSource = new InputSource(stringReader);
        try {
            dOMParser.parse(inputSource);
        }
        catch (SAXException sAXException) {
        }
        catch (IOException iOException) {
            // empty catch block
        }
        Document document2 = dOMParser.getDocument();
        dOMParser.dropDocumentReferences();
        Element element = document2.getDocumentElement();
        Node node2 = null;
        if (document instanceof CoreDocumentImpl) {
            node2 = document.adoptNode(element);
            if (node2 == null) {
                node2 = document.importNode(element, true);
            }
        } else {
            node2 = document.importNode(element, true);
        }
        node.insertBefore(node2, node.getFirstChild());
    }
}

