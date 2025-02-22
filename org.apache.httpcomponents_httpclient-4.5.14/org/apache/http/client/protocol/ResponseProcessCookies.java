/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HeaderIterator
 *  org.apache.http.HttpException
 *  org.apache.http.HttpResponse
 *  org.apache.http.HttpResponseInterceptor
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class ResponseProcessCookies
implements HttpResponseInterceptor {
    private final Log log = LogFactory.getLog(this.getClass());

    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        Args.notNull((Object)response, (String)"HTTP request");
        Args.notNull((Object)context, (String)"HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        CookieSpec cookieSpec = clientContext.getCookieSpec();
        if (cookieSpec == null) {
            this.log.debug((Object)"Cookie spec not specified in HTTP context");
            return;
        }
        CookieStore cookieStore = clientContext.getCookieStore();
        if (cookieStore == null) {
            this.log.debug((Object)"Cookie store not specified in HTTP context");
            return;
        }
        CookieOrigin cookieOrigin = clientContext.getCookieOrigin();
        if (cookieOrigin == null) {
            this.log.debug((Object)"Cookie origin not specified in HTTP context");
            return;
        }
        HeaderIterator it = response.headerIterator("Set-Cookie");
        this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        if (cookieSpec.getVersion() > 0) {
            it = response.headerIterator("Set-Cookie2");
            this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        }
    }

    private void processCookies(HeaderIterator iterator, CookieSpec cookieSpec, CookieOrigin cookieOrigin, CookieStore cookieStore) {
        while (iterator.hasNext()) {
            Header header = iterator.nextHeader();
            try {
                List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
                for (Cookie cookie : cookies) {
                    try {
                        cookieSpec.validate(cookie, cookieOrigin);
                        cookieStore.addCookie(cookie);
                        if (!this.log.isDebugEnabled()) continue;
                        this.log.debug((Object)("Cookie accepted [" + ResponseProcessCookies.formatCooke(cookie) + "]"));
                    }
                    catch (MalformedCookieException ex) {
                        if (!this.log.isWarnEnabled()) continue;
                        this.log.warn((Object)("Cookie rejected [" + ResponseProcessCookies.formatCooke(cookie) + "] " + ex.getMessage()));
                    }
                }
            }
            catch (MalformedCookieException ex) {
                if (!this.log.isWarnEnabled()) continue;
                this.log.warn((Object)("Invalid cookie header: \"" + header + "\". " + ex.getMessage()));
            }
        }
    }

    private static String formatCooke(Cookie cookie) {
        StringBuilder buf = new StringBuilder();
        buf.append(cookie.getName());
        buf.append("=\"");
        String v = cookie.getValue();
        if (v != null) {
            if (v.length() > 100) {
                v = v.substring(0, 100) + "...";
            }
            buf.append(v);
        }
        buf.append("\"");
        buf.append(", version:");
        buf.append(Integer.toString(cookie.getVersion()));
        buf.append(", domain:");
        buf.append(cookie.getDomain());
        buf.append(", path:");
        buf.append(cookie.getPath());
        buf.append(", expiry:");
        buf.append(cookie.getExpiryDate());
        return buf.toString();
    }
}

