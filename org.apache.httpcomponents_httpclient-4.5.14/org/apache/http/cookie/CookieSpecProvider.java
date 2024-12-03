/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.protocol.HttpContext
 */
package org.apache.http.cookie;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.protocol.HttpContext;

public interface CookieSpecProvider {
    public CookieSpec create(HttpContext var1);
}

