/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.user;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.LoginRedirector;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensRevoke;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServlet;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletContext;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletValidation;
import com.atlassian.sal.api.net.Request;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessTokensUserProfileServlet
extends AccessTokensServlet {
    private final ApplicationLinkService applicationLinkService;

    public AccessTokensUserProfileServlet(ApplicationLinkService applicationLinkService, AccessTokensServletContext accessTokensServletContext, AccessTokensRevoke accessTokensRevoke, AccessTokensServletValidation accessTokensServletValidation, TemplateRenderer templateRenderer, LoginRedirector loginRedirector) {
        super(accessTokensServletContext, accessTokensRevoke, accessTokensServletValidation, templateRenderer, loginRedirector);
        this.applicationLinkService = Objects.requireNonNull(applicationLinkService, "applicationLinkService");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> username = this.accessTokensServletValidation.validate(request);
        if (username.isPresent()) {
            Map<String, Object> context = this.getContext(username.get());
            context.put("applicationLinks", this.getApplicationLinks(request));
            this.render(response, context);
        } else {
            this.loginRedirector.redirectToLogin(request, response);
        }
    }

    @Override
    protected String getTemplate() {
        return "templates/user/authorized-apps-user-profile.vm";
    }

    private static URI getCurrentLocation(HttpServletRequest request) {
        try {
            return new URI(request.getRequestURL() + "?tab=outgoing-authorizations");
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<ApplicationLinkRepresentation> getApplicationLinks(HttpServletRequest request) {
        ArrayList<ApplicationLinkRepresentation> applinks = new ArrayList<ApplicationLinkRepresentation>();
        for (ApplicationLink link : this.applicationLinkService.getApplicationLinks()) {
            ApplicationLinkRequestFactory authenticatedRequestFactory = link.createAuthenticatedRequestFactory();
            URI url = link.getDisplayUrl();
            ApplicationLinkRepresentation.Builder builder = new ApplicationLinkRepresentation.Builder(link.getName(), url.toASCIIString(), url.getHost());
            try {
                authenticatedRequestFactory.createRequest(Request.MethodType.GET, "/dummy");
                builder.authorised(true);
            }
            catch (CredentialsRequiredException e) {
                builder.authorised(false).authorisationUrl(authenticatedRequestFactory.getAuthorisationURI(AccessTokensUserProfileServlet.getCurrentLocation(request)).toASCIIString());
            }
            applinks.add(builder.build());
        }
        return applinks;
    }

    public static class ApplicationLinkRepresentation {
        private final String authorisationUrl;
        private final boolean authorised;
        private final String displayUrl;
        private final String name;
        private final String url;

        private ApplicationLinkRepresentation(Builder builder) {
            this.authorisationUrl = builder.authorisationUrl;
            this.authorised = builder.authorised;
            this.displayUrl = builder.displayUrl;
            this.name = builder.name;
            this.url = builder.url;
        }

        @Nullable
        public String getAuthorisationUrl() {
            return this.authorisationUrl;
        }

        @Nonnull
        public String getDisplayUrl() {
            return this.displayUrl;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }

        @Nonnull
        public String getUrl() {
            return this.url;
        }

        public boolean isAuthorised() {
            return this.authorised;
        }

        private static class Builder {
            private final String displayUrl;
            private final String name;
            private final String url;
            private String authorisationUrl;
            private boolean authorised;

            private Builder(String name, String url, String displayUrl) {
                this.displayUrl = displayUrl;
                this.name = name;
                this.url = url;
            }

            @Nonnull
            public Builder authorisationUrl(String authorisationUrl) {
                this.authorisationUrl = authorisationUrl;
                return this;
            }

            @Nonnull
            public Builder authorised(boolean authorised) {
                this.authorised = authorised;
                return this;
            }

            @Nonnull
            public ApplicationLinkRepresentation build() {
                return new ApplicationLinkRepresentation(this);
            }
        }
    }
}

