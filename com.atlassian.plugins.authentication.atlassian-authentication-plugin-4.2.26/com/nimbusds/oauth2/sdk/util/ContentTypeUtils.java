/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;

public final class ContentTypeUtils {
    public static void ensureContentType(ContentType expected, ContentType found) throws ParseException {
        if (found == null) {
            throw new ParseException("Missing HTTP Content-Type header");
        }
        if (!expected.matches(found)) {
            throw new ParseException("The HTTP Content-Type header must be " + expected.getType() + ", received " + found.getType());
        }
    }

    private ContentTypeUtils() {
    }
}

