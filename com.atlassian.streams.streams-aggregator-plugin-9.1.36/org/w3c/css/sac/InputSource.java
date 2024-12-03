/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import java.io.InputStream;
import java.io.Reader;

public class InputSource {
    private String uri;
    private InputStream byteStream;
    private String encoding;
    private Reader characterStream;
    private String title;
    private String media;

    public InputSource() {
    }

    public InputSource(String string) {
        this.setURI(string);
    }

    public InputSource(Reader reader) {
        this.setCharacterStream(reader);
    }

    public void setURI(String string) {
        this.uri = string;
    }

    public String getURI() {
        return this.uri;
    }

    public void setByteStream(InputStream inputStream) {
        this.byteStream = inputStream;
    }

    public InputStream getByteStream() {
        return this.byteStream;
    }

    public void setEncoding(String string) {
        this.encoding = string;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setCharacterStream(Reader reader) {
        this.characterStream = reader;
    }

    public Reader getCharacterStream() {
        return this.characterStream;
    }

    public void setTitle(String string) {
        this.title = string;
    }

    public String getTitle() {
        return this.title;
    }

    public void setMedia(String string) {
        this.media = string;
    }

    public String getMedia() {
        if (this.media == null) {
            return "all";
        }
        return this.media;
    }
}

