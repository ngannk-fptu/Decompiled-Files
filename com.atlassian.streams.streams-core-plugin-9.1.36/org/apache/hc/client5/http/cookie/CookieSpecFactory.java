/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.cookie;

import org.apache.hc.client5.http.cookie.CookieSpec;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.protocol.HttpContext;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface CookieSpecFactory {
    public CookieSpec create(HttpContext var1);
}

