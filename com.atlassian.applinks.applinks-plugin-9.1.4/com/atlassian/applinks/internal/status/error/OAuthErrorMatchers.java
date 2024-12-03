/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.common.auth.oauth.OAuthMessageProblemException;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkErrors;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.error.NetworkErrorTranslator;
import com.atlassian.applinks.internal.status.remote.RemoteOAuthException;
import com.google.common.base.Predicate;
import javax.annotation.Nonnull;

final class OAuthErrorMatchers {
    private OAuthErrorMatchers() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    private static Predicate<Throwable> withOAuthProblem(final @Nonnull String oAuthProblem) {
        return new Predicate<Throwable>(){

            public boolean apply(Throwable error) {
                return error instanceof OAuthMessageProblemException && oAuthProblem.equals(((OAuthMessageProblemException)((Object)OAuthMessageProblemException.class.cast(error))).getOAuthProblem());
            }
        };
    }

    @Nonnull
    static OAuthProblemMatcher matchOAuthProblem(@Nonnull ApplinkErrorType applinkErrorType, @Nonnull String oauthProblem) {
        return new SpecificOAuthProblemMatcher(applinkErrorType, oauthProblem);
    }

    @Nonnull
    static OAuthProblemMatcher fallback() {
        return new OAuthProblemMatcher(ApplinkErrorType.OAUTH_PROBLEM);
    }

    static class SpecificOAuthProblemMatcher
    extends OAuthProblemMatcher {
        private final Predicate<Throwable> errorMatcher;

        SpecificOAuthProblemMatcher(@Nonnull ApplinkErrorType applinkErrorType, @Nonnull String oauthProblem) {
            super(applinkErrorType);
            this.errorMatcher = OAuthErrorMatchers.withOAuthProblem(oauthProblem);
        }

        @Override
        public boolean matches(Throwable original) {
            return ApplinkErrors.findCauseMatching(original, this.errorMatcher) != null;
        }
    }

    static class OAuthProblemMatcher
    extends NetworkErrorTranslator.ByTypeMatcher {
        OAuthProblemMatcher(ApplinkErrorType applinkErrorType) {
            super(applinkErrorType, OAuthMessageProblemException.class);
        }

        @Override
        public ApplinkStatusException createMatchingError(String message, Throwable original) {
            return new RemoteOAuthException(this.applinkErrorType, message, original);
        }
    }
}

