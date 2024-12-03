/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.protocol;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieOrigin;
import org.apache.hc.client5.http.cookie.CookieSpec;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
public class ResponseProcessCookies
implements HttpResponseInterceptor {
    public static final ResponseProcessCookies INSTANCE = new ResponseProcessCookies();
    private static final Logger LOG = LoggerFactory.getLogger(ResponseProcessCookies.class);

    @Override
    public void process(HttpResponse response, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP request");
        Args.notNull(context, "HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        String exchangeId = clientContext.getExchangeId();
        CookieSpec cookieSpec = clientContext.getCookieSpec();
        if (cookieSpec == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Cookie spec not specified in HTTP context", (Object)exchangeId);
            }
            return;
        }
        CookieStore cookieStore = clientContext.getCookieStore();
        if (cookieStore == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Cookie store not specified in HTTP context", (Object)exchangeId);
            }
            return;
        }
        CookieOrigin cookieOrigin = clientContext.getCookieOrigin();
        if (cookieOrigin == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Cookie origin not specified in HTTP context", (Object)exchangeId);
            }
            return;
        }
        Iterator<Header> it = response.headerIterator("Set-Cookie");
        this.processCookies(exchangeId, it, cookieSpec, cookieOrigin, cookieStore);
    }

    private void processCookies(String exchangeId, Iterator<Header> iterator, CookieSpec cookieSpec, CookieOrigin cookieOrigin, CookieStore cookieStore) {
        while (iterator.hasNext()) {
            Header header = iterator.next();
            try {
                List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
                for (Cookie cookie : cookies) {
                    try {
                        cookieSpec.validate(cookie, cookieOrigin);
                        cookieStore.addCookie(cookie);
                        if (!LOG.isDebugEnabled()) continue;
                        LOG.debug("{} Cookie accepted [{}]", (Object)exchangeId, (Object)ResponseProcessCookies.formatCookie(cookie));
                    }
                    catch (MalformedCookieException ex) {
                        if (!LOG.isWarnEnabled()) continue;
                        LOG.warn("{} Cookie rejected [{}] {}", new Object[]{exchangeId, ResponseProcessCookies.formatCookie(cookie), ex.getMessage()});
                    }
                }
            }
            catch (MalformedCookieException ex) {
                if (!LOG.isWarnEnabled()) continue;
                LOG.warn("{} Invalid cookie header: \"{}\". {}", new Object[]{exchangeId, header, ex.getMessage()});
            }
        }
    }

    private static String formatCookie(Cookie cookie) {
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
        buf.append(", domain:");
        buf.append(cookie.getDomain());
        buf.append(", path:");
        buf.append(cookie.getPath());
        buf.append(", expiry:");
        buf.append(cookie.getExpiryInstant());
        return buf.toString();
    }
}

