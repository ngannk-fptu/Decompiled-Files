/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.jaxp.ValidatingDocumentBuilder;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ValidatingDocumentBuilderFactory
extends DocumentBuilderFactory {
    protected Schema _Schema;
    protected DocumentBuilderFactory _WrappedFactory;
    private boolean validation = true;

    protected ValidatingDocumentBuilderFactory(DocumentBuilderFactory documentbuilderfactory, Schema schema) {
        this._WrappedFactory = documentbuilderfactory;
        this._Schema = schema;
    }

    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        if (this.isValidating()) {
            try {
                return new ValidatingDocumentBuilder(this._WrappedFactory.newDocumentBuilder(), this._Schema.newVerifier());
            }
            catch (VerifierConfigurationException verifierconfigurationexception) {
                throw new ParserConfigurationException(verifierconfigurationexception.getMessage());
            }
        }
        return this._WrappedFactory.newDocumentBuilder();
    }

    public void setAttribute(String s, Object obj) {
        this._WrappedFactory.setAttribute(s, obj);
    }

    public Object getAttribute(String s) {
        return this._WrappedFactory.getAttribute(s);
    }

    public boolean isValidating() {
        return this.validation;
    }

    public void setValidating(boolean flag) {
        this.validation = flag;
    }

    public boolean isCoalescing() {
        return this._WrappedFactory.isCoalescing();
    }

    public boolean isExpandEntityReference() {
        return this._WrappedFactory.isExpandEntityReferences();
    }

    public boolean isIgnoringComments() {
        return this._WrappedFactory.isIgnoringComments();
    }

    public boolean isIgnoringElementContentWhitespace() {
        return this._WrappedFactory.isIgnoringElementContentWhitespace();
    }

    public boolean isNamespaceAware() {
        return this._WrappedFactory.isNamespaceAware();
    }

    public void setCoalescing(boolean flag) {
        this._WrappedFactory.setCoalescing(flag);
    }

    public void setExpandEntityReference(boolean flag) {
        this._WrappedFactory.setExpandEntityReferences(flag);
    }

    public void setIgnoringComments(boolean flag) {
        this._WrappedFactory.setIgnoringComments(flag);
    }

    public void setIgnoringElementContentWhitespace(boolean flag) {
        this._WrappedFactory.setIgnoringElementContentWhitespace(flag);
    }

    public void setNamespaceAware(boolean flag) {
        this._WrappedFactory.setNamespaceAware(flag);
    }

    public static ValidatingDocumentBuilderFactory newInstance(Schema schema) {
        return new ValidatingDocumentBuilderFactory(DocumentBuilderFactory.newInstance(), schema);
    }

    public void setFeature(String name, boolean value) throws ParserConfigurationException {
    }

    public boolean getFeature(String name) throws ParserConfigurationException {
        return false;
    }
}

