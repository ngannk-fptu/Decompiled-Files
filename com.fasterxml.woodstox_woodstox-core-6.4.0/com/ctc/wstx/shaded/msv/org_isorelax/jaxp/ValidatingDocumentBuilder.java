/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class ValidatingDocumentBuilder
extends DocumentBuilder {
    protected DocumentBuilder _WrappedBuilder;
    protected Verifier _Verifier;

    protected ValidatingDocumentBuilder(DocumentBuilder documentbuilder, Verifier verifier) {
        this._WrappedBuilder = documentbuilder;
        this._Verifier = verifier;
    }

    public Document parse(InputSource inputsource) throws SAXException, IOException {
        return this.verify(this._WrappedBuilder.parse(inputsource));
    }

    public Document parse(File file) throws SAXException, IOException {
        return this.verify(this._WrappedBuilder.parse(file));
    }

    public Document parse(InputStream inputstream) throws SAXException, IOException {
        return this.verify(this._WrappedBuilder.parse(inputstream));
    }

    public Document parse(InputStream inputstream, String s) throws SAXException, IOException {
        return this.verify(this._WrappedBuilder.parse(inputstream, s));
    }

    public Document parse(String s) throws SAXException, IOException {
        return this.verify(this._WrappedBuilder.parse(s));
    }

    public boolean isNamespaceAware() {
        return this._WrappedBuilder.isNamespaceAware();
    }

    public boolean isValidating() {
        return true;
    }

    public void setEntityResolver(EntityResolver entityresolver) {
        this._WrappedBuilder.setEntityResolver(entityresolver);
        this._Verifier.setEntityResolver(entityresolver);
    }

    public void setErrorHandler(ErrorHandler errorhandler) {
        this._WrappedBuilder.setErrorHandler(errorhandler);
        this._Verifier.setErrorHandler(errorhandler);
    }

    public Document newDocument() {
        return this._WrappedBuilder.newDocument();
    }

    public DOMImplementation getDOMImplementation() {
        return this._WrappedBuilder.getDOMImplementation();
    }

    private Document verify(Document document) throws SAXException, IOException {
        if (this._Verifier.verify(document)) {
            return document;
        }
        throw new SAXException("the document is invalid, but the ErrorHandler does not throw any Exception.");
    }
}

