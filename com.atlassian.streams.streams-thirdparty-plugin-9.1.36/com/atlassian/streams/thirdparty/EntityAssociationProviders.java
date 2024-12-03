/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Options
 *  com.atlassian.streams.spi.ActivityProviderModuleDescriptor
 *  com.atlassian.streams.spi.EntityIdentifier
 *  com.atlassian.streams.spi.SessionManager
 *  com.atlassian.streams.spi.StreamsEntityAssociationProvider
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.streams.thirdparty;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Options;
import com.atlassian.streams.spi.ActivityProviderModuleDescriptor;
import com.atlassian.streams.spi.EntityIdentifier;
import com.atlassian.streams.spi.SessionManager;
import com.atlassian.streams.spi.StreamsEntityAssociationProvider;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

class EntityAssociationProviders {
    private final Logger log = LoggerFactory.getLogger(EntityAssociationProviders.class);
    private final PluginAccessor pluginAccessor;
    private final TransactionTemplate transactionTemplate;
    private final SessionManager sessionManager;
    private final UserManager userManager;
    private final Function<ActivityProviderModuleDescriptor, StreamsEntityAssociationProvider> toAssociationProviderFunction = new Function<ActivityProviderModuleDescriptor, StreamsEntityAssociationProvider>(){

        public StreamsEntityAssociationProvider apply(ActivityProviderModuleDescriptor descriptor) {
            return descriptor.getEntityAssociationProvider();
        }
    };

    public EntityAssociationProviders(PluginAccessor pluginAccessor, @Qualifier(value="sessionManager") SessionManager sessionManager, TransactionTemplate transactionTemplate, UserManager userManager) {
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor, (Object)"pluginAccessor");
        this.sessionManager = (SessionManager)Preconditions.checkNotNull((Object)sessionManager, (Object)"sessionManager");
        this.transactionTemplate = (TransactionTemplate)Preconditions.checkNotNull((Object)transactionTemplate, (Object)"transactionTemplate");
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
    }

    public Iterable<StreamsEntityAssociationProvider> getProviders() {
        return Iterables.filter((Iterable)Iterables.transform((Iterable)this.pluginAccessor.getEnabledModuleDescriptorsByClass(ActivityProviderModuleDescriptor.class), this.toAssociationProvider()), (Predicate)Predicates.notNull());
    }

    public Iterable<EntityIdentifier> getEntityAssociations(URI targetUri) {
        return Iterables.concat((Iterable)Iterables.transform(this.getProviders(), this.toAssociations(targetUri)));
    }

    public Option<URI> getEntityURI(EntityIdentifier target) {
        return Options.find((Iterable)Iterables.transform(this.getProviders(), this.toEntityURI(target)));
    }

    public Option<String> getFilterKey(EntityIdentifier target) {
        return Options.find((Iterable)Iterables.transform(this.getProviders(), this.toFilterKey(target)));
    }

    public boolean getCurrentUserViewPermission(EntityIdentifier target) {
        return (Boolean)Options.find((Iterable)Iterables.transform(this.getProviders(), this.toCurrentUserViewPermission(target))).getOrElse((Object)false);
    }

    public boolean getCurrentUserViewPermissionOfTargetlessEntity() {
        return StreamSupport.stream(this.getProviders().spliterator(), false).map(StreamsEntityAssociationProvider::getCurrentUserViewPermissionForTargetlessEntity).filter(Optional::isPresent).map(Optional::get).reduce((left, right) -> left != false && right != false).orElseGet(() -> this.userManager.getRemoteUser() != null);
    }

    public boolean getCurrentUserEditPermission(EntityIdentifier target) {
        return (Boolean)Options.find((Iterable)Iterables.transform(this.getProviders(), this.toCurrentUserEditPermission(target))).getOrElse((Object)false);
    }

    private Function<ActivityProviderModuleDescriptor, StreamsEntityAssociationProvider> toAssociationProvider() {
        return this.toAssociationProviderFunction;
    }

    private Function<StreamsEntityAssociationProvider, Iterable<EntityIdentifier>> toAssociations(final URI target) {
        return new ProviderSessionScopedFunction<Iterable<EntityIdentifier>>(new Function<StreamsEntityAssociationProvider, Iterable<EntityIdentifier>>(){

            public Iterable<EntityIdentifier> apply(StreamsEntityAssociationProvider provider) {
                return provider.getEntityIdentifiers(target);
            }
        }, new Supplier<Iterable<EntityIdentifier>>(){

            public Iterable<EntityIdentifier> get() {
                return ImmutableList.of();
            }
        });
    }

    private Function<StreamsEntityAssociationProvider, Option<URI>> toEntityURI(final EntityIdentifier identifier) {
        return new ProviderSessionScopedFunction<Option<URI>>(new Function<StreamsEntityAssociationProvider, Option<URI>>(){

            public Option<URI> apply(StreamsEntityAssociationProvider provider) {
                return provider.getEntityURI(identifier);
            }
        }, Options.noneSupplier());
    }

    private Function<StreamsEntityAssociationProvider, Option<String>> toFilterKey(final EntityIdentifier identifier) {
        return new ProviderSessionScopedFunction<Option<String>>(new Function<StreamsEntityAssociationProvider, Option<String>>(){

            public Option<String> apply(StreamsEntityAssociationProvider provider) {
                return provider.getFilterKey(identifier);
            }
        }, Options.noneSupplier());
    }

    private Function<StreamsEntityAssociationProvider, Option<Boolean>> toCurrentUserViewPermission(final EntityIdentifier identifier) {
        return new ProviderSessionScopedFunction<Option<Boolean>>(new Function<StreamsEntityAssociationProvider, Option<Boolean>>(){

            public Option<Boolean> apply(StreamsEntityAssociationProvider provider) {
                return provider.getCurrentUserViewPermission(identifier);
            }
        }, Options.noneSupplier());
    }

    private Function<StreamsEntityAssociationProvider, Option<Boolean>> toCurrentUserEditPermission(final EntityIdentifier identifier) {
        return new ProviderSessionScopedFunction<Option<Boolean>>(new Function<StreamsEntityAssociationProvider, Option<Boolean>>(){

            public Option<Boolean> apply(StreamsEntityAssociationProvider provider) {
                return provider.getCurrentUserEditPermission(identifier);
            }
        }, Options.noneSupplier());
    }

    private class ProviderSessionScopedFunction<T>
    implements Function<StreamsEntityAssociationProvider, T> {
        private final Function<StreamsEntityAssociationProvider, T> getFromProviderFunction;
        private final Supplier<T> defaultSupplier;

        ProviderSessionScopedFunction(Function<StreamsEntityAssociationProvider, T> getFromProviderFunction, Supplier<T> defaultSupplier) {
            this.getFromProviderFunction = getFromProviderFunction;
            this.defaultSupplier = defaultSupplier;
        }

        public T apply(final StreamsEntityAssociationProvider provider) {
            final TransactionCallback fetcher = new TransactionCallback<T>(){

                public T doInTransaction() {
                    try {
                        return ProviderSessionScopedFunction.this.getFromProviderFunction.apply((Object)provider);
                    }
                    catch (Exception e) {
                        EntityAssociationProviders.this.log.error("Error while calling StreamsEntityAssociationProvider method", (Throwable)e);
                        return ProviderSessionScopedFunction.this.defaultSupplier.get();
                    }
                }
            };
            return (T)EntityAssociationProviders.this.sessionManager.withSession(new Supplier<T>(){

                public T get() {
                    return EntityAssociationProviders.this.transactionTemplate.execute(fetcher);
                }
            });
        }
    }
}

