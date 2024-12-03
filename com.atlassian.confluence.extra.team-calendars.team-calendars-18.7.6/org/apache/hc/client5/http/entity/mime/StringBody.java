/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.entity.mime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.hc.client5.http.entity.mime.AbstractContentBody;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.util.Args;

public class StringBody
extends AbstractContentBody {
    private final byte[] content;

    public StringBody(String text, ContentType contentType) {
        super(contentType);
        Args.notNull(text, "Text");
        Charset charset = contentType.getCharset();
        this.content = text.getBytes(charset != null ? charset : StandardCharsets.US_ASCII);
    }

    public Reader getReader() {
        Charset charset = this.getContentType().getCharset();
        return new InputStreamReader((InputStream)new ByteArrayInputStream(this.content), charset != null ? charset : StandardCharsets.US_ASCII);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        out.write(this.content);
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

