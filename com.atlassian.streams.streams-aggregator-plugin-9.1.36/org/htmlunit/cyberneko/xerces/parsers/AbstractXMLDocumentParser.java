/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.parsers;

import org.htmlunit.cyberneko.xerces.parsers.XMLParser;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.NamespaceContext;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLDocumentHandler;
import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XMLString;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentSource;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParserConfiguration;

public abstract class AbstractXMLDocumentParser
extends XMLParser
implements XMLDocumentHandler {
    protected XMLDocumentSource fDocumentSource;

    protected AbstractXMLDocumentParser(XMLParserConfiguration config) {
        super(config);
        config.setDocumentHandler(this);
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
    }

    @Override
    public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        this.startElement(element, attributes, augs);
        this.endElement(element, augs);
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
    }

    @Override
    public void endDocument(Augmentations augs) throws XNIException {
    }

    @Override
    public void startGeneralEntity(String name, String encoding, Augmentations augs) throws XNIException {
    }

    @Override
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
    }

    @Override
    public void setDocumentSource(XMLDocumentSource source) {
        this.fDocumentSource = source;
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
}

