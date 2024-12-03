/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  io.atlassian.fugue.Suppliers
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InheritedContentPermissionManager;
import com.atlassian.confluence.event.events.security.AncestorsUpdateEvent;
import com.atlassian.confluence.event.events.security.ContentPermissionEvent;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.security.ContentPermissionSetCache;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.persistence.dao.ContentPermissionSetDao;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Suppliers;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class CachingInheritedContentPermissionManager
implements InheritedContentPermissionManager {
    private final InheritedContentPermissionManager delegate;
    private final PageDaoInternal pageDao;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final Supplier<PermissionsCache> cacheRef;

    public CachingInheritedContentPermissionManager(InheritedContentPermissionManager delegate, ContentPermissionSetDao contentPermissionSetDao, PageDaoInternal pageDao, TransactionAwareCacheFactory cacheFactory, EventListenerRegistrar eventListenerRegistrar) {
        this(delegate, pageDao, eventListenerRegistrar, Suppliers.memoize(() -> CachingInheritedContentPermissionManager.createCache(cacheFactory, contentPermissionSetDao)));
    }

    @VisibleForTesting
    CachingInheritedContentPermissionManager(InheritedContentPermissionManager delegate, PageDaoInternal pageDao, EventListenerRegistrar eventListenerRegistrar, Supplier<PermissionsCache> cacheRef) {
        this.delegate = delegate;
        this.pageDao = pageDao;
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.cacheRef = cacheRef;
    }

    private static PermissionsCache createCache(TransactionAwareCacheFactory cacheFactory, ContentPermissionSetDao contentPermissionSetDao) {
        return new ContentPermissionSetCache(CoreCache.CONTENT_PERMISSION_SETS_BY_CONTENT_ID.resolve(cacheFactory::getTxCache), id -> Optional.ofNullable(contentPermissionSetDao.getById((long)id)));
    }

    @Override
    public List<ContentPermissionSet> getInheritedContentPermissionSets(ContentEntityObject contentEntityObject) {
        return this.getInheritedContentPermissionSets(contentEntityObject, false);
    }

    @Override
    public List<ContentPermissionSet> getInheritedContentPermissionSetsIncludeEdit(ContentEntityObject contentEntityObject) {
        return this.getInheritedContentPermissionSets(contentEntityObject, true);
    }

    private List<ContentPermissionSet> getInheritedContentPermissionSets(ContentEntityObject contentEntity, boolean includeEditPermissions) {
        ContentId contentId = contentEntity.getContentId() != null ? contentEntity.getContentId() : ContentId.of((long)contentEntity.getId());
        List<ContentPermissionSet> permissionSets = this.cacheRef.get().getOrLoad(contentId, () -> this.delegate.getInheritedContentPermissionSetsIncludeEdit(contentEntity));
        return CachingInheritedContentPermissionManager.filter(permissionSets, includeEditPermissions);
    }

    private static List<ContentPermissionSet> filter(List<ContentPermissionSet> appPermissionSets, boolean includeEditPermissions) {
        return (List)appPermissionSets.stream().filter(permissionSet -> permissionSet != null && (includeEditPermissions || !"Edit".equals(permissionSet.getType()))).collect(ImmutableList.toImmutableList());
    }

    @EventListener
    public void onEvent(ContentPermissionEvent event) {
        this.cacheRef.get().remove(this.getContentIdAndAllDescendents(event.getContent()));
    }

    @EventListener
    public void onEvent(AncestorsUpdateEvent event) {
        this.cacheRef.get().remove(Collections.singleton(ContentId.of((long)event.getPageId())));
    }

    private Collection<ContentId> getContentIdAndAllDescendents(@Nullable ContentEntityObject contentEntity) {
        ImmutableList.Builder builder = ImmutableList.builder();
        if (contentEntity != null) {
            builder.add((Object)ContentId.of((long)contentEntity.getId()));
            if (contentEntity instanceof Page) {
                for (Long descendentId : this.pageDao.getDescendantIds((Page)contentEntity, ContentStatus.CURRENT, ContentStatus.DRAFT)) {
                    builder.add((Object)ContentId.of((long)descendentId));
                }
            }
        }
        return builder.build();
    }

    @PostConstruct
    public void registerEventListener() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unregisterEventListener() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    static interface PermissionsCache {
        public List<ContentPermissionSet> getOrLoad(ContentId var1, Supplier<List<ContentPermissionSet>> var2);

        public void remove(Iterable<ContentId> var1);
    }
}

