/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.lookup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.text.lookup.AbstractStringLookup;
import org.apache.commons.text.lookup.IllegalArgumentExceptions;

final class UrlEncoderStringLookup
extends AbstractStringLookup {
    static final UrlEncoderStringLookup INSTANCE = new UrlEncoderStringLookup();

    UrlEncoderStringLookup() {
    }

    String encode(String key, String enc) throws UnsupportedEncodingException {
        return URLEncoder.encode(key, enc);
    }

    @Override
    public String lookup(String key) {
        if (key == null) {
            return null;
        }
        String enc = StandardCharsets.UTF_8.name();
        try {
            return this.encode(key, enc);
        }
        catch (UnsupportedEncodingException e) {
            throw IllegalArgumentExceptions.format(e, "%s: source=%s, encoding=%s", e, key, enc);
        }
    }
}

