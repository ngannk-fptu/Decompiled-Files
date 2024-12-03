/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.auth;

import java.nio.charset.Charset;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.impl.auth.DigestScheme;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.protocol.HttpContext;

@Contract(threading=ThreadingBehavior.STATELESS)
public class DigestSchemeFactory
implements AuthSchemeFactory {
    public static final DigestSchemeFactory INSTANCE = new DigestSchemeFactory();
    private final Charset charset;

    public DigestSchemeFactory(Charset charset) {
        this.charset = charset;
    }

    public DigestSchemeFactory() {
        this(null);
    }

    @Override
    public AuthScheme create(HttpContext context) {
        return new DigestScheme(this.charset);
    }
}

