/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Either
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderCallable;
import com.atlassian.streams.internal.ActivityProviders;
import com.atlassian.streams.internal.StreamsCompletionService;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public class ProjectKeyValidator {
    private final ActivityProviders activityProviders;
    private final StreamsCompletionService completionService;

    ProjectKeyValidator(ActivityProviders activityProviders, StreamsCompletionService completionService) {
        this.activityProviders = (ActivityProviders)Preconditions.checkNotNull((Object)activityProviders, (Object)"activityProviders");
        this.completionService = (StreamsCompletionService)Preconditions.checkNotNull((Object)completionService, (Object)"completionService");
    }

    public boolean allKeysAreValid(Iterable<String> keys, boolean local) {
        Iterable<ActivityProvider> providers = this.activityProviders.get(ActivityProviders.localOnly(local), this.completionService.reachable());
        Iterable callables = Iterables.transform(providers, this.toKeysValidatorCallable(keys));
        return Iterables.contains((Iterable)Either.getRights(this.completionService.execute(callables)), (Object)true);
    }

    private Function<ActivityProvider, ActivityProviderCallable<Either<ActivityProvider.Error, Boolean>>> toKeysValidatorCallable(final Iterable<String> keys) {
        return new Function<ActivityProvider, ActivityProviderCallable<Either<ActivityProvider.Error, Boolean>>>(){

            public ActivityProviderCallable<Either<ActivityProvider.Error, Boolean>> apply(final ActivityProvider provider) {
                return new ActivityProviderCallable<Either<ActivityProvider.Error, Boolean>>(){

                    @Override
                    public Either<ActivityProvider.Error, Boolean> call() throws Exception {
                        return Either.right((Object)provider.allKeysAreValid(keys));
                    }

                    @Override
                    public ActivityProvider getActivityProvider() {
                        return provider;
                    }
                };
            }
        };
    }
}

