/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.protocol.HttpContext
 */
package org.apache.http.auth;

import org.apache.http.auth.AuthScheme;
import org.apache.http.protocol.HttpContext;

public interface AuthSchemeProvider {
    public AuthScheme create(HttpContext var1);
}

