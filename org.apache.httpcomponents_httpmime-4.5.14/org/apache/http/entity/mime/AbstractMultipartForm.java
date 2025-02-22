/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.util.Args
 *  org.apache.http.util.ByteArrayBuffer
 */
package org.apache.http.entity.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MinimalField;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.util.Args;
import org.apache.http.util.ByteArrayBuffer;

abstract class AbstractMultipartForm {
    private static final ByteArrayBuffer FIELD_SEP = AbstractMultipartForm.encode(MIME.DEFAULT_CHARSET, ": ");
    private static final ByteArrayBuffer CR_LF = AbstractMultipartForm.encode(MIME.DEFAULT_CHARSET, "\r\n");
    private static final ByteArrayBuffer TWO_DASHES = AbstractMultipartForm.encode(MIME.DEFAULT_CHARSET, "--");
    final Charset charset;
    final String boundary;

    private static ByteArrayBuffer encode(Charset charset, String string) {
        ByteBuffer encoded = charset.encode(CharBuffer.wrap(string));
        ByteArrayBuffer bab = new ByteArrayBuffer(encoded.remaining());
        bab.append(encoded.array(), encoded.position(), encoded.remaining());
        return bab;
    }

    private static void writeBytes(ByteArrayBuffer b, OutputStream out) throws IOException {
        out.write(b.buffer(), 0, b.length());
    }

    private static void writeBytes(String s, Charset charset, OutputStream out) throws IOException {
        ByteArrayBuffer b = AbstractMultipartForm.encode(charset, s);
        AbstractMultipartForm.writeBytes(b, out);
    }

    private static void writeBytes(String s, OutputStream out) throws IOException {
        ByteArrayBuffer b = AbstractMultipartForm.encode(MIME.DEFAULT_CHARSET, s);
        AbstractMultipartForm.writeBytes(b, out);
    }

    protected static void writeField(MinimalField field, OutputStream out) throws IOException {
        AbstractMultipartForm.writeBytes(field.getName(), out);
        AbstractMultipartForm.writeBytes(FIELD_SEP, out);
        AbstractMultipartForm.writeBytes(field.getBody(), out);
        AbstractMultipartForm.writeBytes(CR_LF, out);
    }

    protected static void writeField(MinimalField field, Charset charset, OutputStream out) throws IOException {
        AbstractMultipartForm.writeBytes(field.getName(), charset, out);
        AbstractMultipartForm.writeBytes(FIELD_SEP, out);
        AbstractMultipartForm.writeBytes(field.getBody(), charset, out);
        AbstractMultipartForm.writeBytes(CR_LF, out);
    }

    public AbstractMultipartForm(Charset charset, String boundary) {
        Args.notNull((Object)boundary, (String)"Multipart boundary");
        this.charset = charset != null ? charset : MIME.DEFAULT_CHARSET;
        this.boundary = boundary;
    }

    public AbstractMultipartForm(String boundary) {
        this(null, boundary);
    }

    public abstract List<FormBodyPart> getBodyParts();

    void doWriteTo(OutputStream out, boolean writeContent) throws IOException {
        ByteArrayBuffer boundaryEncoded = AbstractMultipartForm.encode(this.charset, this.boundary);
        for (FormBodyPart part : this.getBodyParts()) {
            AbstractMultipartForm.writeBytes(TWO_DASHES, out);
            AbstractMultipartForm.writeBytes(boundaryEncoded, out);
            AbstractMultipartForm.writeBytes(CR_LF, out);
            this.formatMultipartHeader(part, out);
            AbstractMultipartForm.writeBytes(CR_LF, out);
            if (writeContent) {
                part.getBody().writeTo(out);
            }
            AbstractMultipartForm.writeBytes(CR_LF, out);
        }
        AbstractMultipartForm.writeBytes(TWO_DASHES, out);
        AbstractMultipartForm.writeBytes(boundaryEncoded, out);
        AbstractMultipartForm.writeBytes(TWO_DASHES, out);
        AbstractMultipartForm.writeBytes(CR_LF, out);
    }

    protected abstract void formatMultipartHeader(FormBodyPart var1, OutputStream var2) throws IOException;

    public void writeTo(OutputStream out) throws IOException {
        this.doWriteTo(out, true);
    }

    public long getTotalLength() {
        long contentLen = 0L;
        for (FormBodyPart part : this.getBodyParts()) {
            ContentBody body = part.getBody();
            long len = body.getContentLength();
            if (len >= 0L) {
                contentLen += len;
                continue;
            }
            return -1L;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            this.doWriteTo(out, false);
            byte[] extra = out.toByteArray();
            return contentLen + (long)extra.length;
        }
        catch (IOException ex) {
            return -1L;
        }
    }
}

