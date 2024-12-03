/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.atlassian.streams.api.StreamsException
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.atlassian.streams.api.common.uri.Uris
 *  com.atlassian.streams.spi.ServletPath
 *  com.atlassian.streams.spi.StreamsCommentHandler$PostReplyError
 *  com.atlassian.streams.spi.StreamsCommentHandler$PostReplyError$Type
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.atlassian.streams.api.StreamsException;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.api.common.uri.Uris;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderWithAnalytics;
import com.atlassian.streams.internal.ActivityProviders;
import com.atlassian.streams.internal.AppLinksActivityProvider;
import com.atlassian.streams.internal.LocalActivityProvider;
import com.atlassian.streams.internal.MissingModuleKeyException;
import com.atlassian.streams.internal.NoSuchModuleException;
import com.atlassian.streams.internal.RemotePostValidationException;
import com.atlassian.streams.spi.ServletPath;
import com.atlassian.streams.spi.StreamsCommentHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostReplyHandler {
    private static final Logger log = LoggerFactory.getLogger(PostReplyHandler.class);
    @Deprecated
    public static final String NO_CHECK = "no-check";
    private final ActivityProviders activityProviders;
    private final ApplicationProperties applicationProperties;

    public PostReplyHandler(ActivityProviders activityProviders, ApplicationProperties applicationProperties) {
        this.activityProviders = (ActivityProviders)Preconditions.checkNotNull((Object)activityProviders, (Object)"activityProviders");
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
    }

    public Either<StreamsCommentHandler.PostReplyError, URI> postReply(String pathInfo, String comment, String replyTo) {
        URI baseUri = URI.create(this.applicationProperties.getBaseUrl());
        if (replyTo != null) {
            String thisPath = baseUri.toASCIIString() + ServletPath.COMMENTS.getPath();
            if (replyTo.equals(thisPath) || replyTo.startsWith(thisPath + "/")) {
                return this.routeCommentToLocalProvider(baseUri, replyTo.substring(thisPath.length()), comment);
            }
            return this.forwardCommentRequest(replyTo, comment);
        }
        return this.routeCommentToLocalProvider(baseUri, pathInfo, comment);
    }

    private Either<StreamsCommentHandler.PostReplyError, URI> routeCommentToLocalProvider(URI baseUri, String pathInfo, String comment) {
        ArrayList<String> pathElements;
        String moduleKey;
        if (pathInfo != null && pathInfo.length() > 0) {
            String[] parts = pathInfo.split("/");
            moduleKey = Uris.decode((String)parts[1]);
            pathElements = new ArrayList<String>(parts.length - 1);
            for (int i = 2; i < parts.length; ++i) {
                pathElements.add(parts[i]);
            }
        } else {
            throw new MissingModuleKeyException();
        }
        try {
            ActivityProvider provider = (ActivityProvider)Iterables.getOnlyElement(this.activityProviders.get(ActivityProviders.module(moduleKey)));
            if (provider instanceof LocalActivityProvider) {
                return ((LocalActivityProvider)provider).postReply(baseUri, pathElements, comment);
            }
            if (ActivityProviders.isActivityProviderWithAnalyticsWithDelegate(provider, LocalActivityProvider.class)) {
                return ((LocalActivityProvider)((ActivityProviderWithAnalytics)provider).getDelegate()).postReply(baseUri, pathElements, comment);
            }
            throw new StreamsException("No suitable provider type found for module key: " + moduleKey);
        }
        catch (NoSuchElementException e) {
            throw new NoSuchModuleException(moduleKey);
        }
    }

    private Either<StreamsCommentHandler.PostReplyError, URI> forwardCommentRequest(String replyTo, String comment) {
        Iterable<String> errors = this.validateReplyTo(replyTo);
        if (Iterables.isEmpty(errors)) {
            Option<AppLinksActivityProvider> provider = this.activityProviders.getRemoteProviderForUri(Uri.parse((String)replyTo));
            if (!provider.isDefined()) {
                log.warn("no remote activity provider found for comment URL " + replyTo);
                return Either.left((Object)new StreamsCommentHandler.PostReplyError(StreamsCommentHandler.PostReplyError.Type.FORBIDDEN));
            }
            try {
                Either<StreamsCommentHandler.PostReplyError, URI> response = this.executeRequest(((AppLinksActivityProvider)provider.get()).createRequest(replyTo, Request.MethodType.POST), comment);
                if (response.isLeft() && 401 == ((StreamsCommentHandler.PostReplyError)response.left().get()).getType().getStatusCode()) {
                    return this.retryFeedAsAnonymous(provider, replyTo, comment);
                }
                return response;
            }
            catch (CredentialsRequiredException e) {
                return this.retryFeedAsAnonymous(provider, replyTo, comment);
            }
        }
        throw new RemotePostValidationException(errors);
    }

    private Either<StreamsCommentHandler.PostReplyError, URI> retryFeedAsAnonymous(Option<AppLinksActivityProvider> provider, String replyTo, String comment) {
        try {
            return this.executeRequest(((AppLinksActivityProvider)provider.get()).createAnonymousRequest(replyTo, Request.MethodType.POST), comment);
        }
        catch (CredentialsRequiredException e2) {
            return Either.left((Object)new StreamsCommentHandler.PostReplyError(StreamsCommentHandler.PostReplyError.Type.UNAUTHORIZED));
        }
    }

    @VisibleForTesting
    Either<StreamsCommentHandler.PostReplyError, URI> executeRequest(Request<?, Response> request, String comment) {
        try {
            request.setHeader("X-Atlassian-Token", NO_CHECK);
            request.addRequestParameters(new String[]{"comment", comment});
            return (Either)request.executeAndReturn((ReturningResponseHandler)new AddCommentHandler());
        }
        catch (ResponseException e) {
            return Either.left((Object)new StreamsCommentHandler.PostReplyError(StreamsCommentHandler.PostReplyError.Type.REMOTE_POST_REPLY_ERROR));
        }
    }

    private Iterable<String> validateReplyTo(String replyTo) {
        if (!replyTo.contains(ServletPath.COMMENTS.getPath())) {
            return ImmutableList.of((Object)"Will not forward comment request to URL that does not contain streams servlet comments path");
        }
        if (replyTo.contains("?")) {
            return ImmutableList.of((Object)"Will not forward comment request to URL that contains a query string");
        }
        if (!replyTo.matches("https?://.*")) {
            return ImmutableList.of((Object)"Will not forward comment request to URL that doesn't look like an HTTP url");
        }
        return ImmutableList.of();
    }

    private final class AddCommentHandler
    implements ReturningResponseHandler<Response, Either<StreamsCommentHandler.PostReplyError, URI>> {
        private AddCommentHandler() {
        }

        public Either<StreamsCommentHandler.PostReplyError, URI> handle(Response response) throws ResponseException {
            switch (response.getStatusCode()) {
                case 201: {
                    return Either.right((Object)URI.create(response.getHeader("Location")));
                }
                case 401: {
                    return Either.left((Object)new StreamsCommentHandler.PostReplyError(StreamsCommentHandler.PostReplyError.Type.UNAUTHORIZED));
                }
            }
            return Either.left((Object)new StreamsCommentHandler.PostReplyError(StreamsCommentHandler.PostReplyError.Type.REMOTE_POST_REPLY_ERROR));
        }
    }
}

