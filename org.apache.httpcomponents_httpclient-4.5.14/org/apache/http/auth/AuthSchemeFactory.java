/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.params.HttpParams
 */
package org.apache.http.auth;

import org.apache.http.auth.AuthScheme;
import org.apache.http.params.HttpParams;

@Deprecated
public interface AuthSchemeFactory {
    public AuthScheme newInstance(HttpParams var1);
}

