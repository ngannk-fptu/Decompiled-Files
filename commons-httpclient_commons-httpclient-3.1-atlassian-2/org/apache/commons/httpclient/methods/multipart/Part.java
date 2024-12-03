/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods.multipart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Part {
    private static final Log LOG = LogFactory.getLog(Part.class);
    protected static final String BOUNDARY = "----------------314159265358979323846";
    protected static final byte[] BOUNDARY_BYTES = EncodingUtil.getAsciiBytes("----------------314159265358979323846");
    private static final byte[] DEFAULT_BOUNDARY_BYTES = BOUNDARY_BYTES;
    protected static final String CRLF = "\r\n";
    protected static final byte[] CRLF_BYTES = EncodingUtil.getAsciiBytes("\r\n");
    protected static final String QUOTE = "\"";
    protected static final byte[] QUOTE_BYTES = EncodingUtil.getAsciiBytes("\"");
    protected static final String EXTRA = "--";
    protected static final byte[] EXTRA_BYTES = EncodingUtil.getAsciiBytes("--");
    protected static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; name=";
    protected static final byte[] CONTENT_DISPOSITION_BYTES = EncodingUtil.getAsciiBytes("Content-Disposition: form-data; name=");
    protected static final String CONTENT_TYPE = "Content-Type: ";
    protected static final byte[] CONTENT_TYPE_BYTES = EncodingUtil.getAsciiBytes("Content-Type: ");
    protected static final String CHARSET = "; charset=";
    protected static final byte[] CHARSET_BYTES = EncodingUtil.getAsciiBytes("; charset=");
    protected static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding: ";
    protected static final byte[] CONTENT_TRANSFER_ENCODING_BYTES = EncodingUtil.getAsciiBytes("Content-Transfer-Encoding: ");
    private byte[] boundaryBytes;

    public static String getBoundary() {
        return BOUNDARY;
    }

    public abstract String getName();

    public abstract String getContentType();

    public abstract String getCharSet();

    public abstract String getTransferEncoding();

    protected byte[] getPartBoundary() {
        if (this.boundaryBytes == null) {
            return DEFAULT_BOUNDARY_BYTES;
        }
        return this.boundaryBytes;
    }

    void setPartBoundary(byte[] boundaryBytes) {
        this.boundaryBytes = boundaryBytes;
    }

    public boolean isRepeatable() {
        return true;
    }

    protected void sendStart(OutputStream out) throws IOException {
        LOG.trace((Object)"enter sendStart(OutputStream out)");
        out.write(EXTRA_BYTES);
        out.write(this.getPartBoundary());
        out.write(CRLF_BYTES);
    }

    protected void sendDispositionHeader(OutputStream out) throws IOException {
        LOG.trace((Object)"enter sendDispositionHeader(OutputStream out)");
        out.write(CONTENT_DISPOSITION_BYTES);
        out.write(QUOTE_BYTES);
        out.write(EncodingUtil.getAsciiBytes(this.getName()));
        out.write(QUOTE_BYTES);
    }

    protected void sendContentTypeHeader(OutputStream out) throws IOException {
        LOG.trace((Object)"enter sendContentTypeHeader(OutputStream out)");
        String contentType = this.getContentType();
        if (contentType != null) {
            out.write(CRLF_BYTES);
            out.write(CONTENT_TYPE_BYTES);
            out.write(EncodingUtil.getAsciiBytes(contentType));
            String charSet = this.getCharSet();
            if (charSet != null) {
                out.write(CHARSET_BYTES);
                out.write(EncodingUtil.getAsciiBytes(charSet));
            }
        }
    }

    protected void sendTransferEncodingHeader(OutputStream out) throws IOException {
        LOG.trace((Object)"enter sendTransferEncodingHeader(OutputStream out)");
        String transferEncoding = this.getTransferEncoding();
        if (transferEncoding != null) {
            out.write(CRLF_BYTES);
            out.write(CONTENT_TRANSFER_ENCODING_BYTES);
            out.write(EncodingUtil.getAsciiBytes(transferEncoding));
        }
    }

    protected void sendEndOfHeader(OutputStream out) throws IOException {
        LOG.trace((Object)"enter sendEndOfHeader(OutputStream out)");
        out.write(CRLF_BYTES);
        out.write(CRLF_BYTES);
    }

    protected abstract void sendData(OutputStream var1) throws IOException;

    protected abstract long lengthOfData() throws IOException;

    protected void sendEnd(OutputStream out) throws IOException {
        LOG.trace((Object)"enter sendEnd(OutputStream out)");
        out.write(CRLF_BYTES);
    }

    public void send(OutputStream out) throws IOException {
        LOG.trace((Object)"enter send(OutputStream out)");
        this.sendStart(out);
        this.sendDispositionHeader(out);
        this.sendContentTypeHeader(out);
        this.sendTransferEncodingHeader(out);
        this.sendEndOfHeader(out);
        this.sendData(out);
        this.sendEnd(out);
    }

    public long length() throws IOException {
        LOG.trace((Object)"enter length()");
        if (this.lengthOfData() < 0L) {
            return -1L;
        }
        ByteArrayOutputStream overhead = new ByteArrayOutputStream();
        this.sendStart(overhead);
        this.sendDispositionHeader(overhead);
        this.sendContentTypeHeader(overhead);
        this.sendTransferEncodingHeader(overhead);
        this.sendEndOfHeader(overhead);
        this.sendEnd(overhead);
        return (long)overhead.size() + this.lengthOfData();
    }

    public String toString() {
        return this.getName();
    }

    public static void sendParts(OutputStream out, Part[] parts) throws IOException {
        Part.sendParts(out, parts, DEFAULT_BOUNDARY_BYTES);
    }

    public static void sendParts(OutputStream out, Part[] parts, byte[] partBoundary) throws IOException {
        if (parts == null) {
            throw new IllegalArgumentException("Parts may not be null");
        }
        if (partBoundary == null || partBoundary.length == 0) {
            throw new IllegalArgumentException("partBoundary may not be empty");
        }
        for (int i = 0; i < parts.length; ++i) {
            parts[i].setPartBoundary(partBoundary);
            parts[i].send(out);
        }
        out.write(EXTRA_BYTES);
        out.write(partBoundary);
        out.write(EXTRA_BYTES);
        out.write(CRLF_BYTES);
    }

    public static long getLengthOfParts(Part[] parts) throws IOException {
        return Part.getLengthOfParts(parts, DEFAULT_BOUNDARY_BYTES);
    }

    public static long getLengthOfParts(Part[] parts, byte[] partBoundary) throws IOException {
        LOG.trace((Object)"getLengthOfParts(Parts[])");
        if (parts == null) {
            throw new IllegalArgumentException("Parts may not be null");
        }
        long total = 0L;
        for (int i = 0; i < parts.length; ++i) {
            parts[i].setPartBoundary(partBoundary);
            long l = parts[i].length();
            if (l < 0L) {
                return -1L;
            }
            total += l;
        }
        total += (long)EXTRA_BYTES.length;
        total += (long)partBoundary.length;
        total += (long)EXTRA_BYTES.length;
        return total += (long)CRLF_BYTES.length;
    }
}

