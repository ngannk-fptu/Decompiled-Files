/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences.attachment;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.StringUtils;

public enum AttachmentType {
    EMBEDDED,
    EXTERNAL;


    public String toString() {
        return this.name().toLowerCase();
    }

    public static AttachmentType parse(String s) throws ParseException {
        if (StringUtils.isBlank(s)) {
            throw new ParseException("Null or blank attachment type");
        }
        if (EMBEDDED.name().equalsIgnoreCase(s)) {
            return EMBEDDED;
        }
        if (EXTERNAL.name().equalsIgnoreCase(s)) {
            return EXTERNAL;
        }
        throw new ParseException("Invalid attachment type: " + s);
    }
}

