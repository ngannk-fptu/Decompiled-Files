/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.ProtocolException
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.net.URIBuilder
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.client5.http.utils.URIUtils;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
public class DefaultRedirectStrategy
implements RedirectStrategy {
    public static final DefaultRedirectStrategy INSTANCE = new DefaultRedirectStrategy();

    @Override
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)response, (String)"HTTP response");
        if (!response.containsHeader("Location")) {
            return false;
        }
        int statusCode = response.getCode();
        switch (statusCode) {
            case 301: 
            case 302: 
            case 303: 
            case 307: 
            case 308: {
                return true;
            }
        }
        return false;
    }

    @Override
    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException {
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)response, (String)"HTTP response");
        Args.notNull((Object)context, (String)"HTTP context");
        Header locationHeader = response.getFirstHeader("Location");
        if (locationHeader == null) {
            throw new HttpException("Redirect location is missing");
        }
        String location = locationHeader.getValue();
        URI uri = this.createLocationURI(location);
        try {
            if (!uri.isAbsolute()) {
                uri = URIUtils.resolve(request.getUri(), uri);
            }
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException(ex.getMessage(), (Throwable)ex);
        }
        return uri;
    }

    protected URI createLocationURI(String location) throws ProtocolException {
        try {
            URIBuilder b = new URIBuilder(new URI(location).normalize());
            String host = b.getHost();
            if (host != null) {
                b.setHost(host.toLowerCase(Locale.ROOT));
            }
            if (b.isPathEmpty()) {
                b.setPathSegments(new String[]{""});
            }
            return b.build();
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid redirect URI: " + location, (Throwable)ex);
        }
    }
}

