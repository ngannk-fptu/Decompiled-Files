/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest.model.auth.compatibility;

import com.atlassian.applinks.internal.common.rest.model.oauth.RestConsumer;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.applinks.internal.rest.model.auth.compatibility.RestAuthenticationProvider;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collections;
import javax.annotation.Nonnull;

public class RestApplicationLinkAuthentication
extends BaseRestEntity {
    public static final String CONSUMERS = "consumers";
    public static final String CONFIGURED_AUTHENTICATION_PROVIDERS = "configuredAuthProviders";

    @Nonnull
    public Iterable<RestConsumer> getConsumers() {
        if (this.containsKey(CONSUMERS)) {
            Iterable consumers = (Iterable)this.get(CONSUMERS);
            Iterable filtered = Iterables.filter((Iterable)consumers, (Predicate)Predicates.notNull());
            return ImmutableList.copyOf((Iterable)Iterables.transform((Iterable)filtered, RestConsumer.REST_TRANSFORM));
        }
        return Collections.emptyList();
    }

    @Nonnull
    public Iterable<RestAuthenticationProvider> getConfiguredAuthenticationProviders() {
        if (this.containsKey(CONFIGURED_AUTHENTICATION_PROVIDERS)) {
            Iterable authProviders = (Iterable)this.get(CONFIGURED_AUTHENTICATION_PROVIDERS);
            Iterable filtered = Iterables.filter((Iterable)authProviders, (Predicate)Predicates.notNull());
            return ImmutableList.copyOf((Iterable)Iterables.transform((Iterable)filtered, RestAuthenticationProvider.REST_TRANSFORM));
        }
        return Collections.emptyList();
    }
}

