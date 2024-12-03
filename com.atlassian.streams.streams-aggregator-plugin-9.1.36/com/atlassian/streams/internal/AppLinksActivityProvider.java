/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityLinkService
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.streams.api.StreamsException
 *  com.atlassian.streams.api.StreamsFilterType$Operator
 *  com.atlassian.streams.api.builder.StreamsFeedUriBuilder
 *  com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Iterables
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Options
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.atlassian.streams.api.common.uri.Uris
 *  com.atlassian.streams.spi.CancellableTask
 *  com.atlassian.streams.spi.CancellableTask$Result
 *  com.atlassian.streams.spi.EntityResolver
 *  com.atlassian.streams.spi.Filters
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.atlassian.streams.spi.StreamsLocaleProvider
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityLinkService;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.streams.api.StreamsException;
import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.api.builder.StreamsFeedUriBuilder;
import com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Iterables;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Options;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.api.common.uri.Uris;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviders;
import com.atlassian.streams.internal.ActivityRequestImpl;
import com.atlassian.streams.internal.ApplinkResponseException;
import com.atlassian.streams.internal.NoMatchingRemoteKeysException;
import com.atlassian.streams.internal.Sys;
import com.atlassian.streams.internal.applinks.ApplicationLinkServiceExtensions;
import com.atlassian.streams.internal.feed.AuthRequiredFeedHeader;
import com.atlassian.streams.internal.feed.FeedHeader;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.internal.feed.FeedParser;
import com.atlassian.streams.internal.rest.MediaTypes;
import com.atlassian.streams.internal.rest.representations.JsonProvider;
import com.atlassian.streams.internal.rest.representations.ProviderFilterRepresentation;
import com.atlassian.streams.internal.rest.representations.StreamsConfigRepresentation;
import com.atlassian.streams.internal.rest.representations.StreamsKeysRepresentation;
import com.atlassian.streams.spi.CancellableTask;
import com.atlassian.streams.spi.EntityResolver;
import com.atlassian.streams.spi.Filters;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.streams.spi.StreamsLocaleProvider;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLinksActivityProvider
implements ActivityProvider {
    public static final String PROVIDER_KEY_SEPARATOR = "@";
    private static final Logger log = LoggerFactory.getLogger(AppLinksActivityProvider.class);
    private static final int CONNECTION_TIMEOUT = 1000;
    private static final int SO_TIMEOUT = 5000;
    private final ApplicationLink appLink;
    private final EntityLinkService entityLinkService;
    private final Iterable<EntityResolver> entityResolvers;
    private final StreamsFeedUriBuilderFactory streamsFeedUriBuilderFactory;
    private final JsonProvider jsonProvider = new JsonProvider();
    private final StreamsLocaleProvider streamsLocaleProvider;
    private final FeedParser feedParser;
    private final ApplicationLinkServiceExtensions appLinkServiceExtensions;
    private final TransactionTemplate transactionTemplate;
    private final StreamsI18nResolver i18nResolver;
    private final Function<String, Iterable<String>> toRemoteKey = new Function<String, Iterable<String>>(){

        public Iterable<String> apply(String key) {
            ImmutableSet.Builder result = ImmutableSet.builder();
            for (EntityLink entityLink : AppLinksActivityProvider.this.getEntityLinks(key)) {
                if (!entityLink.getApplicationLink().equals(AppLinksActivityProvider.this.appLink)) continue;
                result.add((Object)entityLink.getKey());
            }
            return result.build();
        }
    };
    private Function<Object, Iterable<EntityLink>> getEntityLinks = new Function<Object, Iterable<EntityLink>>(){

        public Iterable<EntityLink> apply(final Object entity) {
            return (Iterable)AppLinksActivityProvider.this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Iterable<EntityLink>>(){

                public Iterable<EntityLink> doInTransaction() {
                    return AppLinksActivityProvider.this.entityLinkService.getEntityLinks(entity);
                }
            });
        }
    };
    private Supplier<ApplicationLinkRequestFactory> defaultAuthenticatedRequestFactory = new Supplier<ApplicationLinkRequestFactory>(){

        public ApplicationLinkRequestFactory get() {
            return AppLinksActivityProvider.this.appLink.createAuthenticatedRequestFactory();
        }
    };
    private Supplier<ApplicationLinkRequestFactory> anonymousRequestFactory = new Supplier<ApplicationLinkRequestFactory>(){

        public ApplicationLinkRequestFactory get() {
            return AppLinksActivityProvider.this.appLink.createAuthenticatedRequestFactory(Anonymous.class);
        }
    };
    private static final Predicate<ProviderFilterRepresentation> standardFilterOptions = new Predicate<ProviderFilterRepresentation>(){

        public boolean apply(ProviderFilterRepresentation filters) {
            return "streams".equals(filters.getKey());
        }
    };

    public AppLinksActivityProvider(ApplicationLink appLink, EntityLinkService entityLinkService, Iterable<EntityResolver> entityResolvers, FeedParser feedParser, StreamsFeedUriBuilderFactory streamsFeedUriBuilderFactory, StreamsLocaleProvider streamsLocaleProvider, TransactionTemplate transactionTemplate, ApplicationLinkServiceExtensions appLinkServiceExtensions, StreamsI18nResolver i18nResolver) {
        this.appLink = (ApplicationLink)Preconditions.checkNotNull((Object)appLink, (Object)"appLink");
        this.entityLinkService = (EntityLinkService)Preconditions.checkNotNull((Object)entityLinkService, (Object)"entityLinkService");
        this.entityResolvers = (Iterable)Preconditions.checkNotNull(entityResolvers, (Object)"entityResolvers");
        this.feedParser = (FeedParser)Preconditions.checkNotNull((Object)feedParser, (Object)"feedParser");
        this.streamsFeedUriBuilderFactory = (StreamsFeedUriBuilderFactory)Preconditions.checkNotNull((Object)streamsFeedUriBuilderFactory, (Object)"streamsFeedUriBuilderFactory");
        this.streamsLocaleProvider = (StreamsLocaleProvider)Preconditions.checkNotNull((Object)streamsLocaleProvider, (Object)"streamsLocaleProvider");
        this.transactionTemplate = (TransactionTemplate)Preconditions.checkNotNull((Object)transactionTemplate, (Object)"transactionTemplate");
        this.appLinkServiceExtensions = (ApplicationLinkServiceExtensions)Preconditions.checkNotNull((Object)appLinkServiceExtensions, (Object)"appLinkServiceExtensions");
        this.i18nResolver = (StreamsI18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
    }

    @Override
    public boolean matches(String key) {
        return key.endsWith(PROVIDER_KEY_SEPARATOR + this.getKey());
    }

    @Override
    public String getName() {
        return this.appLink.getName();
    }

    @Override
    public String getKey() {
        return this.appLink.getId().get();
    }

    @Override
    public String getBaseUrl() {
        return this.appLink.getDisplayUrl().toString();
    }

    @Override
    public String getType() {
        return this.i18nResolver.getText(this.appLink.getType().getI18nKey());
    }

    @Override
    public CancellableTask<Either<ActivityProvider.Error, FeedModel>> getActivityFeed(final ActivityRequestImpl request) throws StreamsException {
        final AppLinksActivityProvider activityProvider = this;
        return new CancellableTask<Either<ActivityProvider.Error, FeedModel>>(){

            public Either<ActivityProvider.Error, FeedModel> call() {
                URI uri = AppLinksActivityProvider.this.getBuilder(request).getServletUri();
                RetryFeedAsAnonymousIfCredentialsRequiredHandler retryHandler = new RetryFeedAsAnonymousIfCredentialsRequiredHandler(uri, request.getTimeout(), activityProvider);
                return AppLinksActivityProvider.this.fetch((Supplier<ApplicationLinkRequestFactory>)AppLinksActivityProvider.this.defaultAuthenticatedRequestFactory, uri.toASCIIString(), request.getTimeout(), new FeedResponseHandler(uri, retryHandler), retryHandler);
            }

            public CancellableTask.Result cancel() {
                return CancellableTask.Result.INTERRUPT;
            }
        };
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Either<ActivityProvider.Error, FeedModel> addAuthRequestToFeed(Either<ActivityProvider.Error, FeedModel> result, URI feedUri) {
        FeedModel.Builder newFeed;
        URI callbackUri = this.appLinkServiceExtensions.getAuthCallbackUri(this.appLink);
        URI authUri = this.appLink.createAuthenticatedRequestFactory().getAuthorisationURI(callbackUri);
        AuthRequiredFeedHeader header = new AuthRequiredFeedHeader(this.appLink.getId().toString(), this.appLink.getName(), this.appLink.getDisplayUrl(), authUri);
        if (result.isLeft()) {
            if (((ActivityProvider.Error)result.left().get()).getType() != ActivityProvider.Error.Type.CREDENTIALS_REQUIRED) return result;
            newFeed = new FeedModel.Builder(Uri.fromJavaUri((URI)feedUri));
            return Either.right((Object)newFeed.addHeaders((Iterable<FeedHeader>)ImmutableList.of((Object)header)).build());
        } else {
            newFeed = new FeedModel.Builder((FeedModel)result.right().get());
        }
        return Either.right((Object)newFeed.addHeaders((Iterable<FeedHeader>)ImmutableList.of((Object)header)).build());
    }

    @Override
    public Either<ActivityProvider.Error, Iterable<ProviderFilterRepresentation>> getFilters(boolean addApplinkName) {
        String url = "/rest/activity-stream/1.0/config?local=true";
        return this.fetch(this.anonymousRequestFactory, url, 5000, new GetFiltersHandler(url, this, addApplinkName), new ReturnErrorIfCredentialsRequiredHandler(this));
    }

    @Override
    public StreamsKeysRepresentation getKeys() {
        return new StreamsKeysRepresentation((Collection<StreamsKeysRepresentation.StreamsKeyEntry>)ImmutableList.of());
    }

    @Override
    public boolean allKeysAreValid(Iterable<String> keys) {
        String url = "/rest/activity-stream/1.0/validate?local=true&keys=" + Uris.encode((String)Joiner.on((char)',').join(keys)) + "&title=test&numofentries=1";
        return (Boolean)this.fetch(this.anonymousRequestFactory, url, 5000, new KeyValidationHandler(), new ReturnErrorIfCredentialsRequiredHandler(this)).right().toOption().getOrElse((Object)false);
    }

    public Request<?, Response> createRequest(String url, Request.MethodType methodType) throws CredentialsRequiredException {
        ApplicationLinkRequest request = ((ApplicationLinkRequestFactory)this.defaultAuthenticatedRequestFactory.get()).createRequest(methodType, url);
        request.setConnectionTimeout(1000);
        return request;
    }

    public Request<?, Response> createAnonymousRequest(String url, Request.MethodType methodType) throws CredentialsRequiredException {
        ApplicationLinkRequest request = ((ApplicationLinkRequestFactory)this.anonymousRequestFactory.get()).createRequest(methodType, url);
        request.setConnectionTimeout(1000);
        return request;
    }

    private StreamsFeedUriBuilder getBuilder(ActivityRequestImpl request) {
        Iterable localNotKeys;
        Iterable remoteNotKeys;
        StreamsFeedUriBuilder builder = this.createUriBuilder(request);
        Set localIsKeys = Filters.getIsValues((Iterable)request.getStandardFilters().get((Object)"key"));
        Iterable remoteIsKeys = com.google.common.collect.Iterables.concat((Iterable)com.google.common.collect.Iterables.transform((Iterable)localIsKeys, this.toRemoteKey));
        if (com.google.common.collect.Iterables.isEmpty((Iterable)remoteIsKeys) && !com.google.common.collect.Iterables.isEmpty((Iterable)localIsKeys)) {
            throw new NoMatchingRemoteKeysException(localIsKeys);
        }
        if (!com.google.common.collect.Iterables.isEmpty((Iterable)remoteIsKeys)) {
            builder.addStandardFilter("key", StreamsFilterType.Operator.IS, remoteIsKeys);
        }
        if (!com.google.common.collect.Iterables.isEmpty((Iterable)(remoteNotKeys = com.google.common.collect.Iterables.concat((Iterable)com.google.common.collect.Iterables.transform((Iterable)(localNotKeys = Filters.getAllValues((StreamsFilterType.Operator)StreamsFilterType.Operator.NOT, (Collection)request.getStandardFilters().get((Object)"key"))), this.toRemoteKey))))) {
            builder.addStandardFilter("key", StreamsFilterType.Operator.NOT, remoteNotKeys);
        }
        builder.addAuthOnly(true);
        return builder;
    }

    private Iterable<EntityLink> getEntityLinks(String key) {
        return com.google.common.collect.Iterables.concat((Iterable)com.google.common.collect.Iterables.transform((Iterable)Options.catOptions((Iterable)Iterables.revMap(this.entityResolvers, (Object)key)), this.getEntityLinks()));
    }

    private Function<Object, Iterable<EntityLink>> getEntityLinks() {
        return this.getEntityLinks;
    }

    private StreamsFeedUriBuilder createUriBuilder(ActivityRequestImpl request) {
        StreamsFeedUriBuilder builder = this.streamsFeedUriBuilderFactory.getStreamsFeedUriBuilder("");
        for (Map.Entry param : request.getStandardFilters().entries()) {
            String name = (String)param.getKey();
            if (name.equals("key")) continue;
            builder.addStandardFilter(name, (Pair)param.getValue());
        }
        for (Map.Entry param : request.getProviderFilters().entries()) {
            for (String key : request.getKey()) {
                builder.addProviderFilter(this.stripAppLinkName(key), (String)param.getKey(), (Pair)param.getValue());
            }
        }
        for (String provider : com.google.common.collect.Iterables.filter(request.getProviders(), ActivityProviders.matches(this))) {
            builder.addProvider(this.stripAppLinkName(provider));
        }
        builder.addLocalOnly(true);
        builder.addUseAcceptLang(true);
        builder.setMaxResults(request.getMaxResults());
        if (request.getTimeout() != 10000) {
            builder.setTimeout(request.getTimeout());
        }
        return builder;
    }

    private String stripAppLinkName(String key) {
        return key.substring(0, key.lastIndexOf(PROVIDER_KEY_SEPARATOR));
    }

    private String applinkDescription() {
        return this.appLink.getName();
    }

    private <T> Either<ActivityProvider.Error, T> fetch(Supplier<ApplicationLinkRequestFactory> requestFactorySupplier, String url, int timeout, ApplicationLinkResponseHandler<Either<ActivityProvider.Error, T>> response, Function<Option<CredentialsRequiredException>, Either<ActivityProvider.Error, T>> credentialsRequiredHandler) {
        ApplicationLinkRequestFactory requestFactory = (ApplicationLinkRequestFactory)requestFactorySupplier.get();
        try {
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, url);
            request.setConnectionTimeout(1000);
            if (Sys.inDevMode()) {
                request.setSoTimeout(Integer.MAX_VALUE);
            } else {
                request.setSoTimeout(timeout + 5000);
            }
            request.setHeader("Accept-Language", this.acceptLanguageString());
            return (Either)request.execute(response);
        }
        catch (ApplinkResponseException e) {
            if (e.getStatusCode() == 401) {
                if (this.errorMessageContainsCaseInsensitive(e.getMessage(), "full authentication")) {
                    return Either.left((Object)ActivityProvider.Error.credentialsRequired(this));
                }
                return Either.left((Object)ActivityProvider.Error.unauthorized(this));
            }
            return this.handleResponseException(url, e);
        }
        catch (ResponseException e) {
            return this.handleResponseException(url, e);
        }
        catch (CredentialsRequiredException e) {
            return (Either)credentialsRequiredHandler.apply((Object)Option.some((Object)((Object)e)));
        }
    }

    private <T> Either<ActivityProvider.Error, T> handleResponseException(String url, ResponseException e) {
        if (e.getCause() instanceof SocketTimeoutException) {
            log.debug("Timed out fetching feed for '" + this.applinkDescription() + "' from " + url, (Throwable)e);
            return Either.left((Object)ActivityProvider.Error.timeout(this));
        }
        if (this.errorMessageContainsCaseInsensitive(e.getMessage(), "401", "full authentication")) {
            return Either.left((Object)ActivityProvider.Error.credentialsRequired(this));
        }
        log.info("Error fetching feed for '" + this.applinkDescription() + "' from " + url + ": " + e.getMessage());
        return Either.left((Object)ActivityProvider.Error.other(this));
    }

    private boolean errorMessageContainsCaseInsensitive(@Nullable String errorMessage, String ... tokens) {
        if (errorMessage == null) {
            return false;
        }
        String lowerCase = errorMessage.toLowerCase();
        for (String token : tokens) {
            if (lowerCase.contains(token.toLowerCase())) continue;
            return false;
        }
        return true;
    }

    private String acceptLanguageString() {
        Locale userLocale = this.streamsLocaleProvider.getUserLocale();
        Locale appLocale = this.streamsLocaleProvider.getApplicationLocale();
        Preconditions.checkNotNull((Object)userLocale, (Object)"userLocale");
        Preconditions.checkNotNull((Object)appLocale, (Object)"appLocale");
        return String.format("%s, %s;q=0.9, %s;q=0.8, %s;q=0.7, *;q=0.5", this.toLanguageTag(userLocale), userLocale.getLanguage(), this.toLanguageTag(appLocale), appLocale.getLanguage());
    }

    private String toLanguageTag(Locale locale) {
        return locale.toString().toLowerCase().replace("_", "-");
    }

    private <T> T readResponseAs(Response response, MediaType mediaType, Class<T> type) throws IOException, ResponseException {
        return type.cast(this.jsonProvider.readFrom(AppLinksActivityProvider.asClassOfObject(type), AppLinksActivityProvider.asClassOfObject(type), new Annotation[0], mediaType, AppLinksActivityProvider.emptyHeaders(), response.getResponseBodyAsStream()));
    }

    ApplicationLink getApplink() {
        return this.appLink;
    }

    private static Class<Object> asClassOfObject(Class<? extends Object> c) {
        return c;
    }

    private static MultivaluedMap<String, String> emptyHeaders() {
        return AppLinksActivityProvider.emptyMultivaluedMap();
    }

    private static <A, B> MultivaluedMap<A, B> emptyMultivaluedMap() {
        return new MultivaluedMap<A, B>(){

            public void add(A key, B value) {
            }

            public B getFirst(A key) {
                return null;
            }

            public void putSingle(A key, B value) {
            }

            public void clear() {
            }

            public boolean containsKey(Object arg0) {
                return false;
            }

            public boolean containsValue(Object arg0) {
                return false;
            }

            public Set<Map.Entry<A, List<B>>> entrySet() {
                return Collections.emptySet();
            }

            public List<B> get(Object arg0) {
                return null;
            }

            public boolean isEmpty() {
                return true;
            }

            public Set<A> keySet() {
                return Collections.emptySet();
            }

            public List<B> put(A arg0, List<B> arg1) {
                return null;
            }

            public void putAll(Map<? extends A, ? extends List<B>> arg0) {
            }

            public List<B> remove(Object arg0) {
                return null;
            }

            public int size() {
                return 0;
            }

            public Collection<List<B>> values() {
                return ImmutableList.of();
            }
        };
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AppLinksActivityProvider that = (AppLinksActivityProvider)o;
        return !(this.appLink != null ? !this.appLink.equals(that.appLink) : that.appLink != null);
    }

    public int hashCode() {
        return this.appLink != null ? this.appLink.hashCode() : 0;
    }

    private static final class ToAppLinksFilterRepresentation
    implements Function<ProviderFilterRepresentation, ProviderFilterRepresentation> {
        private final AppLinksActivityProvider provider;
        private final Boolean addApplinkName;

        public ToAppLinksFilterRepresentation(AppLinksActivityProvider provider, boolean addApplinkName) {
            this.provider = provider;
            this.addApplinkName = addApplinkName;
        }

        public ProviderFilterRepresentation apply(ProviderFilterRepresentation rep) {
            return new ProviderFilterRepresentation(rep.getKey() + AppLinksActivityProvider.PROVIDER_KEY_SEPARATOR + this.provider.getKey(), this.addApplinkName == false || this.provider.getName().equals(rep.getName()) ? rep.getName() : this.provider.getName() + " - " + rep.getName(), this.provider.getName(), rep.getOptions(), rep.getProviderAliasOptionKey());
        }
    }

    private final class GetFiltersHandler
    implements ApplicationLinkResponseHandler<Either<ActivityProvider.Error, Iterable<ProviderFilterRepresentation>>> {
        private final String url;
        private final AppLinksActivityProvider provider;
        private final Boolean addApplinkName;

        public GetFiltersHandler(String url, AppLinksActivityProvider provider, boolean addApplinkName) {
            this.url = url;
            this.provider = provider;
            this.addApplinkName = addApplinkName;
        }

        public Either<ActivityProvider.Error, Iterable<ProviderFilterRepresentation>> handle(Response response) throws ResponseException {
            if (response.getStatusCode() != Response.Status.OK.getStatusCode()) {
                log.warn("Unable to retrieve filter options from {}", (Object)this.url);
                return Either.right((Object)ImmutableList.of());
            }
            try {
                return Either.right((Object)com.google.common.collect.Iterables.transform((Iterable)com.google.common.collect.Iterables.filter(((StreamsConfigRepresentation)AppLinksActivityProvider.this.readResponseAs(response, MediaTypes.STREAMS_JSON_TYPE, StreamsConfigRepresentation.class)).getFilters(), (Predicate)Predicates.not((Predicate)standardFilterOptions)), this.toAppLinksFilterRepresentation(this.provider, this.addApplinkName)));
            }
            catch (IOException e) {
                log.warn("Unable to retrieve filters from " + this.url, (Throwable)e);
                return Either.right((Object)ImmutableList.of());
            }
        }

        private Function<ProviderFilterRepresentation, ProviderFilterRepresentation> toAppLinksFilterRepresentation(AppLinksActivityProvider provider, boolean addApplinkName) {
            return new ToAppLinksFilterRepresentation(provider, addApplinkName);
        }

        public Either<ActivityProvider.Error, Iterable<ProviderFilterRepresentation>> credentialsRequired(Response response) throws ResponseException {
            return this.handle(response);
        }
    }

    private static final class KeyValidationHandler
    implements ApplicationLinkResponseHandler<Either<ActivityProvider.Error, Boolean>> {
        private KeyValidationHandler() {
        }

        public Either<ActivityProvider.Error, Boolean> handle(Response response) throws ResponseException {
            return Either.right((Object)(response.getStatusCode() == Response.Status.OK.getStatusCode() ? 1 : 0));
        }

        public Either<ActivityProvider.Error, Boolean> credentialsRequired(Response response) throws ResponseException {
            return Either.right((Object)false);
        }
    }

    private final class FeedResponseHandler
    implements ApplicationLinkResponseHandler<Either<ActivityProvider.Error, FeedModel>> {
        private final URI uri;
        private final Function<Option<CredentialsRequiredException>, Either<ActivityProvider.Error, FeedModel>> retryHandler;

        FeedResponseHandler(URI uri, Function<Option<CredentialsRequiredException>, Either<ActivityProvider.Error, FeedModel>> retryHandler) {
            this.uri = uri;
            this.retryHandler = retryHandler;
        }

        public Either<ActivityProvider.Error, FeedModel> handle(Response response) throws ResponseException {
            if (!response.isSuccessful()) {
                throw new ApplinkResponseException("Error " + response.getStatusCode() + ": '" + response.getStatusText() + "' received when retrieving activity from '" + AppLinksActivityProvider.this.appLink.getRpcUrl() + this.uri + "'", response.getStatusCode());
            }
            return Either.right((Object)this.readStream(response.getResponseBodyAsStream(), this.getCharset(response)));
        }

        private String getCharset(Response response) {
            MediaType contentType = MediaType.valueOf((String)response.getHeader("Content-Type"));
            return (String)Option.option(contentType.getParameters().get("charset")).getOrElse((Object)"ISO-8859-1");
        }

        private FeedModel readStream(InputStream entity, String charset) throws ResponseException {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy((InputStream)entity, (OutputStream)baos);
                InputStreamReader reader = new InputStreamReader((InputStream)new ByteArrayInputStream(baos.toByteArray()), charset);
                return AppLinksActivityProvider.this.feedParser.readFeed(reader);
            }
            catch (ClassCastException cce) {
                throw new ResponseException("Response is not an atom feed");
            }
            catch (ParseException pe) {
                throw new ResponseException("Error reading returned atom feed from " + AppLinksActivityProvider.this.appLink.getRpcUrl() + this.uri, (Throwable)pe);
            }
            catch (IOException e) {
                throw new ResponseException("Error reading returned atom feed from " + AppLinksActivityProvider.this.appLink.getRpcUrl() + this.uri, (Throwable)e);
            }
        }

        public Either<ActivityProvider.Error, FeedModel> credentialsRequired(Response response) throws ResponseException {
            return (Either)this.retryHandler.apply((Object)Option.none());
        }
    }

    private class RetryFeedAsAnonymousIfCredentialsRequiredHandler
    implements Function<Option<CredentialsRequiredException>, Either<ActivityProvider.Error, FeedModel>> {
        private final URI feedUri;
        private final int timeout;
        private final ActivityProvider activityProvider;

        public RetryFeedAsAnonymousIfCredentialsRequiredHandler(URI feedUri, int timeout, ActivityProvider activityProvider) {
            this.feedUri = feedUri;
            this.timeout = timeout;
            this.activityProvider = activityProvider;
        }

        public Either<ActivityProvider.Error, FeedModel> apply(Option<CredentialsRequiredException> e) {
            ReturnErrorIfCredentialsRequiredHandler<FeedModel> noRetryHandler = new ReturnErrorIfCredentialsRequiredHandler<FeedModel>(this.activityProvider);
            return AppLinksActivityProvider.this.addAuthRequestToFeed((Either<ActivityProvider.Error, FeedModel>)AppLinksActivityProvider.this.fetch((Supplier<ApplicationLinkRequestFactory>)AppLinksActivityProvider.this.anonymousRequestFactory, this.feedUri.toASCIIString(), this.timeout, new FeedResponseHandler(this.feedUri, noRetryHandler), noRetryHandler), this.feedUri);
        }
    }

    private class ReturnErrorIfCredentialsRequiredHandler<T>
    implements Function<Option<CredentialsRequiredException>, Either<ActivityProvider.Error, T>> {
        private final ActivityProvider activityProvider;

        private ReturnErrorIfCredentialsRequiredHandler(ActivityProvider activityProvider) {
            this.activityProvider = activityProvider;
        }

        public Either<ActivityProvider.Error, T> apply(Option<CredentialsRequiredException> e) {
            return Either.left((Object)ActivityProvider.Error.credentialsRequired(this.activityProvider, e));
        }
    }
}

