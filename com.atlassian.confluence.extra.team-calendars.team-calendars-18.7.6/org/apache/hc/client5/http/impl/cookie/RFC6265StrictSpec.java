/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.cookie;

import org.apache.hc.client5.http.cookie.CommonCookieAttributeHandler;
import org.apache.hc.client5.http.impl.cookie.BasicDomainHandler;
import org.apache.hc.client5.http.impl.cookie.BasicExpiresHandler;
import org.apache.hc.client5.http.impl.cookie.BasicHttpOnlyHandler;
import org.apache.hc.client5.http.impl.cookie.BasicMaxAgeHandler;
import org.apache.hc.client5.http.impl.cookie.BasicPathHandler;
import org.apache.hc.client5.http.impl.cookie.BasicSecureHandler;
import org.apache.hc.client5.http.impl.cookie.RFC6265CookieSpecBase;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.SAFE)
public class RFC6265StrictSpec
extends RFC6265CookieSpecBase {
    public RFC6265StrictSpec() {
        super(BasicPathHandler.INSTANCE, BasicDomainHandler.INSTANCE, BasicMaxAgeHandler.INSTANCE, BasicSecureHandler.INSTANCE, BasicHttpOnlyHandler.INSTANCE, new BasicExpiresHandler(DateUtils.STANDARD_PATTERNS));
    }

    RFC6265StrictSpec(CommonCookieAttributeHandler ... handlers) {
        super(handlers);
    }

    public String toString() {
        return "rfc6265-strict";
    }
}

