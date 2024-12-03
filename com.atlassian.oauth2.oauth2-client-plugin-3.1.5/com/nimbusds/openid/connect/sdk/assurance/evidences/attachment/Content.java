/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences.attachment;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jose.util.Base64;
import java.util.Objects;

public class Content {
    private final ContentType type;
    private final Base64 base64;
    private final String description;

    public Content(ContentType type, Base64 base64, String description) {
        Objects.requireNonNull(type);
        this.type = type;
        Objects.requireNonNull(base64);
        this.base64 = base64;
        this.description = description;
    }

    public ContentType getType() {
        return this.type;
    }

    public Base64 getBase64() {
        return this.base64;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Content)) {
            return false;
        }
        Content content = (Content)o;
        return this.getType().equals(content.getType()) && this.getBase64().equals(content.getBase64()) && Objects.equals(this.getDescription(), content.getDescription());
    }

    public int hashCode() {
        return Objects.hash(this.getType(), this.getBase64(), this.getDescription());
    }
}

