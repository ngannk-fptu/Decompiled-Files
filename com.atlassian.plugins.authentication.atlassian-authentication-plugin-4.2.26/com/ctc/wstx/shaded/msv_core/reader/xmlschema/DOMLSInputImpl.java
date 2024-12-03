/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.DOMLSInput;
import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSInput;

public class DOMLSInputImpl
implements LSInput,
DOMLSInput {
    private String baseURI;
    private String systemId;
    private Element element;

    public DOMLSInputImpl(String baseURI, String systemId, Element data) {
        this.baseURI = baseURI;
        this.element = data;
        this.systemId = systemId;
    }

    public String getBaseURI() {
        return this.baseURI;
    }

    public InputStream getByteStream() {
        return null;
    }

    public boolean getCertifiedText() {
        return false;
    }

    public Reader getCharacterStream() {
        return null;
    }

    public String getEncoding() {
        return null;
    }

    public String getPublicId() {
        return null;
    }

    public String getStringData() {
        return null;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public void setByteStream(InputStream byteStream) {
        throw new UnsupportedOperationException();
    }

    public void setCertifiedText(boolean certifiedText) {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream(Reader characterStream) {
        throw new UnsupportedOperationException();
    }

    public void setEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }

    public void setPublicId(String publicId) {
        throw new UnsupportedOperationException();
    }

    public void setStringData(String stringData) {
        throw new UnsupportedOperationException();
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Element getElement() {
        return this.element;
    }
}

