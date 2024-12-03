/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.entity.mime;

import java.util.List;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.MinimalField;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

public class FormBodyPartBuilder {
    private String name;
    private ContentBody body;
    private final Header header = new Header();

    public static FormBodyPartBuilder create(String name, ContentBody body) {
        return new FormBodyPartBuilder(name, body);
    }

    public static FormBodyPartBuilder create() {
        return new FormBodyPartBuilder();
    }

    FormBodyPartBuilder(String name, ContentBody body) {
        this();
        this.name = name;
        this.body = body;
    }

    FormBodyPartBuilder() {
    }

    public FormBodyPartBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public FormBodyPartBuilder setBody(ContentBody body) {
        this.body = body;
        return this;
    }

    public FormBodyPartBuilder addField(String name, String value) {
        Args.notNull(name, "Field name");
        this.header.addField(new MinimalField(name, value));
        return this;
    }

    public FormBodyPartBuilder setField(String name, String value) {
        Args.notNull(name, "Field name");
        this.header.setField(new MinimalField(name, value));
        return this;
    }

    public FormBodyPartBuilder removeFields(String name) {
        Args.notNull(name, "Field name");
        this.header.removeFields(name);
        return this;
    }

    public FormBodyPart build() {
        Asserts.notBlank(this.name, "Name");
        Asserts.notNull(this.body, "Content body");
        Header headerCopy = new Header();
        List<MinimalField> fields = this.header.getFields();
        for (MinimalField field : fields) {
            headerCopy.addField(field);
        }
        if (headerCopy.getField("Content-Disposition") == null) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("form-data; name=\"");
            buffer.append(FormBodyPartBuilder.encodeForHeader(this.name));
            buffer.append("\"");
            if (this.body.getFilename() != null) {
                buffer.append("; filename=\"");
                buffer.append(FormBodyPartBuilder.encodeForHeader(this.body.getFilename()));
                buffer.append("\"");
            }
            headerCopy.addField(new MinimalField("Content-Disposition", buffer.toString()));
        }
        if (headerCopy.getField("Content-Type") == null) {
            ContentType contentType = this.body instanceof AbstractContentBody ? ((AbstractContentBody)this.body).getContentType() : null;
            if (contentType != null) {
                headerCopy.addField(new MinimalField("Content-Type", contentType.toString()));
            } else {
                StringBuilder buffer = new StringBuilder();
                buffer.append(this.body.getMimeType());
                if (this.body.getCharset() != null) {
                    buffer.append("; charset=");
                    buffer.append(this.body.getCharset());
                }
                headerCopy.addField(new MinimalField("Content-Type", buffer.toString()));
            }
        }
        if (headerCopy.getField("Content-Transfer-Encoding") == null) {
            headerCopy.addField(new MinimalField("Content-Transfer-Encoding", this.body.getTransferEncoding()));
        }
        return new FormBodyPart(this.name, this.body, headerCopy);
    }

    private static String encodeForHeader(String headerName) {
        if (headerName == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headerName.length(); ++i) {
            char x = headerName.charAt(i);
            if (x == '\"' || x == '\\' || x == '\r') {
                sb.append("\\");
            }
            sb.append(x);
        }
        return sb.toString();
    }
}

