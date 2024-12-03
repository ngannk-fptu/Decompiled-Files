/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.params.HttpParams
 */
package org.apache.http.cookie;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.params.HttpParams;

@Deprecated
public interface CookieSpecFactory {
    public CookieSpec newInstance(HttpParams var1);
}

