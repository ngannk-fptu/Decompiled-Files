/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.entity.mime.AbstractMultipartForm;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MinimalField;

@Deprecated
public class HttpMultipart
extends AbstractMultipartForm {
    private final HttpMultipartMode mode;
    private final List<FormBodyPart> parts;
    private final String subType;

    public HttpMultipart(String subType, Charset charset, String boundary, HttpMultipartMode mode) {
        super(charset, boundary);
        this.subType = subType;
        this.mode = mode;
        this.parts = new ArrayList<FormBodyPart>();
    }

    public HttpMultipart(String subType, Charset charset, String boundary) {
        this(subType, charset, boundary, HttpMultipartMode.STRICT);
    }

    public HttpMultipart(String subType, String boundary) {
        this(subType, null, boundary);
    }

    public HttpMultipartMode getMode() {
        return this.mode;
    }

    @Override
    protected void formatMultipartHeader(FormBodyPart part, OutputStream out) throws IOException {
        Header header = part.getHeader();
        switch (this.mode) {
            case BROWSER_COMPATIBLE: {
                MinimalField cd = header.getField("Content-Disposition");
                HttpMultipart.writeField(cd, this.charset, out);
                String filename = part.getBody().getFilename();
                if (filename == null) break;
                MinimalField ct = header.getField("Content-Type");
                HttpMultipart.writeField(ct, this.charset, out);
                break;
            }
            default: {
                for (MinimalField field : header) {
                    HttpMultipart.writeField(field, out);
                }
            }
        }
    }

    @Override
    public List<FormBodyPart> getBodyParts() {
        return this.parts;
    }

    public void addBodyPart(FormBodyPart part) {
        if (part == null) {
            return;
        }
        this.parts.add(part);
    }

    public String getSubType() {
        return this.subType;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getBoundary() {
        return this.boundary;
    }
}

