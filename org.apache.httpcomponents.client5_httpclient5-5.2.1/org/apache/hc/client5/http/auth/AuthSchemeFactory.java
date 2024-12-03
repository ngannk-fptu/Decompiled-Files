/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package org.apache.hc.client5.http.auth;

import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.core5.http.protocol.HttpContext;

public interface AuthSchemeFactory {
    public AuthScheme create(HttpContext var1);
}

