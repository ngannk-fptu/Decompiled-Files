/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package org.apache.hc.client5.http.impl.auth;

import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.impl.auth.NTLMScheme;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.protocol.HttpContext;

@Contract(threading=ThreadingBehavior.STATELESS)
public class NTLMSchemeFactory
implements AuthSchemeFactory {
    public static final NTLMSchemeFactory INSTANCE = new NTLMSchemeFactory();

    @Override
    public AuthScheme create(HttpContext context) {
        return new NTLMScheme();
    }
}

