/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.streams.api.StreamsException
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.spi.CancellableTask
 */
package com.atlassian.streams.internal;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.streams.api.StreamsException;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.internal.ActivityRequestImpl;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.internal.rest.representations.ProviderFilterRepresentation;
import com.atlassian.streams.internal.rest.representations.StreamsKeysRepresentation;
import com.atlassian.streams.spi.CancellableTask;

public interface ActivityProvider {
    public boolean matches(String var1);

    public String getName();

    public String getKey();

    public String getBaseUrl();

    public String getType();

    public CancellableTask<Either<Error, FeedModel>> getActivityFeed(ActivityRequestImpl var1) throws StreamsException;

    public Either<Error, Iterable<ProviderFilterRepresentation>> getFilters(boolean var1);

    public StreamsKeysRepresentation getKeys();

    public boolean allKeysAreValid(Iterable<String> var1);

    public static class Error {
        private final Type type;
        private final Option<Exception> cause;
        private final Option<String> applicationLinkName;
        private final Option<ActivityProvider> activityProvider;

        private Error(Type type, ActivityProvider activityProvider) {
            this(type, (Option<String>)Option.option((Object)activityProvider.getName()), (Option<Exception>)Option.none(Exception.class), (Option<ActivityProvider>)Option.option((Object)activityProvider));
        }

        private Error(Type type, Option<String> applicationLinkName, Option<Exception> cause, Option<ActivityProvider> activityProvider) {
            this.type = type;
            this.cause = cause;
            this.applicationLinkName = applicationLinkName;
            this.activityProvider = activityProvider;
        }

        public Type getType() {
            return this.type;
        }

        public Option<Exception> getCause() {
            return this.cause;
        }

        public Option<String> getApplicationLinkName() {
            return this.applicationLinkName;
        }

        public Option<ActivityProvider> getActivityProvider() {
            return this.activityProvider;
        }

        public static Error timeout(ActivityProvider activityProvider) {
            return new Error(Type.TIMEOUT, activityProvider);
        }

        public static Error credentialsRequired(ActivityProvider activityProvider, Option<CredentialsRequiredException> e) {
            return new Error(Type.CREDENTIALS_REQUIRED, (Option<String>)Option.none(String.class), (Option<Exception>)Option.option((Object)e.get()), (Option<ActivityProvider>)Option.option((Object)activityProvider));
        }

        public static Error credentialsRequired(ActivityProvider activityProvider) {
            return new Error(Type.CREDENTIALS_REQUIRED, activityProvider);
        }

        public static Error unauthorized(ActivityProvider activityProvider) {
            return new Error(Type.UNAUTHORIZED, activityProvider);
        }

        public static Error other(ActivityProvider activityProvider) {
            return new Error(Type.OTHER, activityProvider);
        }

        public static Error timeout() {
            return new Error(Type.TIMEOUT, (Option<String>)Option.none(String.class), (Option<Exception>)Option.none(Exception.class), (Option<ActivityProvider>)Option.none(ActivityProvider.class));
        }

        public static Error other() {
            return new Error(Type.OTHER, (Option<String>)Option.none(String.class), (Option<Exception>)Option.none(Exception.class), (Option<ActivityProvider>)Option.none(ActivityProvider.class));
        }

        public static Error banned(ActivityProvider activityProvider) {
            return new Error(Type.BANNED, activityProvider);
        }

        public static Error throttled(ActivityProvider activityProvider) {
            return new Error(Type.THROTTLED, activityProvider);
        }

        public String toString() {
            return this.type.name().toLowerCase();
        }

        public static enum Type {
            TIMEOUT,
            OTHER,
            CREDENTIALS_REQUIRED,
            BANNED,
            THROTTLED,
            UNAUTHORIZED;

        }
    }
}

