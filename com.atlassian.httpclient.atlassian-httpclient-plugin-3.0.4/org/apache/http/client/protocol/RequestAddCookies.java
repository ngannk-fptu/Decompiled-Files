/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.protocol;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class RequestAddCookies
implements HttpRequestInterceptor {
    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Header header;
        int ver;
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT")) {
            return;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        CookieStore cookieStore = clientContext.getCookieStore();
        if (cookieStore == null) {
            this.log.debug("Cookie store not specified in HTTP context");
            return;
        }
        Lookup<CookieSpecProvider> registry = clientContext.getCookieSpecRegistry();
        if (registry == null) {
            this.log.debug("CookieSpec registry not specified in HTTP context");
            return;
        }
        HttpHost targetHost = clientContext.getTargetHost();
        if (targetHost == null) {
            this.log.debug("Target host not set in the context");
            return;
        }
        RouteInfo route = clientContext.getHttpRoute();
        if (route == null) {
            this.log.debug("Connection route not set in the context");
            return;
        }
        RequestConfig config = clientContext.getRequestConfig();
        String policy = config.getCookieSpec();
        if (policy == null) {
            policy = "default";
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("CookieSpec selected: " + policy);
        }
        URI requestURI = null;
        if (request instanceof HttpUriRequest) {
            requestURI = ((HttpUriRequest)request).getURI();
        } else {
            try {
                requestURI = new URI(request.getRequestLine().getUri());
            }
            catch (URISyntaxException uRISyntaxException) {
                // empty catch block
            }
        }
        String path = requestURI != null ? requestURI.getPath() : null;
        String hostName = targetHost.getHostName();
        int port = targetHost.getPort();
        if (port < 0) {
            port = route.getTargetHost().getPort();
        }
        CookieOrigin cookieOrigin = new CookieOrigin(hostName, port >= 0 ? port : 0, !TextUtils.isEmpty(path) ? path : "/", route.isSecure());
        CookieSpecProvider provider = registry.lookup(policy);
        if (provider == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Unsupported cookie policy: " + policy);
            }
            return;
        }
        CookieSpec cookieSpec = provider.create(clientContext);
        List<Cookie> cookies = cookieStore.getCookies();
        ArrayList<Cookie> matchedCookies = new ArrayList<Cookie>();
        Date now = new Date();
        boolean expired = false;
        for (Cookie cookie : cookies) {
            if (!cookie.isExpired(now)) {
                if (!cookieSpec.match(cookie, cookieOrigin)) continue;
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Cookie " + cookie + " match " + cookieOrigin);
                }
                matchedCookies.add(cookie);
                continue;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Cookie " + cookie + " expired");
            }
            expired = true;
        }
        if (expired) {
            cookieStore.clearExpired(now);
        }
        if (!matchedCookies.isEmpty()) {
            List<Header> headers = cookieSpec.formatCookies(matchedCookies);
            for (Header header2 : headers) {
                request.addHeader(header2);
            }
        }
        if ((ver = cookieSpec.getVersion()) > 0 && (header = cookieSpec.getVersionHeader()) != null) {
            request.addHeader(header);
        }
        context.setAttribute("http.cookie-spec", cookieSpec);
        context.setAttribute("http.cookie-origin", cookieOrigin);
    }
}

