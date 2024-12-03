/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.http.entity.mime.AbstractMultipartForm;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.MinimalField;

class HttpBrowserCompatibleMultipart
extends AbstractMultipartForm {
    private final List<FormBodyPart> parts;

    public HttpBrowserCompatibleMultipart(Charset charset, String boundary, List<FormBodyPart> parts) {
        super(charset, boundary);
        this.parts = parts;
    }

    @Override
    public List<FormBodyPart> getBodyParts() {
        return this.parts;
    }

    @Override
    protected void formatMultipartHeader(FormBodyPart part, OutputStream out) throws IOException {
        Header header = part.getHeader();
        MinimalField cd = header.getField("Content-Disposition");
        HttpBrowserCompatibleMultipart.writeField(cd, this.charset, out);
        String filename = part.getBody().getFilename();
        if (filename != null) {
            MinimalField ct = header.getField("Content-Type");
            HttpBrowserCompatibleMultipart.writeField(ct, this.charset, out);
        }
    }
}

