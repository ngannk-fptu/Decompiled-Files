/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.AuthorisationAdminURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.applinks.api.auth.NonImpersonatingAuthenticationProvider
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.http.url.SameOrigin
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.AuthorisationAdminURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.api.auth.NonImpersonatingAuthenticationProvider;
import com.atlassian.applinks.core.auth.ApplicationLinkAnalyticsRequestFactory;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestAdaptor;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestFactoryFactory;
import com.atlassian.applinks.core.auth.AuthenticatorAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.http.url.SameOrigin;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLinkRequestFactoryFactoryImpl
implements ApplicationLinkRequestFactoryFactory {
    private static final Logger log = LoggerFactory.getLogger(ApplicationLinkRequestFactoryFactoryImpl.class);
    private final AuthenticatorAccessor authenticatorAccessor;
    private final ApplicationLinkRequestFactory delegateRequestFactory;
    private final UserManager userManager;
    private final EventPublisher publisher;

    @Autowired
    public ApplicationLinkRequestFactoryFactoryImpl(RequestFactory<Request<?, ?>> requestFactory, UserManager userManager, AuthenticatorAccessor authenticatorAccessor, EventPublisher publisher) {
        this.authenticatorAccessor = Objects.requireNonNull(authenticatorAccessor, "authenticatorAccessor can't be null");
        this.delegateRequestFactory = new SalRequestFactoryAdapter(Objects.requireNonNull(requestFactory, "requestFactory can't be null"));
        this.userManager = Objects.requireNonNull(userManager, "userManager can't be null");
        this.publisher = Objects.requireNonNull(publisher);
    }

    @Override
    public ApplicationLinkRequestFactory getApplicationLinkRequestFactory(ApplicationLink link) {
        Objects.requireNonNull(link);
        String username = this.userManager.getRemoteUsername();
        if (username == null) {
            log.debug("No current user context. Outgoing requests will be anonymous");
            return new ApplicationLinkAnalyticsRequestFactory(this.createAnonymousRequestFactory(link), link, this.publisher);
        }
        ApplicationLinkRequestFactory factory = this.createImpersonatingRequestFactory(link, ImpersonatingAuthenticationProvider.class, username);
        if (factory == null && (factory = this.createNonImpersonatingRequestFactory(link, NonImpersonatingAuthenticationProvider.class)) == null) {
            log.debug("No authenticator configured for link '{}', outgoing requests will be anonymous", (Object)link);
            factory = this.createAnonymousRequestFactory(link);
        }
        return new ApplicationLinkAnalyticsRequestFactory(factory, link, this.publisher);
    }

    @Override
    public ApplicationLinkRequestFactory getApplicationLinkRequestFactory(ApplicationLink link, Class<? extends AuthenticationProvider> providerClass) {
        ApplicationLinkRequestFactory factory;
        Objects.requireNonNull(link, "link can't be null");
        Objects.requireNonNull(providerClass, "providerClass can't be null");
        if (Anonymous.class.isAssignableFrom(providerClass)) {
            factory = this.createAnonymousRequestFactory(link);
        } else if (ImpersonatingAuthenticationProvider.class.isAssignableFrom(providerClass)) {
            factory = this.createImpersonatingRequestFactory(link, providerClass);
        } else if (NonImpersonatingAuthenticationProvider.class.isAssignableFrom(providerClass)) {
            factory = this.createNonImpersonatingRequestFactory(link, providerClass);
        } else {
            throw new IllegalArgumentException(String.format("Only AuthenticationProviders that are subclasses of %s, %s or %s are supported", ImpersonatingAuthenticationProvider.class.getSimpleName(), NonImpersonatingAuthenticationProvider.class.getSimpleName(), Anonymous.class.getSimpleName()));
        }
        if (factory == null) {
            return null;
        }
        return new ApplicationLinkAnalyticsRequestFactory(factory, link, this.publisher);
    }

    private ApplicationLinkRequestFactory createAnonymousRequestFactory(ApplicationLink link) {
        return AbsoluteURLRequestFactory.create(this.delegateRequestFactory, link);
    }

    private ApplicationLinkRequestFactory createImpersonatingRequestFactory(ApplicationLink link, Class<? extends AuthenticationProvider> providerClass) {
        String username = this.userManager.getRemoteUsername();
        if (username == null) {
            log.debug("Cannot create request factory with authentication provider '{}' without current user context.", (Object)providerClass.getName());
            return null;
        }
        return this.createImpersonatingRequestFactory(link, providerClass, username);
    }

    private ApplicationLinkRequestFactory createImpersonatingRequestFactory(ApplicationLink link, Class<? extends AuthenticationProvider> providerClass, String username) {
        AuthenticationProvider authenticationProvider = this.authenticatorAccessor.getAuthenticationProvider(link, providerClass);
        if (authenticationProvider == null) {
            return null;
        }
        return AbsoluteURLRequestFactory.create(((ImpersonatingAuthenticationProvider)authenticationProvider).getRequestFactory(username), link);
    }

    private ApplicationLinkRequestFactory createNonImpersonatingRequestFactory(ApplicationLink link, Class<? extends AuthenticationProvider> providerClass) {
        AuthenticationProvider authenticationProvider = this.authenticatorAccessor.getAuthenticationProvider(link, providerClass);
        if (authenticationProvider == null) {
            return null;
        }
        return AbsoluteURLRequestFactory.create(((NonImpersonatingAuthenticationProvider)authenticationProvider).getRequestFactory(), link);
    }

    protected static class SalRequestFactoryAdapter
    implements ApplicationLinkRequestFactory {
        private final RequestFactory<Request<?, ?>> adaptedFactory;

        public SalRequestFactoryAdapter(RequestFactory<Request<?, ?>> requestFactory) {
            this.adaptedFactory = requestFactory;
        }

        public URI getAuthorisationURI() {
            return null;
        }

        public URI getAuthorisationURI(URI callback) {
            return null;
        }

        public ApplicationLinkRequest createRequest(Request.MethodType methodType, String url) {
            return new ApplicationLinkRequestAdaptor(this.adaptedFactory.createRequest(methodType, url));
        }
    }

    protected static class AbsoluteURLRequestFactoryWithAdminURI
    extends AbsoluteURLRequestFactory
    implements AuthorisationAdminURIGenerator {
        private AuthorisationAdminURIGenerator adminUriGenerator;

        public AbsoluteURLRequestFactoryWithAdminURI(ApplicationLinkRequestFactory requestFactory, AuthorisationAdminURIGenerator adminUriGenerator, ApplicationLink link) {
            super(requestFactory, link);
            this.adminUriGenerator = adminUriGenerator;
        }

        public URI getAuthorisationAdminURI() {
            return this.adminUriGenerator.getAuthorisationAdminURI();
        }
    }

    protected static class AbsoluteURLRequestFactory
    implements ApplicationLinkRequestFactory {
        protected final ApplicationLinkRequestFactory requestFactory;
        private final ApplicationLink link;

        public static ApplicationLinkRequestFactory create(ApplicationLinkRequestFactory requestFactory, ApplicationLink link) {
            if (requestFactory instanceof AuthorisationAdminURIGenerator) {
                return new AbsoluteURLRequestFactoryWithAdminURI(requestFactory, (AuthorisationAdminURIGenerator)requestFactory, link);
            }
            return new AbsoluteURLRequestFactory(requestFactory, link);
        }

        public AbsoluteURLRequestFactory(ApplicationLinkRequestFactory requestFactory, ApplicationLink link) {
            this.requestFactory = Objects.requireNonNull(requestFactory, "requestFactory can't be null");
            this.link = Objects.requireNonNull(link, "link can't be null");
        }

        public ApplicationLinkRequest createRequest(Request.MethodType methodType, String uri) throws CredentialsRequiredException {
            URI updatedUri;
            Objects.requireNonNull(uri);
            boolean isAbsoluteUri = false;
            try {
                isAbsoluteUri = new URI(uri).isAbsolute();
            }
            catch (URISyntaxException e) {
                log.warn("Couldn't parse uri '{}' supplied to RequestFactory.createRequest(), assuming relative.", (Object)uri);
            }
            try {
                updatedUri = isAbsoluteUri ? new URI(uri) : new URI(this.link.getRpcUrl() + (uri.startsWith("/") ? uri : "/" + uri));
                Validate.isTrue((boolean)SameOrigin.isSameOrigin((URI)updatedUri, (URI)this.link.getRpcUrl()), (String)"Request url '%s' isn't in the same origin as the rpc url '%s'", (Object[])new Object[]{updatedUri, this.link.getRpcUrl()});
                Validate.isTrue((boolean)updatedUri.getPath().startsWith(this.link.getRpcUrl().getPath()), (String)"Request url '%s' doesn't match rpc url '%s'", (Object[])new Object[]{updatedUri, this.link.getRpcUrl()});
            }
            catch (MalformedURLException | URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
            return this.requestFactory.createRequest(methodType, updatedUri.toString());
        }

        public URI getAuthorisationURI() {
            return this.requestFactory.getAuthorisationURI();
        }

        public URI getAuthorisationURI(URI callback) {
            return this.requestFactory.getAuthorisationURI(callback);
        }
    }
}

