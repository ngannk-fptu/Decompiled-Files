/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.params.HttpParams
 *  org.apache.http.protocol.HttpContext
 */
package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.cookie.RFC2109Spec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Deprecated
@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class RFC2109SpecFactory
implements CookieSpecFactory,
CookieSpecProvider {
    private final CookieSpec cookieSpec;

    public RFC2109SpecFactory(String[] datepatterns, boolean oneHeader) {
        this.cookieSpec = new RFC2109Spec(datepatterns, oneHeader);
    }

    public RFC2109SpecFactory() {
        this(null, false);
    }

    @Override
    public CookieSpec newInstance(HttpParams params) {
        if (params != null) {
            String[] patterns = null;
            Collection param = (Collection)params.getParameter("http.protocol.cookie-datepatterns");
            if (param != null) {
                patterns = new String[param.size()];
                patterns = param.toArray(patterns);
            }
            boolean singleHeader = params.getBooleanParameter("http.protocol.single-cookie-header", false);
            return new RFC2109Spec(patterns, singleHeader);
        }
        return new RFC2109Spec();
    }

    @Override
    public CookieSpec create(HttpContext context) {
        return this.cookieSpec;
    }
}

