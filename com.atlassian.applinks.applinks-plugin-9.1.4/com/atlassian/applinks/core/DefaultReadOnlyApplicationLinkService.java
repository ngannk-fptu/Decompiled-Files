/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkMadePrimaryEvent
 *  com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.Supplier
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkMadePrimaryEvent;
import com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent;
import com.atlassian.applinks.core.ImmutableApplicationLink;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestFactoryFactory;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.Supplier;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultReadOnlyApplicationLinkService
implements ReadOnlyApplicationLinkService,
InitializingBean,
DisposableBean {
    private static final int DEFAULT_CACHE_EXPIRY_IN_SECONDS = Integer.getInteger("applinks.cache.expiry", 300);
    private static final Predicate<ReadOnlyApplicationLink> IS_PRIMARY = new Predicate<ReadOnlyApplicationLink>(){

        public boolean apply(ReadOnlyApplicationLink input) {
            return input.isPrimary();
        }
    };
    private static final boolean PRIMARY = true;
    private static final boolean NON_PRIMARY = false;
    private static final Ordering<ReadOnlyApplicationLink> SORT_BY_PRIMARY = Ordering.explicit((Object)true, (Object[])new Boolean[]{false}).onResultOf(Functions.forPredicate(IS_PRIMARY));
    private final ApplicationLinkService applicationLinkService;
    private final ApplicationLinkRequestFactoryFactory requestFactoryFactory;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;
    private final CachedReference<Iterable<ReadOnlyApplicationLink>> links;

    @Autowired
    public DefaultReadOnlyApplicationLinkService(ApplicationLinkService applicationLinkService, ApplicationLinkRequestFactoryFactory requestFactoryFactory, TransactionTemplate transactionTemplate, EventPublisher eventPublisher, CacheFactory cacheFactory) throws InvocationTargetException, IllegalAccessException {
        this.applicationLinkService = applicationLinkService;
        this.requestFactoryFactory = requestFactoryFactory;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
        this.links = cacheFactory.getCachedReference(DefaultReadOnlyApplicationLinkService.class.getName() + ".links", (Supplier)new LinkSupplier(), DefaultReadOnlyApplicationLinkService.newCacheSettings());
    }

    private static CacheSettings newCacheSettings() throws InvocationTargetException, IllegalAccessException {
        return new CacheSettingsBuilder().replicateViaInvalidation().expireAfterWrite((long)DEFAULT_CACHE_EXPIRY_IN_SECONDS, TimeUnit.SECONDS).build();
    }

    public Iterable<ReadOnlyApplicationLink> getApplicationLinks() {
        return (Iterable)this.links.get();
    }

    @Nullable
    public ReadOnlyApplicationLink getApplicationLink(ApplicationId applicationId) {
        Objects.requireNonNull(applicationId, "applicationId");
        return (ReadOnlyApplicationLink)Iterables.getOnlyElement((Iterable)Iterables.filter(this.getApplicationLinks(), DefaultReadOnlyApplicationLinkService.hasApplicationId(applicationId)), null);
    }

    public Iterable<ReadOnlyApplicationLink> getApplicationLinks(Class<? extends ApplicationType> type) {
        Objects.requireNonNull(type, "type");
        return SORT_BY_PRIMARY.immutableSortedCopy(Iterables.filter((Iterable)((Iterable)this.links.get()), DefaultReadOnlyApplicationLinkService.hasApplicationType(type)));
    }

    @Nullable
    public ReadOnlyApplicationLink getPrimaryApplicationLink(Class<? extends ApplicationType> type) {
        Objects.requireNonNull(type, "type");
        Iterable filteredByApplicationType = Iterables.filter((Iterable)((Iterable)this.links.get()), DefaultReadOnlyApplicationLinkService.hasApplicationType(type));
        if (Iterables.isEmpty((Iterable)filteredByApplicationType)) {
            return null;
        }
        ReadOnlyApplicationLink result = (ReadOnlyApplicationLink)Iterables.getOnlyElement((Iterable)Iterables.filter((Iterable)filteredByApplicationType, IS_PRIMARY), null);
        if (result == null) {
            throw new IllegalStateException("There are application links of type " + type + " configured, but none are marked as primary");
        }
        return result;
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onApplicationLinkAddedEvent(ApplicationLinkAddedEvent event) {
        this.links.reset();
    }

    @EventListener
    public void onApplicationLinkIdChangedEvent(ApplicationLinksIDChangedEvent event) {
        this.links.reset();
    }

    @EventListener
    public void onApplicationLinkDetailsChangedEvent(ApplicationLinkDetailsChangedEvent event) {
        this.links.reset();
    }

    @EventListener
    public void onApplicationLinkMadePrimaryEvent(ApplicationLinkMadePrimaryEvent event) {
        this.links.reset();
    }

    @EventListener
    public void onApplicationLinkDeletedEvent(ApplicationLinkDeletedEvent event) {
        this.links.reset();
    }

    private Function<ApplicationLink, ReadOnlyApplicationLink> toImmutableApplicationLink() {
        return new Function<ApplicationLink, ReadOnlyApplicationLink>(){

            public ReadOnlyApplicationLink apply(ApplicationLink from) {
                return new ImmutableApplicationLink(from, DefaultReadOnlyApplicationLinkService.this.requestFactoryFactory);
            }
        };
    }

    private static Predicate<ReadOnlyApplicationLink> hasApplicationId(final ApplicationId applicationId) {
        return new Predicate<ReadOnlyApplicationLink>(){

            public boolean apply(ReadOnlyApplicationLink input) {
                return applicationId.equals((Object)input.getId());
            }
        };
    }

    private static Predicate<ReadOnlyApplicationLink> hasApplicationType(final Class<? extends ApplicationType> type) {
        return new Predicate<ReadOnlyApplicationLink>(){

            public boolean apply(ReadOnlyApplicationLink input) {
                return type.isAssignableFrom(input.getType().getClass());
            }
        };
    }

    private class LinkSupplier
    implements Supplier<Iterable<ReadOnlyApplicationLink>> {
        private LinkSupplier() {
        }

        public Iterable<ReadOnlyApplicationLink> get() {
            return (Iterable)DefaultReadOnlyApplicationLinkService.this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Iterable<ReadOnlyApplicationLink>>(){

                public Iterable<ReadOnlyApplicationLink> doInTransaction() {
                    Iterable applicationLinks = DefaultReadOnlyApplicationLinkService.this.applicationLinkService.getApplicationLinks();
                    return ImmutableList.copyOf((Iterable)Iterables.transform((Iterable)applicationLinks, (Function)DefaultReadOnlyApplicationLinkService.this.toImmutableApplicationLink()));
                }
            });
        }
    }
}

