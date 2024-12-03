/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.mime.impl.axiom;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.activation.DataHandler;
import org.apache.axiom.mime.Header;
import org.apache.axiom.mime.MultipartWriter;
import org.apache.axiom.util.base64.Base64EncodingOutputStream;

class MultipartWriterImpl
implements MultipartWriter {
    private final OutputStream out;
    private final String boundary;
    private final byte[] buffer = new byte[256];

    public MultipartWriterImpl(OutputStream out, String boundary) {
        this.out = out;
        this.boundary = boundary;
    }

    void writeAscii(String s) throws IOException {
        int count = 0;
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (c >= '\u0080') {
                throw new IOException("Illegal character '" + c + "'");
            }
            this.buffer[count++] = (byte)c;
            if (count != this.buffer.length) continue;
            this.out.write(this.buffer);
            count = 0;
        }
        if (count > 0) {
            this.out.write(this.buffer, 0, count);
        }
    }

    public OutputStream writePart(String contentType, String contentTransferEncoding, String contentID, List extraHeaders) throws IOException {
        OutputStream transferEncoder;
        if (contentTransferEncoding.equals("8bit") || contentTransferEncoding.equals("binary")) {
            transferEncoder = this.out;
        } else {
            transferEncoder = new Base64EncodingOutputStream(this.out);
            contentTransferEncoding = "base64";
        }
        this.writeAscii("--");
        this.writeAscii(this.boundary);
        if (contentType != null) {
            this.writeAscii("\r\nContent-Type: ");
            this.writeAscii(contentType);
        }
        this.writeAscii("\r\nContent-Transfer-Encoding: ");
        this.writeAscii(contentTransferEncoding);
        if (contentID != null) {
            this.writeAscii("\r\nContent-ID: <");
            this.writeAscii(contentID);
            this.out.write(62);
        }
        if (extraHeaders != null) {
            for (Header header : extraHeaders) {
                this.writeAscii("\r\n");
                this.writeAscii(header.getName());
                this.writeAscii(": ");
                this.writeAscii(header.getValue());
            }
        }
        this.writeAscii("\r\n\r\n");
        return new PartOutputStream(transferEncoder);
    }

    public OutputStream writePart(String contentType, String contentTransferEncoding, String contentID) throws IOException {
        return this.writePart(contentType, contentTransferEncoding, contentID, null);
    }

    public void writePart(DataHandler dataHandler, String contentTransferEncoding, String contentID, List extraHeaders) throws IOException {
        OutputStream partOutputStream = this.writePart(dataHandler.getContentType(), contentTransferEncoding, contentID, extraHeaders);
        dataHandler.writeTo(partOutputStream);
        partOutputStream.close();
    }

    public void writePart(DataHandler dataHandler, String contentTransferEncoding, String contentID) throws IOException {
        this.writePart(dataHandler, contentTransferEncoding, contentID, null);
    }

    public void complete() throws IOException {
        this.writeAscii("--");
        this.writeAscii(this.boundary);
        this.writeAscii("--\r\n");
    }

    class PartOutputStream
    extends OutputStream {
        private final OutputStream parent;

        public PartOutputStream(OutputStream parent) {
            this.parent = parent;
        }

        public void write(int b) throws IOException {
            this.parent.write(b);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            this.parent.write(b, off, len);
        }

        public void write(byte[] b) throws IOException {
            this.parent.write(b);
        }

        public void close() throws IOException {
            if (this.parent instanceof Base64EncodingOutputStream) {
                ((Base64EncodingOutputStream)this.parent).complete();
            }
            MultipartWriterImpl.this.writeAscii("\r\n");
        }
    }
}

