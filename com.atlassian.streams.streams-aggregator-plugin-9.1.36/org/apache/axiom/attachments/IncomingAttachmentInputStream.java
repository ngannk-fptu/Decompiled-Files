/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.axiom.attachments.IncomingAttachmentStreams;

public class IncomingAttachmentInputStream
extends InputStream {
    private HashMap _headers = null;
    private HashMap _headersLowerCase = null;
    private InputStream _stream = null;
    private IncomingAttachmentStreams parentContainer;
    public static final String HEADER_CONTENT_DESCRIPTION = "content-description";
    public static final String HEADER_CONTENT_TYPE = "content-type";
    public static final String HEADER_CONTENT_TRANSFER_ENCODING = "content-transfer-encoding";
    public static final String HEADER_CONTENT_LENGTH = "content-length";
    public static final String HEADER_CONTENT_LOCATION = "content-location";
    public static final String HEADER_CONTENT_ID = "content-id";

    public IncomingAttachmentInputStream(InputStream in, IncomingAttachmentStreams parentContainer) {
        this._stream = in;
        this.parentContainer = parentContainer;
    }

    public Map getHeaders() {
        return this._headers;
    }

    public void addHeader(String name, String value) {
        if (this._headers == null) {
            this._headers = new HashMap();
            this._headersLowerCase = new HashMap();
        }
        this._headers.put(name, value);
        this._headersLowerCase.put(name.toLowerCase(), value);
    }

    public String getHeader(String name) {
        Object header;
        block3: {
            block2: {
                header = null;
                if (this._headersLowerCase == null) break block2;
                Object v = this._headersLowerCase.get(name.toLowerCase());
                header = v;
                if (v != null) break block3;
            }
            return null;
        }
        return header.toString();
    }

    public String getContentId() {
        return this.getHeader(HEADER_CONTENT_ID);
    }

    public String getContentLocation() {
        return this.getHeader(HEADER_CONTENT_LOCATION);
    }

    public String getContentType() {
        return this.getHeader(HEADER_CONTENT_TYPE);
    }

    public boolean markSupported() {
        return false;
    }

    public void reset() throws IOException {
        throw new IOException("markNotSupported");
    }

    public void mark(int readLimit) {
    }

    public int read() throws IOException {
        int retval = this._stream.read();
        this.parentContainer.setReadyToGetNextStream(retval == -1);
        return retval;
    }

    public int read(byte[] b) throws IOException {
        int retval = this._stream.read(b);
        this.parentContainer.setReadyToGetNextStream(retval == -1);
        return retval;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int retval = this._stream.read(b, off, len);
        this.parentContainer.setReadyToGetNextStream(retval == -1);
        return retval;
    }
}

