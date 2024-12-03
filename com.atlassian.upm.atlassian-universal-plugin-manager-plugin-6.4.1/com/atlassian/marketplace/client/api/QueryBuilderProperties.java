/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;

public abstract class QueryBuilderProperties {
    private QueryBuilderProperties() {
    }

    static class ApplicationCriteriaHelper {
        public final Optional<ApplicationKey> application;
        public final Optional<Integer> appBuildNumber;

        public ApplicationCriteriaHelper() {
            this(Optional.empty(), Optional.empty());
        }

        private ApplicationCriteriaHelper(Optional<ApplicationKey> application, Optional<Integer> appBuildNumber) {
            this.application = application;
            this.appBuildNumber = appBuildNumber;
        }

        public ApplicationCriteriaHelper application(Optional<ApplicationKey> application) {
            return new ApplicationCriteriaHelper((Optional)Preconditions.checkNotNull(application), this.appBuildNumber);
        }

        public ApplicationCriteriaHelper appBuildNumber(Optional<Integer> appBuildNumber) {
            return new ApplicationCriteriaHelper(this.application, (Optional)Preconditions.checkNotNull(appBuildNumber));
        }

        public Iterable<String> describe() {
            ImmutableList.Builder ret = ImmutableList.builder();
            for (ApplicationKey a : Convert.iterableOf(this.application)) {
                ret.add((Object)("application(" + a.getKey() + ")"));
            }
            for (Integer ab : Convert.iterableOf(this.appBuildNumber)) {
                ret.add((Object)("appBuildNumber(" + ab + ")"));
            }
            return ret.build();
        }
    }

    public static interface WithVersion<T extends WithVersion<T>> {
        public T withVersion(boolean var1);
    }

    public static interface IncludePrivate<T extends IncludePrivate<T>> {
        public T includePrivate(boolean var1);
    }

    public static interface MultiHosting<T extends MultiHosting<T>> {
        public T hosting(List<HostingType> var1);
    }

    public static interface Hosting<T extends Hosting<T>> {
        public T hosting(Optional<HostingType> var1);
    }

    public static interface Cost<T extends Cost<T>> {
        public T cost(Optional<com.atlassian.marketplace.client.api.Cost> var1);
    }

    public static interface Bounds<T extends Bounds<T>> {
        public T bounds(QueryBounds var1);
    }

    public static interface ApplicationCriteria<T extends ApplicationCriteria<T>> {
        public T application(Optional<ApplicationKey> var1);

        public T appBuildNumber(Optional<Integer> var1);
    }

    public static interface AccessToken<T extends AccessToken<T>> {
        public T accessToken(Optional<String> var1);
    }
}

