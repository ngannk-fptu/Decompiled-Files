/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.cookie;

import org.apache.hc.client5.http.cookie.CommonCookieAttributeHandler;
import org.apache.hc.client5.http.impl.cookie.BasicDomainHandler;
import org.apache.hc.client5.http.impl.cookie.BasicHttpOnlyHandler;
import org.apache.hc.client5.http.impl.cookie.BasicPathHandler;
import org.apache.hc.client5.http.impl.cookie.BasicSecureHandler;
import org.apache.hc.client5.http.impl.cookie.LaxExpiresHandler;
import org.apache.hc.client5.http.impl.cookie.LaxMaxAgeHandler;
import org.apache.hc.client5.http.impl.cookie.RFC6265CookieSpecBase;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.SAFE)
public class RFC6265LaxSpec
extends RFC6265CookieSpecBase {
    public RFC6265LaxSpec() {
        super(BasicPathHandler.INSTANCE, BasicDomainHandler.INSTANCE, LaxMaxAgeHandler.INSTANCE, BasicSecureHandler.INSTANCE, BasicHttpOnlyHandler.INSTANCE, LaxExpiresHandler.INSTANCE);
    }

    RFC6265LaxSpec(CommonCookieAttributeHandler ... handlers) {
        super(handlers);
    }

    public String toString() {
        return "rfc6265-lax";
    }
}

