/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.io.InputStream;
import java.io.Reader;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public final class SAXInputSource
extends XMLInputSource {
    private XMLReader fXMLReader;
    private InputSource fInputSource;

    public SAXInputSource() {
        this((InputSource)null);
    }

    public SAXInputSource(InputSource inputSource) {
        this(null, inputSource);
    }

    public SAXInputSource(XMLReader xMLReader, InputSource inputSource) {
        super(inputSource != null ? inputSource.getPublicId() : null, inputSource != null ? inputSource.getSystemId() : null, null);
        if (inputSource != null) {
            this.setByteStream(inputSource.getByteStream());
            this.setCharacterStream(inputSource.getCharacterStream());
            this.setEncoding(inputSource.getEncoding());
        }
        this.fInputSource = inputSource;
        this.fXMLReader = xMLReader;
    }

    public void setXMLReader(XMLReader xMLReader) {
        this.fXMLReader = xMLReader;
    }

    public XMLReader getXMLReader() {
        return this.fXMLReader;
    }

    public void setInputSource(InputSource inputSource) {
        if (inputSource != null) {
            this.setPublicId(inputSource.getPublicId());
            this.setSystemId(inputSource.getSystemId());
            this.setByteStream(inputSource.getByteStream());
            this.setCharacterStream(inputSource.getCharacterStream());
            this.setEncoding(inputSource.getEncoding());
        } else {
            this.setPublicId(null);
            this.setSystemId(null);
            this.setByteStream(null);
            this.setCharacterStream(null);
            this.setEncoding(null);
        }
        this.fInputSource = inputSource;
    }

    public InputSource getInputSource() {
        return this.fInputSource;
    }

    @Override
    public void setPublicId(String string) {
        super.setPublicId(string);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setPublicId(string);
    }

    @Override
    public void setSystemId(String string) {
        super.setSystemId(string);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setSystemId(string);
    }

    @Override
    public void setByteStream(InputStream inputStream) {
        super.setByteStream(inputStream);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setByteStream(inputStream);
    }

    @Override
    public void setCharacterStream(Reader reader) {
        super.setCharacterStream(reader);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setCharacterStream(reader);
    }

    @Override
    public void setEncoding(String string) {
        super.setEncoding(string);
        if (this.fInputSource == null) {
            this.fInputSource = new InputSource();
        }
        this.fInputSource.setEncoding(string);
    }
}

