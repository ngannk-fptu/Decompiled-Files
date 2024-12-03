/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;

public class DOMInputImpl
implements LSInput {
    protected String fPublicId = null;
    protected String fSystemId = null;
    protected String fBaseSystemId = null;
    protected InputStream fByteStream = null;
    protected Reader fCharStream = null;
    protected String fData = null;
    protected String fEncoding = null;
    protected boolean fCertifiedText = false;

    public DOMInputImpl() {
    }

    public DOMInputImpl(String string, String string2, String string3) {
        this.fPublicId = string;
        this.fSystemId = string2;
        this.fBaseSystemId = string3;
    }

    public DOMInputImpl(String string, String string2, String string3, InputStream inputStream, String string4) {
        this.fPublicId = string;
        this.fSystemId = string2;
        this.fBaseSystemId = string3;
        this.fByteStream = inputStream;
        this.fEncoding = string4;
    }

    public DOMInputImpl(String string, String string2, String string3, Reader reader, String string4) {
        this.fPublicId = string;
        this.fSystemId = string2;
        this.fBaseSystemId = string3;
        this.fCharStream = reader;
        this.fEncoding = string4;
    }

    public DOMInputImpl(String string, String string2, String string3, String string4, String string5) {
        this.fPublicId = string;
        this.fSystemId = string2;
        this.fBaseSystemId = string3;
        this.fData = string4;
        this.fEncoding = string5;
    }

    @Override
    public InputStream getByteStream() {
        return this.fByteStream;
    }

    @Override
    public void setByteStream(InputStream inputStream) {
        this.fByteStream = inputStream;
    }

    @Override
    public Reader getCharacterStream() {
        return this.fCharStream;
    }

    @Override
    public void setCharacterStream(Reader reader) {
        this.fCharStream = reader;
    }

    @Override
    public String getStringData() {
        return this.fData;
    }

    @Override
    public void setStringData(String string) {
        this.fData = string;
    }

    @Override
    public String getEncoding() {
        return this.fEncoding;
    }

    @Override
    public void setEncoding(String string) {
        this.fEncoding = string;
    }

    @Override
    public String getPublicId() {
        return this.fPublicId;
    }

    @Override
    public void setPublicId(String string) {
        this.fPublicId = string;
    }

    @Override
    public String getSystemId() {
        return this.fSystemId;
    }

    @Override
    public void setSystemId(String string) {
        this.fSystemId = string;
    }

    @Override
    public String getBaseURI() {
        return this.fBaseSystemId;
    }

    @Override
    public void setBaseURI(String string) {
        this.fBaseSystemId = string;
    }

    @Override
    public boolean getCertifiedText() {
        return this.fCertifiedText;
    }

    @Override
    public void setCertifiedText(boolean bl) {
        this.fCertifiedText = bl;
    }
}

