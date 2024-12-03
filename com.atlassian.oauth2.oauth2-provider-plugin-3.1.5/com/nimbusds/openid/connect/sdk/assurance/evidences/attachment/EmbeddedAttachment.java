/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences.attachment;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.AttachmentType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Content;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class EmbeddedAttachment
extends Attachment {
    private final Content content;

    public EmbeddedAttachment(Content content) {
        super(AttachmentType.EMBEDDED, content.getDescription());
        this.content = content;
    }

    public Content getContent() {
        return this.content;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        o.put("content_type", this.getContent().getType().toString());
        o.put("content", this.getContent().getBase64().toString());
        return o;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmbeddedAttachment)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        EmbeddedAttachment that = (EmbeddedAttachment)o;
        return this.getContent().equals(that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.getContent());
    }

    public static EmbeddedAttachment parse(JSONObject jsonObject) throws ParseException {
        ContentType type;
        try {
            type = ContentType.parse(JSONObjectUtils.getString(jsonObject, "content_type"));
        }
        catch (java.text.ParseException e) {
            throw new ParseException("Invalid content_type: " + e.getMessage(), e);
        }
        Base64 base64 = Base64.from(JSONObjectUtils.getString(jsonObject, "content"));
        if (base64.toString().trim().isEmpty()) {
            throw new ParseException("Empty or blank content");
        }
        String description = JSONObjectUtils.getString(jsonObject, "desc", null);
        return new EmbeddedAttachment(new Content(type, base64, description));
    }
}

