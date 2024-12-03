/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.common.auth.oauth.ApplinksOAuth;
import com.atlassian.applinks.internal.common.net.ResponseContentException;
import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorExceptionFactory;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkErrors;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.error.OAuthErrorMatchers;
import com.atlassian.applinks.internal.status.error.ResponseApplinkError;
import com.atlassian.applinks.internal.status.error.SimpleApplinkStatusException;
import com.atlassian.applinks.internal.status.error.UnexpectedResponseError;
import com.atlassian.applinks.internal.status.error.UnexpectedResponseStatusError;
import com.atlassian.applinks.internal.status.remote.RemoteNetworkException;
import com.atlassian.applinks.internal.status.remote.RemoteStatusUnknownException;
import com.atlassian.applinks.internal.status.remote.ResponseApplinkStatusException;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

public final class NetworkErrorTranslator {
    private static final List<RemoteExceptionMatcher> MATCHERS = ImmutableList.builder().add((Object)ApplinkErrorMatcher.INSTANCE).add((Object)NetworkErrorTranslator.byTypeAndFuzzyMessageMatcher(ApplinkErrorType.CONNECTION_REFUSED, ConnectException.class, "Connection refused")).add((Object)NetworkErrorTranslator.byTypeMatcher(ApplinkErrorType.UNKNOWN_HOST, UnknownHostException.class)).add((Object)NetworkErrorTranslator.byTypeMatcher(ApplinkErrorType.SSL_UNTRUSTED, SSLHandshakeException.class)).add((Object)NetworkErrorTranslator.byTypeMatcher(ApplinkErrorType.SSL_HOSTNAME_UNMATCHED, SSLPeerUnverifiedException.class)).add((Object)NetworkErrorTranslator.byTypeAndFuzzyMessageMatcher(ApplinkErrorType.SSL_UNMATCHED, SSLException.class, "certificate")).add((Object)OAuthErrorMatchers.matchOAuthProblem(ApplinkErrorType.OAUTH_TIMESTAMP_REFUSED, ApplinksOAuth.PROBLEM_TIMESTAMP_REFUSED)).add((Object)OAuthErrorMatchers.matchOAuthProblem(ApplinkErrorType.OAUTH_SIGNATURE_INVALID, ApplinksOAuth.PROBLEM_SIGNATURE_INVALID)).add((Object)OAuthErrorMatchers.fallback()).add((Object)ResponseContentMatcher.INSTANCE).add((Object)ResponseStatusMatcher.INSTANCE).build();

    private NetworkErrorTranslator() {
    }

    @Nonnull
    public static ApplinkStatusException toApplinkErrorException(@Nonnull Throwable e, @Nullable String message) {
        for (RemoteExceptionMatcher matcher : MATCHERS) {
            if (!matcher.matches(e)) continue;
            return matcher.createMatchingError(message, e);
        }
        return new RemoteStatusUnknownException(message, e);
    }

    @Nonnull
    public static ApplinkError toApplinkError(@Nonnull Throwable e, @Nullable String message) {
        return NetworkErrorTranslator.toApplinkErrorException(e, message);
    }

    private static Predicate<Throwable> withMessageMatching(final String expectedMessage) {
        return new Predicate<Throwable>(){

            public boolean apply(Throwable error) {
                return expectedMessage.equalsIgnoreCase(error.getMessage());
            }
        };
    }

    private static Predicate<Throwable> withMessageContaining(final String expectedMessage) {
        return new Predicate<Throwable>(){

            public boolean apply(Throwable error) {
                return error.getMessage() != null && error.getMessage().toLowerCase().contains(expectedMessage.toLowerCase());
            }
        };
    }

    private static RemoteExceptionMatcher byTypeMatcher(ApplinkErrorType applinkErrorType, Class<? extends Throwable> expectedType) {
        return new ByTypeMatcher(applinkErrorType, expectedType);
    }

    private static RemoteExceptionMatcher byTypeAndExactMessageMatcher(ApplinkErrorType applinkErrorType, Class<? extends Throwable> expectedType, String expectedMessage) {
        return new ByTypeAndExactMessageMatcher(applinkErrorType, expectedType, expectedMessage);
    }

    private static RemoteExceptionMatcher byTypeAndFuzzyMessageMatcher(ApplinkErrorType applinkErrorType, Class<? extends Throwable> expectedType, String expectedMessage) {
        return new ByTypeAndFuzzyMessageMatcher(applinkErrorType, expectedType, expectedMessage);
    }

    static class ResponseStatusMatcher
    extends ByTypeMatcher {
        static final ResponseStatusMatcher INSTANCE = new ResponseStatusMatcher();

        public ResponseStatusMatcher() {
            super(ApplinkErrorType.UNEXPECTED_RESPONSE_STATUS, ResponseStatusException.class);
        }

        @Override
        public ApplinkStatusException createMatchingError(String message, Throwable original) {
            ResponseStatusException statusException = ApplinkErrors.findCauseOfType(original, ResponseStatusException.class);
            Response response = statusException.getResponse();
            return response != null ? new ResponseApplinkStatusException((ResponseApplinkError)new UnexpectedResponseStatusError(response), (Throwable)statusException) : new SimpleApplinkStatusException(ApplinkErrorType.UNEXPECTED_RESPONSE_STATUS, "<UNKNOWN STATUS>", (Throwable)statusException);
        }
    }

    static class ResponseContentMatcher
    extends ByTypeMatcher {
        static final ResponseContentMatcher INSTANCE = new ResponseContentMatcher();

        public ResponseContentMatcher() {
            super(ApplinkErrorType.UNEXPECTED_RESPONSE, ResponseContentException.class);
        }

        @Override
        public ApplinkStatusException createMatchingError(String message, Throwable original) {
            ResponseContentException responseException = ApplinkErrors.findCauseOfType(original, ResponseContentException.class);
            return new ResponseApplinkStatusException((ResponseApplinkError)new UnexpectedResponseError(responseException.getResponse()), (Throwable)((Object)responseException));
        }
    }

    static class ApplinkErrorMatcher
    implements RemoteExceptionMatcher {
        static final ApplinkErrorMatcher INSTANCE = new ApplinkErrorMatcher();

        ApplinkErrorMatcher() {
        }

        @Override
        public boolean matches(Throwable original) {
            return this.findApplinkError(original) != null;
        }

        @Override
        public ApplinkStatusException createMatchingError(String message, Throwable original) {
            return this.findApplinkError(original).accept(new ApplinkErrorExceptionFactory());
        }

        private ApplinkError findApplinkError(Throwable original) {
            return (ApplinkError)((Object)ApplinkErrors.findCauseMatching(original, (Predicate<? super Throwable>)Predicates.instanceOf(ApplinkError.class)));
        }
    }

    static class ByTypeAndFuzzyMessageMatcher
    extends ByTypeMatcher {
        private final String expectedMessage;

        ByTypeAndFuzzyMessageMatcher(ApplinkErrorType applinkErrorType, Class<? extends Throwable> expectedType, String expectedMessage) {
            super(applinkErrorType, expectedType);
            this.expectedMessage = expectedMessage;
        }

        @Override
        public boolean matches(Throwable original) {
            return ApplinkErrors.findCauseMatching(original, (Predicate<? super Throwable>)Predicates.and((Predicate)Predicates.instanceOf((Class)this.expectedType), (Predicate)NetworkErrorTranslator.withMessageContaining(this.expectedMessage))) != null;
        }
    }

    static class ByTypeAndExactMessageMatcher
    extends ByTypeMatcher {
        private final String expectedMessage;

        ByTypeAndExactMessageMatcher(ApplinkErrorType applinkErrorType, Class<? extends Throwable> expectedType, String expectedMessage) {
            super(applinkErrorType, expectedType);
            this.expectedMessage = expectedMessage;
        }

        @Override
        public boolean matches(Throwable original) {
            return ApplinkErrors.findCauseMatching(original, (Predicate<? super Throwable>)Predicates.and((Predicate)Predicates.instanceOf((Class)this.expectedType), (Predicate)NetworkErrorTranslator.withMessageMatching(this.expectedMessage))) != null;
        }
    }

    static class ByTypeMatcher
    extends NetworkExceptionMatcher {
        protected final Class<? extends Throwable> expectedType;

        ByTypeMatcher(ApplinkErrorType applinkErrorType, Class<? extends Throwable> expectedType) {
            super(applinkErrorType, expectedType);
            this.expectedType = expectedType;
        }

        @Override
        public boolean matches(Throwable original) {
            return ApplinkErrors.findCauseOfType(original, this.expectedType) != null;
        }
    }

    static abstract class NetworkExceptionMatcher
    implements RemoteExceptionMatcher {
        protected final ApplinkErrorType applinkErrorType;
        protected final Class<? extends Throwable> underlyingErrorType;

        protected NetworkExceptionMatcher(ApplinkErrorType applinkErrorType, Class<? extends Throwable> underlyingErrorType) {
            this.applinkErrorType = applinkErrorType;
            this.underlyingErrorType = underlyingErrorType;
        }

        @Override
        public ApplinkStatusException createMatchingError(String message, Throwable original) {
            return new RemoteNetworkException(this.applinkErrorType, this.underlyingErrorType, message, original);
        }
    }

    static interface RemoteExceptionMatcher {
        public boolean matches(Throwable var1);

        public ApplinkStatusException createMatchingError(String var1, Throwable var2);
    }
}

