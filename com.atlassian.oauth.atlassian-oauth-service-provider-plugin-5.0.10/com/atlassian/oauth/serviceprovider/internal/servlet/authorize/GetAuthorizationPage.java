/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.authorize;

import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.AuthorizationRenderer;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.AuthorizationRequestProcessor;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="getAuthorizationProcessor")
final class GetAuthorizationPage
implements AuthorizationRequestProcessor {
    private final AuthorizationRenderer renderer;
    private final XsrfTokenAccessor xsrfTokenAccessor;

    @Autowired
    public GetAuthorizationPage(AuthorizationRenderer renderer, XsrfTokenAccessor xsrfTokenAccessor) {
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.xsrfTokenAccessor = Objects.requireNonNull(xsrfTokenAccessor, "xsrfTokenAccessor");
    }

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response, ServiceProviderToken token) throws IOException {
        this.renderer.render(request, response, token);
    }
}

