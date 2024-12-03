/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences.attachment;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONArrayUtils;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.AttachmentType;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.EmbeddedAttachment;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.ExternalAttachment;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public abstract class Attachment {
    private final AttachmentType type;
    private final String description;

    protected Attachment(AttachmentType type, String description) {
        Objects.requireNonNull(type);
        this.type = type;
        this.description = description;
    }

    public AttachmentType getType() {
        return this.type;
    }

    public String getDescriptionString() {
        return this.description;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        if (this.getDescriptionString() != null) {
            o.put((Object)"desc", (Object)this.getDescriptionString());
        }
        return o;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attachment)) {
            return false;
        }
        Attachment that = (Attachment)o;
        return Objects.equals(this.description, that.description);
    }

    public int hashCode() {
        return Objects.hash(this.description);
    }

    public EmbeddedAttachment toEmbeddedAttachment() {
        return (EmbeddedAttachment)this;
    }

    public ExternalAttachment toExternalAttachment() {
        return (ExternalAttachment)this;
    }

    public static Attachment parse(JSONObject jsonObject) throws ParseException {
        if (jsonObject.get((Object)"content") != null) {
            return EmbeddedAttachment.parse(jsonObject);
        }
        if (jsonObject.get((Object)"url") != null) {
            return ExternalAttachment.parse(jsonObject);
        }
        throw new ParseException("Missing required attachment parameter(s)");
    }

    public static List<Attachment> parseList(JSONArray jsonArray) throws ParseException {
        if (jsonArray == null) {
            return null;
        }
        LinkedList<Attachment> attachments = new LinkedList<Attachment>();
        for (JSONObject attachmentObject : JSONArrayUtils.toJSONObjectList(jsonArray)) {
            attachments.add(Attachment.parse(attachmentObject));
        }
        return attachments;
    }
}

