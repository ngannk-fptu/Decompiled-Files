/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.verifier.DocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.IVerifier;
import com.ctc.wstx.shaded.msv_core.verifier.Verifier;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class VerifierFilter
extends XMLFilterImpl
implements IVerifier {
    private final IVerifier verifier;

    public VerifierFilter(IVerifier verifier) {
        this.verifier = verifier;
    }

    public VerifierFilter(DocumentDeclaration documentDecl, ErrorHandler errorHandler) {
        this(new Verifier(documentDecl, errorHandler));
    }

    public boolean isValid() {
        return this.verifier.isValid();
    }

    public Object getCurrentElementType() {
        return this.verifier.getCurrentElementType();
    }

    public Datatype[] getLastCharacterType() {
        return this.verifier.getLastCharacterType();
    }

    public final Locator getLocator() {
        return this.verifier.getLocator();
    }

    public final ErrorHandler getErrorHandler() {
        return this.verifier.getErrorHandler();
    }

    public final void setErrorHandler(ErrorHandler handler) {
        super.setErrorHandler(handler);
        this.verifier.setErrorHandler(handler);
    }

    public final void setPanicMode(boolean usePanicMode) {
        this.verifier.setPanicMode(usePanicMode);
    }

    public IVerifier getVerifier() {
        return this.verifier;
    }

    public void setDocumentLocator(Locator locator) {
        this.verifier.setDocumentLocator(locator);
        super.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
        this.verifier.startDocument();
        super.startDocument();
    }

    public void endDocument() throws SAXException {
        this.verifier.endDocument();
        super.endDocument();
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.verifier.startPrefixMapping(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        this.verifier.endPrefixMapping(prefix);
        super.endPrefixMapping(prefix);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        this.verifier.startElement(namespaceURI, localName, qName, atts);
        super.startElement(namespaceURI, localName, qName, atts);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.verifier.endElement(namespaceURI, localName, qName);
        super.endElement(namespaceURI, localName, qName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        this.verifier.characters(ch, start, length);
        super.characters(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.verifier.ignorableWhitespace(ch, start, length);
        super.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        this.verifier.processingInstruction(target, data);
        super.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException {
        this.verifier.skippedEntity(name);
        super.skippedEntity(name);
    }
}

