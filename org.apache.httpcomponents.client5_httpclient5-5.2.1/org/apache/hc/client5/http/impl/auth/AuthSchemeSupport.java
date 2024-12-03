/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 */
package org.apache.hc.client5.http.impl.auth;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.hc.client5.http.auth.AuthenticationException;
import org.apache.hc.core5.annotation.Internal;

@Internal
public class AuthSchemeSupport {
    public static Charset parseCharset(String charsetName, Charset defaultCharset) throws AuthenticationException {
        try {
            return charsetName != null ? Charset.forName(charsetName) : defaultCharset;
        }
        catch (UnsupportedCharsetException ex) {
            throw new AuthenticationException("Unsupported charset: " + charsetName);
        }
    }
}

