/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.spi.Whitelist
 *  com.atlassian.gadgets.util.IllegalHttpTargetHostException
 *  com.atlassian.sal.api.user.UserManager
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  org.apache.http.Header
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.ProtocolException
 *  org.apache.http.client.RedirectStrategy
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.protocol.HttpContext
 */
package com.atlassian.gadgets.renderer.internal.http;

import com.atlassian.gadgets.opensocial.spi.Whitelist;
import com.atlassian.gadgets.util.IllegalHttpTargetHostException;
import com.atlassian.sal.api.user.UserManager;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

@Singleton
public class WhitelistAwareRedirectStrategy
implements RedirectStrategy {
    private final Whitelist whitelist;
    private final RedirectStrategy delegateStrategy;
    private final UserManager userManager;

    @Inject
    public WhitelistAwareRedirectStrategy(Whitelist whitelist, RedirectStrategy delegateStrategy, UserManager userManager) {
        this.whitelist = whitelist;
        this.delegateStrategy = delegateStrategy;
        this.userManager = userManager;
    }

    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        try {
            if (this.delegateStrategy.isRedirected(request, response, context)) {
                Header location = response.getFirstHeader("Location");
                if (location == null) {
                    return false;
                }
                URI target = new URI(location.getValue());
                if (!this.whitelist.allows(target, this.userManager.getRemoteUserKey())) {
                    throw new IllegalHttpTargetHostException(target.toString());
                }
                return true;
            }
            return false;
        }
        catch (URISyntaxException e) {
            throw new ProtocolException("Could not parse the Location header", (Throwable)e);
        }
    }

    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        return this.delegateStrategy.getRedirect(request, response, context);
    }
}

