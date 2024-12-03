/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.verifier.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class VerifierFilterImpl
extends XMLFilterImpl
implements VerifierFilter {
    private final Verifier verifier;
    private final VerifierHandler core;

    public VerifierFilterImpl(Verifier verifier1) throws SAXException {
        this.verifier = verifier1;
        this.core = this.verifier.getVerifierHandler();
    }

    public boolean isValid() {
        return this.core.isValid();
    }

    public void setErrorHandler(ErrorHandler errorhandler) {
        super.setErrorHandler(errorhandler);
        this.verifier.setErrorHandler(errorhandler);
    }

    public void setEntityResolver(EntityResolver entityresolver) {
        super.setEntityResolver(entityresolver);
        this.verifier.setEntityResolver(entityresolver);
    }

    public void setDocumentLocator(Locator locator) {
        this.core.setDocumentLocator(locator);
        super.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
        this.core.startDocument();
        super.startDocument();
    }

    public void endDocument() throws SAXException {
        this.core.endDocument();
        super.endDocument();
    }

    public void startPrefixMapping(String s, String s1) throws SAXException {
        this.core.startPrefixMapping(s, s1);
        super.startPrefixMapping(s, s1);
    }

    public void endPrefixMapping(String s) throws SAXException {
        this.core.endPrefixMapping(s);
        super.endPrefixMapping(s);
    }

    public void startElement(String s, String s1, String s2, Attributes attributes) throws SAXException {
        this.core.startElement(s, s1, s2, attributes);
        super.startElement(s, s1, s2, attributes);
    }

    public void endElement(String s, String s1, String s2) throws SAXException {
        this.core.endElement(s, s1, s2);
        super.endElement(s, s1, s2);
    }

    public void characters(char[] ac, int i, int j) throws SAXException {
        this.core.characters(ac, i, j);
        super.characters(ac, i, j);
    }

    public void ignorableWhitespace(char[] ac, int i, int j) throws SAXException {
        this.core.ignorableWhitespace(ac, i, j);
        super.ignorableWhitespace(ac, i, j);
    }

    public void processingInstruction(String s, String s1) throws SAXException {
        this.core.processingInstruction(s, s1);
        super.processingInstruction(s, s1);
    }

    public void skippedEntity(String s) throws SAXException {
        this.core.skippedEntity(s);
        super.skippedEntity(s);
    }
}

