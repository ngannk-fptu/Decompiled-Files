/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv_core.verifier.util.ErrorHandlerImpl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class DocumentBuilderImpl
extends DocumentBuilder {
    private final DocumentBuilder core;
    private final Verifier verifier;

    DocumentBuilderImpl(DocumentBuilder _core, Schema _schema) throws ParserConfigurationException {
        this.core = _core;
        try {
            this.verifier = _schema.newVerifier();
        }
        catch (Exception e) {
            throw new ParserConfigurationException(e.toString());
        }
        this.verifier.setErrorHandler(ErrorHandlerImpl.theInstance);
    }

    public DOMImplementation getDOMImplementation() {
        return this.core.getDOMImplementation();
    }

    public boolean isNamespaceAware() {
        return this.core.isNamespaceAware();
    }

    public boolean isValidating() {
        return true;
    }

    public Document newDocument() {
        return this.core.newDocument();
    }

    public Document parse(InputSource is) throws SAXException, IOException {
        return this.verify(this.core.parse(is));
    }

    public Document parse(File f) throws SAXException, IOException {
        return this.verify(this.core.parse(f));
    }

    public Document parse(InputStream is) throws SAXException, IOException {
        return this.verify(this.core.parse(is));
    }

    public Document parse(InputStream is, String systemId) throws SAXException, IOException {
        return this.verify(this.core.parse(is, systemId));
    }

    public Document parse(String url) throws SAXException, IOException {
        return this.verify(this.core.parse(url));
    }

    public void setEntityResolver(EntityResolver resolver) {
        this.verifier.setEntityResolver(resolver);
        this.core.setEntityResolver(resolver);
    }

    public void setErrorHandler(ErrorHandler handler) {
        this.verifier.setErrorHandler(handler);
        this.core.setErrorHandler(handler);
    }

    private Document verify(Document dom) throws SAXException, IOException {
        if (this.verifier.verify(dom)) {
            return dom;
        }
        throw new SAXException("the document is invalid");
    }
}

