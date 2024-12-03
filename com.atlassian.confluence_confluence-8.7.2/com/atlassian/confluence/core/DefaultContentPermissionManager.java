/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResults
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResults;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionUtils;
import com.atlassian.confluence.core.InheritedContentPermissionManager;
import com.atlassian.confluence.core.persistence.ContentPermissionDao;
import com.atlassian.confluence.event.events.internal.security.ContentPermissionAddedEvent;
import com.atlassian.confluence.event.events.internal.security.ContentPermissionRemovedEvent;
import com.atlassian.confluence.event.events.permission.ContentTreePermissionReindexEvent;
import com.atlassian.confluence.event.events.security.ContentPermissionEvent;
import com.atlassian.confluence.impl.backgroundjob.BackgroundJobService;
import com.atlassian.confluence.impl.content.ContentTreePermissionReindexEventBackgroundSender;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.internal.ContentPermissionManagerInternal;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.PageDao;
import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.persistence.dao.ContentPermissionSetDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class DefaultContentPermissionManager
implements ContentPermissionManagerInternal,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultContentPermissionManager.class);
    public static final boolean DISABLE_EFFICIENT_CONTENT_PERMISSIONS_CHANGE = Boolean.getBoolean("confluence.disable-efficient-content-permissions-change");
    private static final int PAGE_SIZE = 1000;
    private PageDao pageDao;
    private ContentPermissionSetDao contentPermissionSetDao;
    protected ContentPermissionDao contentPermissionDao;
    private ConfluenceIndexer indexer;
    private ChangeIndexer changeIndexer;
    private EventPublisher eventPublisher;
    private InheritedContentPermissionManager inheritedContentPermissionManager;
    private AttachmentManager attachmentManager;
    private EventListenerRegistrar eventListenerRegistrar;
    private AuditingContext auditingContext;
    private ScopesRequestCacheDelegate scopesRequestCacheDelegate;
    private BackgroundJobService backgroundJobService;

    private void reindexContentAndDependencies(ContentEntityObject content) {
        if (content == null) {
            return;
        }
        if (content instanceof Page) {
            this.reindexPagesDependencies((Page)content);
        } else if (content instanceof BlogPost) {
            this.reindexContentAndComments(content);
            content.getAttachments().forEach(attachment -> this.reindexContentAndComments((ContentEntityObject)attachment));
        }
    }

    private void reindexPagesDependencies(Page rootPage) {
        if (DISABLE_EFFICIENT_CONTENT_PERMISSIONS_CHANGE || this.backgroundJobService == null) {
            Iterable pages = Iterables.concat(Collections.singleton(rootPage), this.pageDao.getDescendants(rootPage));
            this.getAttachmentManager().getLatestVersionsOfAttachmentsForMultipleCeos(pages).forEach(attachment -> this.reindexContentAndComments((ContentEntityObject)attachment));
            pages.forEach(page -> this.reindexContentAndComments((ContentEntityObject)page));
        } else {
            this.reindexAllDescendantsInBackground(rootPage);
        }
    }

    private void reindexAllDescendantsInBackground(ContentEntityObject contentEntityObject) {
        this.backgroundJobService.addJob(ContentTreePermissionReindexEventBackgroundSender.class, ContentTreePermissionReindexEventBackgroundSender.createParametersForContentEntityObject(contentEntityObject), "Sending ContentTreePermissionReindexEvent for all descendants of content entity object with id " + contentEntityObject.getId() + " and title " + contentEntityObject.getTitle(), Instant.now());
    }

    private void reindexContentAndComments(ContentEntityObject content) {
        this.reindexContent(content);
        content.getComments().forEach(this::reindexContent);
    }

    private void reindexContent(ContentEntityObject content) {
        this.eventPublisher.publish((Object)new ContentTreePermissionReindexEvent((Object)this, content));
        if (this.indexer != null) {
            this.indexer.reIndex(content);
        }
        if (this.changeIndexer != null) {
            this.changeIndexer.reIndexAllVersions(content);
        }
    }

    @EventListener
    public void onContentPermissionEvent(ContentPermissionEvent contentPermissionEvent) {
        if (contentPermissionEvent.isReindexNeeded()) {
            this.reindexContentAndDependencies(contentPermissionEvent.getContent());
        }
    }

    @Override
    public List<ContentPermission> getInheritedContentUserPermissions(ContentEntityObject contentEntityObject) {
        return this.getInheritedContentPermissionSets(contentEntityObject).stream().flatMap(set -> StreamSupport.stream(set.spliterator(), false)).filter(ContentPermission::isUserPermission).collect(Collectors.toList());
    }

    @Override
    public List<ContentPermissionSet> getInheritedContentPermissionSets(ContentEntityObject contentEntityObject) {
        return this.getInheritedContentPermissionSets(contentEntityObject, false);
    }

    @Override
    public List<ContentPermissionSet> getInheritedContentPermissionSets(ContentEntityObject contentEntityObject, boolean includeEditPermissions) {
        List<ContentPermissionSet> inheritedContentPermissionSets = includeEditPermissions ? this.inheritedContentPermissionManager.getInheritedContentPermissionSetsIncludeEdit(this.getLatestVersion(contentEntityObject)) : this.inheritedContentPermissionManager.getInheritedContentPermissionSets(this.getLatestVersion(contentEntityObject));
        return inheritedContentPermissionSets;
    }

    private ContentEntityObject getLatestVersion(ContentEntityObject contentEntityObject) {
        return (ContentEntityObject)contentEntityObject.getLatestVersion();
    }

    @Override
    public List<ContentPermissionSet> getContentPermissionSets(ContentEntityObject ceo, String type) {
        ContentPermissionSet explicitContentPermissionSet;
        if (ceo == null) {
            return Collections.emptyList();
        }
        ArrayList<ContentPermissionSet> result = new ArrayList<ContentPermissionSet>();
        if ("View".equals(type)) {
            result.addAll(this.getInheritedContentPermissionSets(ceo));
        }
        if ((explicitContentPermissionSet = this.getLatestVersion(ceo).getContentPermissionSet(type)) != null) {
            result.add(explicitContentPermissionSet);
        }
        return result;
    }

    @Override
    public boolean hasContentLevelPermission(User user, String permissionType, ContentEntityObject contentEntityObject) {
        ContentEntityObject latestVersion = this.getLatestVersion(contentEntityObject);
        if (!this.hasInheritedContentLevelViewPermissions(user, latestVersion)) {
            return false;
        }
        if (!this.scopesRequestCacheDelegate.hasPermission(permissionType, (Object)contentEntityObject)) {
            return false;
        }
        if ("Edit".equals(permissionType)) {
            return this.hasExplicitContentLevelPermissionIgnoreInherited(user, "Edit", latestVersion) || this.hasContentLevelPermissionIgnoreInherited(user, "View", latestVersion) && this.hasContentLevelPermissionIgnoreInherited(user, "Edit", latestVersion);
        }
        if ("View".equals(permissionType)) {
            return this.hasContentLevelPermissionIgnoreInherited(user, "View", latestVersion) || this.hasExplicitContentLevelPermissionIgnoreInherited(user, "Edit", latestVersion);
        }
        throw new IllegalArgumentException("Got " + permissionType + ", where only View and Edit are valid.");
    }

    private boolean hasContentLevelPermissionIgnoreInherited(User user, String permissionType, ContentEntityObject latestVersion) {
        ContentPermissionSet contentPermissionSet = latestVersion.getContentPermissionSet(permissionType);
        Boolean hasPermission = contentPermissionSet == null || contentPermissionSet.isPermitted(user);
        if (!hasPermission.booleanValue() && permissionType.equals("View")) {
            contentPermissionSet = latestVersion.getContentPermissionSet("Edit");
            hasPermission = contentPermissionSet != null && contentPermissionSet.isPermitted(user);
        }
        return hasPermission;
    }

    private boolean hasContentLevelPermissionIgnoreInherited(User user, String permissionType, List<ContentPermissionSet> contentPermissionSets) {
        ContentPermissionSet contentPermissionSet = this.getContentPermissionSet(permissionType, contentPermissionSets);
        Boolean hasPermission = contentPermissionSet == null || contentPermissionSet.isPermitted(user);
        if (!hasPermission.booleanValue() && permissionType.equals("View")) {
            contentPermissionSet = this.getContentPermissionSet("Edit", contentPermissionSets);
            hasPermission = contentPermissionSet != null && contentPermissionSet.isPermitted(user);
        }
        return hasPermission;
    }

    private ContentPermissionSet getContentPermissionSet(String type, List<ContentPermissionSet> contentPermissionSets) {
        for (ContentPermissionSet permissionSet : contentPermissionSets) {
            if (type == null || !type.equals(permissionSet.getType())) continue;
            return permissionSet;
        }
        return null;
    }

    private boolean hasExplicitContentLevelPermissionIgnoreInherited(User user, String permissionType, ContentEntityObject latestVersion) {
        ContentPermissionSet contentPermissionSet = latestVersion.getContentPermissionSet(permissionType);
        return contentPermissionSet != null && contentPermissionSet.isPermitted(user);
    }

    private boolean hasInheritedContentLevelViewPermissions(User user, ContentEntityObject latestVersion) {
        for (Map.Entry<ContentEntityObject, Map<String, ContentPermissionSet>> entry : ContentPermissionUtils.getPermissionsAsMap(this.getInheritedContentPermissionSets(latestVersion, true)).entrySet()) {
            boolean viewPermitted;
            Map<String, ContentPermissionSet> permissionSetMap = entry.getValue();
            boolean editPermitted = permissionSetMap.containsKey("Edit") && permissionSetMap.get("Edit").isPermitted(user);
            boolean bl = viewPermitted = !permissionSetMap.containsKey("View") || permissionSetMap.get("View").isPermitted(user);
            if (editPermitted || viewPermitted) continue;
            return false;
        }
        return true;
    }

    @Override
    public void removeContentPermission(ContentPermission contentPermission) {
        ContentEntityObject content = this.doRemoveContentPermission(contentPermission);
        this.eventPublisher.publish((Object)new ContentPermissionEvent(this, content, contentPermission));
    }

    @Override
    public void removeAllGroupPermissions(String groupName) {
        List<ContentPermission> permissions = this.contentPermissionDao.getGroupPermissions(groupName);
        this.removeContentPermissions(permissions);
    }

    @Override
    public void removeAllUserPermissions(ConfluenceUser user) {
        List<ContentPermission> permissions = this.contentPermissionDao.getUserPermissions(user);
        this.removeContentPermissions(permissions);
    }

    private void removeContentPermissions(List<ContentPermission> permissions) {
        for (ContentPermission permission : permissions) {
            this.removeContentPermission(permission);
        }
    }

    @Override
    public void addContentPermission(ContentPermission permission, ContentEntityObject content) {
        this.doAddContentPermission(permission, content);
        this.eventPublisher.publish((Object)new ContentPermissionEvent(this, content, permission));
    }

    private void doAddContentPermission(ContentPermission permission, ContentEntityObject content) {
        if (!content.isLatestVersion()) {
            throw new IllegalArgumentException("ContentEntityObject must correspond to the latest version of a content. Permissions can only be added to the latest version of a content.");
        }
        content.addPermission(permission);
        ContentPermissionSet set = permission.getOwningSet();
        this.contentPermissionSetDao.save(set);
        this.contentPermissionDao.save(permission);
        this.eventPublisher.publish((Object)new ContentPermissionAddedEvent(content, permission));
    }

    private ContentEntityObject doRemoveContentPermission(ContentPermission contentPermission) {
        ContentPermissionSet owningSet = contentPermission.getOwningSet();
        owningSet.removeContentPermission(contentPermission);
        this.contentPermissionDao.remove(contentPermission);
        this.eventPublisher.publish((Object)new ContentPermissionRemovedEvent(owningSet.getOwningContent(), contentPermission));
        ContentEntityObject content = owningSet.getOwningContent();
        if (owningSet.isEmpty()) {
            content.removeContentPermissionSet(owningSet);
            this.contentPermissionSetDao.remove(owningSet);
        }
        return content;
    }

    private Collection<ContentPermission> doSetContentPermissions(Collection<ContentPermission> requiredPermissions, ContentEntityObject content, String type) {
        ContentPermissionSet cps = content.getContentPermissionSet(type);
        ArrayList<ContentPermission> affectedPermissions = new ArrayList<ContentPermission>();
        for (ContentPermission contentPermission : requiredPermissions) {
            if (!type.equals(contentPermission.getType())) {
                log.warn("Attempt was made to add contentPermission: " + contentPermission + " as having type: " + type + " instead of type " + contentPermission.getType());
                continue;
            }
            if (cps != null && cps.contains(contentPermission)) continue;
            affectedPermissions.add(contentPermission);
            this.doAddContentPermission(contentPermission, content);
        }
        if (cps != null) {
            Collection<ContentPermission> toRemove = cps.getAllExcept(requiredPermissions);
            for (ContentPermission permission : toRemove) {
                affectedPermissions.add(permission);
                this.doRemoveContentPermission(permission);
            }
        }
        return affectedPermissions;
    }

    @Override
    public void setContentPermissions(@NonNull Map<String, Collection<ContentPermission>> requiredPermissionsMap, ContentEntityObject content) {
        ArrayList<ContentPermission> permissionsToUpdate = new ArrayList<ContentPermission>();
        for (Map.Entry<String, Collection<ContentPermission>> entry : requiredPermissionsMap.entrySet()) {
            String type = entry.getKey();
            Collection<ContentPermission> requiredPermissions = entry.getValue();
            permissionsToUpdate.addAll(this.doSetContentPermissions(requiredPermissions, content, type));
        }
        if (!permissionsToUpdate.isEmpty()) {
            this.reindexContentAndDependencies(content);
            for (ContentPermission permission : permissionsToUpdate) {
                this.eventPublisher.publish((Object)new ContentPermissionEvent(this, content, permission, false));
            }
        }
    }

    @Override
    public void setContentPermissions(Collection<ContentPermission> requiredPermissions, ContentEntityObject content, String type) {
        this.setContentPermissions((Map<String, Collection<ContentPermission>>)ImmutableMap.of((Object)type, requiredPermissions), content);
    }

    @Override
    public List<Page> getPermittedChildren(Page page, User user) {
        List<Page> sortedChildren = page.getSortedChildren();
        if (sortedChildren.isEmpty() || !this.hasInheritedContentLevelViewPermissions(user, sortedChildren.get(0))) {
            return Collections.emptyList();
        }
        ArrayList<Page> permittedChildren = new ArrayList<Page>();
        for (Page child : sortedChildren) {
            if (!this.hasContentLevelPermissionIgnoreInherited(user, "View", child.getLatestVersion())) continue;
            permittedChildren.add(child);
        }
        return permittedChildren;
    }

    @Override
    public List<Page> getPermittedChildrenIgnoreInheritedPermissions(Page page, User user) {
        List<Page> sortedChildren = page.getSortedChildren();
        ArrayList<Page> permittedChildren = new ArrayList<Page>();
        for (Page child : sortedChildren) {
            if (!this.hasContentLevelPermissionIgnoreInherited(user, "View", child.getLatestVersion())) continue;
            permittedChildren.add(child);
        }
        return permittedChildren;
    }

    @Override
    public boolean hasPermittedChildrenIgnoreInheritedPermissions(Page page, User user) {
        List<Page> sortedChildren = page.getSortedChildren();
        for (Page child : sortedChildren) {
            if (!this.hasContentLevelPermissionIgnoreInherited(user, "View", child.getLatestVersion())) continue;
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public Set<ContentPermission> getViewContentPermissions(Page page) {
        ContentEntityObject latestVersion = this.getLatestVersion(page);
        Set<ContentPermission> viewContentPermissions = this.getInheritedContentPermissionSets(page).stream().flatMap(set -> StreamSupport.stream(set.spliterator(), false)).collect(Collectors.toSet());
        ContentPermission viewPermission = latestVersion.getContentPermission("View");
        if (viewPermission != null) {
            viewContentPermissions.add(viewPermission);
        }
        return viewContentPermissions;
    }

    public void setAuditingContext(AuditingContext auditingContext) {
        this.auditingContext = auditingContext;
    }

    public void setContentPermissionDao(ContentPermissionDao contentPermissionDao) {
        this.contentPermissionDao = contentPermissionDao;
    }

    @Deprecated(forRemoval=true)
    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setPageDao(PageDaoInternal pageDao) {
        this.pageDao = pageDao;
    }

    public void setContentPermissionSetDao(ContentPermissionSetDao contentPermissionSetDao) {
        this.contentPermissionSetDao = contentPermissionSetDao;
    }

    @Deprecated
    public void setIndexer(ConfluenceIndexer indexer) {
        this.indexer = indexer;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setInheritedContentPermissionManager(InheritedContentPermissionManager inheritedContentPermissionManager) {
        this.inheritedContentPermissionManager = inheritedContentPermissionManager;
    }

    public void setScopesRequestCacheDelegate(ScopesRequestCacheDelegate scopesRequestCacheDelegate) {
        this.scopesRequestCacheDelegate = scopesRequestCacheDelegate;
    }

    @Deprecated
    public void setChangeIndexer(ChangeIndexer changeIndexer) {
        this.changeIndexer = changeIndexer;
    }

    public void setEventListenerRegistrar(EventListenerRegistrar eventListenerRegistrar) {
        this.eventListenerRegistrar = eventListenerRegistrar;
        eventListenerRegistrar.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    public AttachmentManager getAttachmentManager() {
        if (this.attachmentManager == null) {
            this.attachmentManager = (AttachmentManager)ContainerManager.getComponent((String)"attachmentManager");
        }
        return this.attachmentManager;
    }

    @Override
    public boolean isPermissionInherited(Page childPage) {
        List<ContentPermissionSet> parentPermissionSetList = this.getContentPermissionSets(childPage.getParent(), "View");
        ContentPermissionSet childPermissionSet = childPage.getContentPermissionSet("View");
        for (ContentPermissionSet permissionSet : parentPermissionSetList) {
            if (childPermissionSet != null && childPermissionSet.containsAll(permissionSet)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void copyContentPermissions(AbstractPage from, AbstractPage to) {
        this.copyContentPermissions((ContentEntityObject)from, (ContentEntityObject)to);
    }

    @Override
    public void copyContentPermissions(ContentEntityObject from, ContentEntityObject to) {
        this.auditingContext.executeWithoutAuditing(() -> {
            ContentPermissionSet permissionSet = from.getContentPermissionSet("View");
            this.copyContentPermissionSet(permissionSet, to);
            permissionSet = from.getContentPermissionSet("Edit");
            this.copyContentPermissionSet(permissionSet, to);
        });
    }

    @Override
    public Map<Long, Boolean> getPermissionSets(User user, Space space) {
        HashMap<Long, Boolean> result = new HashMap<Long, Boolean>();
        List<Long> ids = this.contentPermissionSetDao.getContentIdsWithPermissionSet(space.getKey());
        if (ids.isEmpty()) {
            return result;
        }
        IntStream.range(0, (ids.size() - 1) / 1000 + 1).mapToObj(batchId -> ids.subList(batchId * 1000, Math.min((batchId + 1) * 1000, ids.size()))).forEach(contentIds -> this.contentPermissionSetDao.getPermissionSets(space.getKey(), (List<Long>)contentIds).forEach((contentId, permissionSets) -> {
            ContentPermissionSet view = this.findPermissionSet((Set<ContentPermissionSet>)permissionSets, "View");
            ContentPermissionSet edit = this.findPermissionSet((Set<ContentPermissionSet>)permissionSets, "Edit");
            result.put((Long)contentId, this.hasViewPermission(user, view, edit));
        }));
        return result;
    }

    private ContentPermissionSet findPermissionSet(Set<ContentPermissionSet> permissionSets, String permissionType) {
        return permissionSets.stream().filter(permissions -> permissionType.equalsIgnoreCase(permissions.getType())).findAny().orElse(null);
    }

    private boolean hasViewPermission(User user, ContentPermissionSet view, ContentPermissionSet edit) {
        return this.hasViewPermission(user, view) || edit != null && edit.isPermitted(user);
    }

    private boolean hasViewPermission(User user, ContentPermissionSet permission) {
        if (permission == null) {
            return true;
        }
        return permission.isPermitted(user);
    }

    private void copyContentPermissionSet(ContentPermissionSet permissionSet, ContentEntityObject target) {
        if (permissionSet == null) {
            return;
        }
        for (ContentPermission originalPermission : permissionSet) {
            ContentPermission newPermission = originalPermission.isUserPermission() ? ContentPermission.createUserPermission(originalPermission.getType(), originalPermission.getUserSubject()) : ContentPermission.createGroupPermission(originalPermission.getType(), originalPermission.getGroupName());
            this.addContentPermission(newPermission, target);
        }
    }

    @Override
    public Map<Long, ValidationResult> hasContentLevelPermission(ConfluenceUser user, String permissionType, Collection<Long> contentIds) {
        Preconditions.checkNotNull((Object)user);
        Preconditions.checkNotNull((Object)permissionType);
        Preconditions.checkNotNull(contentIds);
        HashSet<Long> setContentId = new HashSet<Long>(contentIds);
        HashMap validationResultMap = Maps.newHashMap();
        Map<Long, List<Long>> ancestorMap = this.pageDao.getAncestorsFor(contentIds);
        Map<Long, List<ContentPermissionSet>> permissionSetMap = this.contentPermissionSetDao.getExplicitPermissionSetsFor(contentIds);
        for (Map.Entry<Long, List<Long>> entry : ancestorMap.entrySet()) {
            List<Long> ancestorList = entry.getValue();
            List validAncestorList = ancestorList.stream().filter(setContentId::contains).collect(Collectors.toList());
            ancestorMap.put(entry.getKey(), validAncestorList);
        }
        LinkedHashMap<Long, List<Long>> sortedAncestorMap = new LinkedHashMap<Long, List<Long>>();
        Stream ancestorMapStream = ancestorMap.entrySet().stream();
        ancestorMapStream.sorted((c1, c2) -> Integer.compare(((List)c1.getValue()).size(), ((List)c2.getValue()).size())).forEachOrdered(e -> sortedAncestorMap.put((Long)e.getKey(), (List)e.getValue()));
        if (this.isSingleHierarchy(sortedAncestorMap)) {
            log.debug("Checking permission using new method");
            long rootNodeId = Objects.requireNonNull((Long)Iterables.getFirst(sortedAncestorMap.keySet(), (Object)-1L));
            Page rootPage = this.pageDao.getPageById(rootNodeId);
            if (rootPage == null) {
                ancestorMap.keySet().forEach(targetId -> validationResultMap.put(targetId, SimpleValidationResult.FORBIDDEN));
            } else {
                boolean canViewRootPage = this.hasContentLevelPermission((User)user, "View", rootPage);
                validationResultMap.putAll(this.hasContentLevelPermissionForHierarchy(user, permissionType, canViewRootPage, permissionSetMap, sortedAncestorMap));
            }
        } else {
            log.debug("Checking permission using old method");
            List<Page> pageList = this.pageDao.getPagesByIds(contentIds);
            for (Page page : pageList) {
                validationResultMap.put(page.getId(), this.hasContentLevelPermission((User)user, permissionType, page) ? SimpleValidationResult.VALID : SimpleValidationResult.FORBIDDEN);
            }
        }
        contentIds.stream().filter(id -> validationResultMap.get(id) == null).forEach(id -> validationResultMap.put(id, SimpleValidationResults.notFoundResult((String)"Page does not exist", (Object[])new Object[0])));
        return validationResultMap;
    }

    private boolean isSingleHierarchy(Map<Long, List<Long>> ancestorMap) {
        Set<Map.Entry<Long, List<Long>>> ancestorSet = ancestorMap.entrySet();
        List ancestorList = ancestorSet.stream().filter(entry -> ((List)entry.getValue()).size() == 0).collect(Collectors.toList());
        for (Map.Entry rootPageEntry : ancestorList) {
            Long rootPageKey = (Long)rootPageEntry.getKey();
            if (((List)rootPageEntry.getValue()).size() > 0) {
                return false;
            }
            for (Map.Entry<Long, List<Long>> childPageEntry : ancestorSet) {
                if (childPageEntry.getKey().equals(rootPageKey) || childPageEntry.getValue().contains(rootPageKey)) continue;
                return false;
            }
        }
        return true;
    }

    private Map<Long, ValidationResult> hasContentLevelPermissionForHierarchy(User user, String permissionType, boolean canViewRootPage, Map<Long, List<ContentPermissionSet>> permissionSetMap, Map<Long, List<Long>> ascendantMap) {
        HashMap validationResultMap = Maps.newHashMap();
        HashSet restrictedSet = Sets.newHashSet();
        for (Map.Entry<Long, List<Long>> entry : ascendantMap.entrySet()) {
            Long checkingId = entry.getKey();
            HashSet checkAncestorSet = new HashSet(entry.getValue());
            if (!canViewRootPage) {
                validationResultMap.put(checkingId, SimpleValidationResult.FORBIDDEN);
                continue;
            }
            if ("View".equals(permissionType) && !Collections.disjoint(restrictedSet, checkAncestorSet)) {
                validationResultMap.put(checkingId, SimpleValidationResult.FORBIDDEN);
                restrictedSet.add(checkingId);
                continue;
            }
            boolean isValid = this.hasContentLevelPermissionIgnoreInherited(user, permissionType, permissionSetMap.get(checkingId));
            if (!isValid) {
                validationResultMap.put(checkingId, SimpleValidationResult.FORBIDDEN);
                restrictedSet.add(checkingId);
                continue;
            }
            validationResultMap.put(checkingId, SimpleValidationResult.VALID);
        }
        return validationResultMap;
    }

    @Override
    public List<Page> getPermittedPagesIgnoreInheritedPermissions(List<Page> contentList, ConfluenceUser user, String permission) {
        return contentList.stream().filter(page -> {
            ContentPermissionSet permissionSet = page.getContentPermissionSet(permission);
            return permissionSet == null || permissionSet.isPermitted(user);
        }).collect(Collectors.toList());
    }

    @Override
    public boolean hasVisibleChildren(Page page, ConfluenceUser user) {
        if (page == null || page.getChildren() == null) {
            return false;
        }
        for (Page child : page.getChildren()) {
            ContentPermissionSet viewContentPermissionSet = child.getContentPermissionSet("View");
            if (viewContentPermissionSet != null && !viewContentPermissionSet.isPermitted(user)) continue;
            return true;
        }
        return false;
    }

    public void setBackgroundJobService(BackgroundJobService backgroundJobService) {
        this.backgroundJobService = backgroundJobService;
    }
}

