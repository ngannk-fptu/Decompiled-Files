/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.ByteArrayBuffer
 */
package org.apache.hc.client5.http.entity.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.MimeField;
import org.apache.hc.client5.http.entity.mime.MultipartPart;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.ByteArrayBuffer;

abstract class AbstractMultipartFormat {
    static final ByteArrayBuffer FIELD_SEP = AbstractMultipartFormat.encode(StandardCharsets.ISO_8859_1, ": ");
    static final ByteArrayBuffer CR_LF = AbstractMultipartFormat.encode(StandardCharsets.ISO_8859_1, "\r\n");
    static final ByteArrayBuffer TWO_HYPHENS = AbstractMultipartFormat.encode(StandardCharsets.ISO_8859_1, "--");
    final Charset charset;
    final String boundary;

    static ByteArrayBuffer encode(Charset charset, CharSequence string) {
        ByteBuffer encoded = charset.encode(CharBuffer.wrap(string));
        ByteArrayBuffer bab = new ByteArrayBuffer(encoded.remaining());
        bab.append(encoded.array(), encoded.arrayOffset() + encoded.position(), encoded.remaining());
        return bab;
    }

    static void writeBytes(ByteArrayBuffer b, OutputStream out) throws IOException {
        out.write(b.array(), 0, b.length());
    }

    static void writeBytes(CharSequence s, Charset charset, OutputStream out) throws IOException {
        ByteArrayBuffer b = AbstractMultipartFormat.encode(charset, s);
        AbstractMultipartFormat.writeBytes(b, out);
    }

    static void writeBytes(CharSequence s, OutputStream out) throws IOException {
        ByteArrayBuffer b = AbstractMultipartFormat.encode(StandardCharsets.ISO_8859_1, s);
        AbstractMultipartFormat.writeBytes(b, out);
    }

    static boolean isLineBreak(char ch) {
        return ch == '\r' || ch == '\n' || ch == '\f' || ch == '\u000b';
    }

    static CharSequence stripLineBreaks(CharSequence s) {
        int n;
        if (s == null) {
            return null;
        }
        boolean requiresRewrite = false;
        for (n = 0; n < s.length(); ++n) {
            char ch = s.charAt(n);
            if (!AbstractMultipartFormat.isLineBreak(ch)) continue;
            requiresRewrite = true;
            break;
        }
        if (!requiresRewrite) {
            return s;
        }
        StringBuilder buf = new StringBuilder();
        buf.append(s, 0, n);
        while (n < s.length()) {
            char ch = s.charAt(n);
            if (AbstractMultipartFormat.isLineBreak(ch)) {
                buf.append(' ');
            } else {
                buf.append(ch);
            }
            ++n;
        }
        return buf.toString();
    }

    static void writeField(MimeField field, OutputStream out) throws IOException {
        AbstractMultipartFormat.writeBytes(AbstractMultipartFormat.stripLineBreaks(field.getName()), out);
        AbstractMultipartFormat.writeBytes(FIELD_SEP, out);
        AbstractMultipartFormat.writeBytes(AbstractMultipartFormat.stripLineBreaks(field.getBody()), out);
        AbstractMultipartFormat.writeBytes(CR_LF, out);
    }

    static void writeField(MimeField field, Charset charset, OutputStream out) throws IOException {
        AbstractMultipartFormat.writeBytes(AbstractMultipartFormat.stripLineBreaks(field.getName()), charset, out);
        AbstractMultipartFormat.writeBytes(FIELD_SEP, out);
        AbstractMultipartFormat.writeBytes(AbstractMultipartFormat.stripLineBreaks(field.getBody()), charset, out);
        AbstractMultipartFormat.writeBytes(CR_LF, out);
    }

    public AbstractMultipartFormat(Charset charset, String boundary) {
        Args.notNull((Object)boundary, (String)"Multipart boundary");
        this.charset = charset != null ? charset : StandardCharsets.ISO_8859_1;
        this.boundary = boundary;
    }

    public AbstractMultipartFormat(String boundary) {
        this(null, boundary);
    }

    public abstract List<MultipartPart> getParts();

    void doWriteTo(OutputStream out, boolean writeContent) throws IOException {
        ByteArrayBuffer boundaryEncoded = AbstractMultipartFormat.encode(this.charset, this.boundary);
        for (MultipartPart part : this.getParts()) {
            AbstractMultipartFormat.writeBytes(TWO_HYPHENS, out);
            AbstractMultipartFormat.writeBytes(boundaryEncoded, out);
            AbstractMultipartFormat.writeBytes(CR_LF, out);
            this.formatMultipartHeader(part, out);
            AbstractMultipartFormat.writeBytes(CR_LF, out);
            if (writeContent) {
                part.getBody().writeTo(out);
            }
            AbstractMultipartFormat.writeBytes(CR_LF, out);
        }
        AbstractMultipartFormat.writeBytes(TWO_HYPHENS, out);
        AbstractMultipartFormat.writeBytes(boundaryEncoded, out);
        AbstractMultipartFormat.writeBytes(TWO_HYPHENS, out);
        AbstractMultipartFormat.writeBytes(CR_LF, out);
    }

    protected abstract void formatMultipartHeader(MultipartPart var1, OutputStream var2) throws IOException;

    public void writeTo(OutputStream out) throws IOException {
        this.doWriteTo(out, true);
    }

    public long getTotalLength() {
        long contentLen = 0L;
        for (MultipartPart part : this.getParts()) {
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

