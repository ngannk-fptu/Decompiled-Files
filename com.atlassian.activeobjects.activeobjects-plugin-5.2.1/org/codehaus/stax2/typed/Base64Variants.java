/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.typed;

import org.codehaus.stax2.typed.Base64Variant;

public final class Base64Variants {
    static final String STD_BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    public static final Base64Variant MIME = new Base64Variant("MIME", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", true, '=', 76);
    public static final Base64Variant MIME_NO_LINEFEEDS = new Base64Variant(MIME, "MIME-NO-LINEFEEDS", Integer.MAX_VALUE);
    public static final Base64Variant PEM = new Base64Variant(MIME, "PEM", true, '=', 64);
    public static final Base64Variant MODIFIED_FOR_URL;

    public static Base64Variant getDefaultVariant() {
        return MIME;
    }

    static {
        StringBuffer stringBuffer = new StringBuffer(STD_BASE64_ALPHABET);
        stringBuffer.setCharAt(stringBuffer.indexOf("+"), '-');
        stringBuffer.setCharAt(stringBuffer.indexOf("/"), '_');
        MODIFIED_FOR_URL = new Base64Variant("MODIFIED-FOR-URL", stringBuffer.toString(), false, '\u0000', Integer.MAX_VALUE);
    }
}

