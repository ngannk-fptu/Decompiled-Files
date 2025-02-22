/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.hc.client5.http.entity.mime.AbstractMultipartFormat;
import org.apache.hc.client5.http.entity.mime.Header;
import org.apache.hc.client5.http.entity.mime.MimeField;
import org.apache.hc.client5.http.entity.mime.MultipartPart;

class HttpRFC6532Multipart
extends AbstractMultipartFormat {
    private final List<MultipartPart> parts;

    public HttpRFC6532Multipart(Charset charset, String boundary, List<MultipartPart> parts) {
        super(charset, boundary);
        this.parts = parts;
    }

    @Override
    public List<MultipartPart> getParts() {
        return this.parts;
    }

    @Override
    protected void formatMultipartHeader(MultipartPart part, OutputStream out) throws IOException {
        Header header = part.getHeader();
        for (MimeField field : header) {
            HttpRFC6532Multipart.writeField(field, StandardCharsets.UTF_8, out);
        }
    }
}

