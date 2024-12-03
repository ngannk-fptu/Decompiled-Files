/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.parser;

import java.io.InputStream;
import java.io.Reader;
import org.apache.xerces.xni.XMLResourceIdentifier;

public class XMLInputSource {
    protected String fPublicId;
    protected String fSystemId;
    protected String fBaseSystemId;
    protected InputStream fByteStream;
    protected Reader fCharStream;
    protected String fEncoding;

    public XMLInputSource(String string, String string2, String string3) {
        this.fPublicId = string;
        this.fSystemId = string2;
        this.fBaseSystemId = string3;
    }

    public XMLInputSource(XMLResourceIdentifier xMLResourceIdentifier) {
        this.fPublicId = xMLResourceIdentifier.getPublicId();
        this.fSystemId = xMLResourceIdentifier.getLiteralSystemId();
        this.fBaseSystemId = xMLResourceIdentifier.getBaseSystemId();
    }

    public XMLInputSource(String string, String string2, String string3, InputStream inputStream, String string4) {
        this.fPublicId = string;
        this.fSystemId = string2;
        this.fBaseSystemId = string3;
        this.fByteStream = inputStream;
        this.fEncoding = string4;
    }

    public XMLInputSource(String string, String string2, String string3, Reader reader, String string4) {
        this.fPublicId = string;
        this.fSystemId = string2;
        this.fBaseSystemId = string3;
        this.fCharStream = reader;
        this.fEncoding = string4;
    }

    public void setPublicId(String string) {
        this.fPublicId = string;
    }

    public String getPublicId() {
        return this.fPublicId;
    }

    public void setSystemId(String string) {
        this.fSystemId = string;
    }

    public String getSystemId() {
        return this.fSystemId;
    }

    public void setBaseSystemId(String string) {
        this.fBaseSystemId = string;
    }

    public String getBaseSystemId() {
        return this.fBaseSystemId;
    }

    public void setByteStream(InputStream inputStream) {
        this.fByteStream = inputStream;
    }

    public InputStream getByteStream() {
        return this.fByteStream;
    }

    public void setCharacterStream(Reader reader) {
        this.fCharStream = reader;
    }

    public Reader getCharacterStream() {
        return this.fCharStream;
    }

    public void setEncoding(String string) {
        this.fEncoding = string;
    }

    public String getEncoding() {
        return this.fEncoding;
    }
}

