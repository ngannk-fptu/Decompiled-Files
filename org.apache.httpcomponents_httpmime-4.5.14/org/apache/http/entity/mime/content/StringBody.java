/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Consts
 *  org.apache.http.entity.ContentType
 *  org.apache.http.util.Args
 */
package org.apache.http.entity.mime.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.util.Args;

public class StringBody
extends AbstractContentBody {
    private final byte[] content;

    @Deprecated
    public static StringBody create(String text, String mimeType, Charset charset) throws IllegalArgumentException {
        try {
            return new StringBody(text, mimeType, charset);
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Charset " + charset + " is not supported", ex);
        }
    }

    @Deprecated
    public static StringBody create(String text, Charset charset) throws IllegalArgumentException {
        return StringBody.create(text, null, charset);
    }

    @Deprecated
    public static StringBody create(String text) throws IllegalArgumentException {
        return StringBody.create(text, null, null);
    }

    @Deprecated
    public StringBody(String text, String mimeType, Charset charset) throws UnsupportedEncodingException {
        this(text, ContentType.create((String)mimeType, (Charset)(charset != null ? charset : Consts.ASCII)));
    }

    @Deprecated
    public StringBody(String text, Charset charset) throws UnsupportedEncodingException {
        this(text, "text/plain", charset);
    }

    @Deprecated
    public StringBody(String text) throws UnsupportedEncodingException {
        this(text, "text/plain", Consts.ASCII);
    }

    public StringBody(String text, ContentType contentType) {
        super(contentType);
        Args.notNull((Object)text, (String)"Text");
        Charset charset = contentType.getCharset();
        this.content = text.getBytes(charset != null ? charset : Consts.ASCII);
    }

    public Reader getReader() {
        Charset charset = this.getContentType().getCharset();
        return new InputStreamReader((InputStream)new ByteArrayInputStream(this.content), charset != null ? charset : Consts.ASCII);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        int l;
        Args.notNull((Object)out, (String)"Output stream");
        ByteArrayInputStream in = new ByteArrayInputStream(this.content);
        byte[] tmp = new byte[4096];
        while ((l = in.read(tmp)) != -1) {
            out.write(tmp, 0, l);
        }
        out.flush();
    }

    @Override
    public String getTransferEncoding() {
        return "8bit";
    }

    @Override
    public long getContentLength() {
        return this.content.length;
    }

    @Override
    public String getFilename() {
        return null;
    }
}

