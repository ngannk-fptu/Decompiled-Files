/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaPersister
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.impl.hibernate.HibernateSessionManager5
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  io.atlassian.fugue.Either
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.StopWatch
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.NoTransactionException
 *  org.springframework.transaction.interceptor.TransactionAspectSupport
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationAdapter
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.confluence.spaces;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.DefaultListBuilder;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.core.ListBuilderCallback;
import com.atlassian.confluence.event.events.space.SpaceArchivedEvent;
import com.atlassian.confluence.event.events.space.SpaceContentWillRemoveEvent;
import com.atlassian.confluence.event.events.space.SpaceCreateEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.event.events.space.SpaceUnArchivedEvent;
import com.atlassian.confluence.event.events.space.SpaceUpdateEvent;
import com.atlassian.confluence.event.events.space.SpaceWillRemoveEvent;
import com.atlassian.confluence.impl.hibernate.HibernateSessionManager5;
import com.atlassian.confluence.impl.search.IndexerEventPublisher;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryManager;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.security.SpacePermissionContext;
import com.atlassian.confluence.internal.security.SpacePermissionManagerInternal;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.internal.spaces.SpacesQueryWithPermissionQueryBuilder;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.search.ThreadLocalIndexerControl;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.DenormalisedPermissionDarkFeature;
import com.atlassian.confluence.security.denormalisedpermissions.impl.analytics.PermittedSpacesAnalyticsEvent;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceDescriptionManager;
import com.atlassian.confluence.spaces.SpaceGroup;
import com.atlassian.confluence.spaces.SpaceLogo;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpaceUpdateTrigger;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.spaces.persistence.dao.SpaceDao;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.atlassian.fugue.Either;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ParametersAreNonnullByDefault
public class DefaultSpaceManager
implements SpaceManagerInternal {
    private static final Logger log = LoggerFactory.getLogger(DefaultSpaceManager.class);
    @VisibleForTesting
    static final String LOCK_PREFIX = "default-space-manager-cluster-lock";
    private static final ListBuilder<Space> EMPTY_SPACE_LIST_BUILDER_RESULT = DefaultListBuilder.newInstance(new ListBuilderCallback<Space>(){

        @Override
        public List<Space> getElements(int offset, int maxResults) {
            return Collections.emptyList();
        }

        @Override
        public int getAvailableSize() {
            return 0;
        }
    });
    private SpaceDescriptionManager spaceDescriptionManager;
    private SpacePermissionManagerInternal spacePermissionManager;
    private SpacePermissionQueryManager spacePermissionQueryManager;
    private AttachmentManager attachmentManager;
    private SpaceDao spaceDao;
    private SettingsManager settingsManager;
    private UserAccessor userAccessor;
    private GroupManager groupManager;
    private PermissionCheckExemptions permissionCheckExemptions;
    private ConfluenceIndexer indexer;
    private EventPublisher eventPublisher;
    private NotificationManager notificationManager;
    private LabelManager labelManager;
    private I18NBeanFactory i18NBeanFactory;
    private AccessModeManager accessModeManager;
    private BulkPermissionService bulkPermissionService;
    private DenormalisedPermissionDarkFeature denormalisedPermissionDarkFeature;
    private HibernateSessionManager5 hibernateSessionManager;
    private PageManager pageManager;
    private PageTemplateManager pageTemplateManager;
    private BandanaPersister bandanaPersister;
    private ClusterLockService clusterLockService;

    public ClusterLockService getClusterLockService() {
        if (this.clusterLockService == null) {
            this.clusterLockService = (ClusterLockService)ContainerManager.getComponent((String)"clusterManager");
        }
        return this.clusterLockService;
    }

    public void setClusterLockService(ClusterLockService clusterLockService) {
        this.clusterLockService = clusterLockService;
    }

    public void setHibernateSessionManager(HibernateSessionManager5 hibernateSessionManager) {
        this.hibernateSessionManager = hibernateSessionManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setPageTemplateManager(PageTemplateManager pageTemplateManager) {
        this.pageTemplateManager = pageTemplateManager;
    }

    public void setBandanaPersister(BandanaPersister bandanaPersister) {
        this.bandanaPersister = bandanaPersister;
    }

    public void setSpaceDao(SpaceDao spaceDao) {
        this.spaceDao = spaceDao;
    }

    public void setSpaceDescriptionManager(SpaceDescriptionManager spaceDescriptionManager) {
        this.spaceDescriptionManager = spaceDescriptionManager;
    }

    public void setSpacePermissionManager(SpacePermissionManagerInternal spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public void setSpacePermissionQueryManager(SpacePermissionQueryManager spacePermissionQueryManager) {
        this.spacePermissionQueryManager = spacePermissionQueryManager;
    }

    @Override
    public Space getSpace(long id) {
        return this.spaceDao.getById(id);
    }

    @Override
    public @Nullable Space getSpace(@Nullable String spaceKey) {
        return this.spaceDao.getSpace(spaceKey);
    }

    @Deprecated
    public void setIndexer(ConfluenceIndexer indexer) {
        this.indexer = indexer;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setGroupManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public void setPermissionCheckExemptions(PermissionCheckExemptions permissionCheckExemptions) {
        this.permissionCheckExemptions = permissionCheckExemptions;
    }

    @Override
    public @NonNull Boolean removeSpace(Space space) {
        return this.removeSpace(space, new ProgressMeter());
    }

    @Override
    public @NonNull Boolean removeSpace(String spaceKey, ProgressMeter progressMeter) {
        try {
            Space space = this.getSpace(spaceKey);
            if (space == null) {
                this.failProgress(progressMeter, this.i18NBeanFactory.getI18NBean());
                return false;
            }
            return this.removeSpace(space, progressMeter);
        }
        catch (RuntimeException e) {
            log.error("Cannot remove space: {}", (Object)spaceKey, (Object)e);
            this.failProgress(progressMeter, this.i18NBeanFactory.getI18NBean());
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean removeSpace(Space space, ProgressMeter progressMeter) {
        String spaceKey = space.getKey();
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        ClusterLock clusterLock = this.getClusterLockService().getLockForName("default-space-manager-cluster-lock:" + spaceKey);
        try {
            Space zombieSpace;
            block16: {
                if (clusterLock == null || !clusterLock.tryLock()) {
                    log.warn("Failed to acquire a cluster lock for removing space {}. Another removal task for this space may be in progress", (Object)spaceKey);
                    boolean bl = false;
                    return bl;
                }
                zombieSpace = this.cloneSpaceForRemoveEvent(space);
                ThreadLocalIndexerControl.getInstance().suspend();
                this.hibernateSessionManager.withNewTransaction(() -> {
                    this.eventPublisher.publish((Object)new SpaceContentWillRemoveEvent(this, this.getSpace(spaceKey), progressMeter));
                    return true;
                });
                boolean continued = this.hibernateSessionManager.withNewTransaction(() -> {
                    progressMeter.setStatus(i18NBean.getText("progress.remove.space.start"));
                    Space spaceToBeRemoved = this.getSpace(spaceKey);
                    if (spaceToBeRemoved == null) {
                        return false;
                    }
                    progressMeter.setStatus(i18NBean.getText("progress.remove.space.permissions"));
                    this.spacePermissionManager.removeAllPermissions(spaceToBeRemoved, SpacePermissionContext.builder().updateTrigger(SpaceUpdateTrigger.SPACE_REMOVED).build());
                    progressMeter.setPercentage(1);
                    progressMeter.setStatus(i18NBean.getText("progress.remove.space.notifications"));
                    this.notificationManager.removeAllNotificationsForSpace(spaceToBeRemoved);
                    progressMeter.setPercentage(2);
                    this.eventPublisher.publish((Object)new SpaceWillRemoveEvent(this, spaceToBeRemoved, progressMeter));
                    return this.removeBundledSpaceContent(spaceKey, progressMeter);
                });
                if (!continued) {
                    this.failProgress(progressMeter, i18NBean);
                    boolean bl = false;
                    return bl;
                }
                continued = this.hibernateSessionManager.withNewTransaction(() -> {
                    Space spaceToBeRemoved = this.spaceDao.getSpace(spaceKey);
                    if (spaceToBeRemoved == null) {
                        return false;
                    }
                    progressMeter.setStatus(i18NBean.getText("progress.remove.space.space"));
                    this.spaceDao.remove(spaceToBeRemoved);
                    progressMeter.setPercentage(progressMeter.getPercentageComplete() + 1);
                    return true;
                });
                if (continued) break block16;
                this.failProgress(progressMeter, i18NBean);
                boolean bl = false;
                return bl;
                finally {
                    ThreadLocalIndexerControl.getInstance().resume();
                }
            }
            progressMeter.setStatus(i18NBean.getText("progress.remove.space.index"));
            this.withIndexer(indexer -> indexer.unIndexSpace(space));
            progressMeter.setPercentage(99);
            this.eventPublisher.publish((Object)new SpaceRemoveEvent(this, zombieSpace, progressMeter));
            this.completeProgressOnTransactionComplete(progressMeter, i18NBean);
        }
        catch (RuntimeException thrown) {
            if (!DefaultSpaceManager.isNewTransaction()) {
                this.failProgress(progressMeter, i18NBean);
            }
            throw thrown;
        }
        finally {
            if (clusterLock != null) {
                clusterLock.unlock();
            }
        }
        return true;
    }

    private void withIndexer(Consumer<ConfluenceIndexer> task) {
        if (this.indexer != null) {
            task.accept(this.indexer);
        } else {
            new IndexerEventPublisher(this.eventPublisher).publishCallbackEvent(task);
        }
    }

    private void completeProgressOnTransactionComplete(final ProgressMeter progressMeter, final I18NBean i18NBean) {
        if (DefaultSpaceManager.isNewTransaction()) {
            TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronizationAdapter(){

                public void afterCompletion(int status) {
                    if (status == 1) {
                        DefaultSpaceManager.this.failProgress(progressMeter, i18NBean);
                    } else {
                        DefaultSpaceManager.this.completeProgress(progressMeter, i18NBean);
                    }
                }
            });
        } else {
            this.completeProgress(progressMeter, i18NBean);
        }
    }

    private static boolean isNewTransaction() {
        try {
            return TransactionAspectSupport.currentTransactionStatus().isNewTransaction();
        }
        catch (NoTransactionException ex) {
            return false;
        }
    }

    private void completeProgress(ProgressMeter progressMeter, I18NBean i18NBean) {
        progressMeter.setStatus(i18NBean.getText("progress.remove.space.finished"));
        progressMeter.setCompletedSuccessfully(true);
        progressMeter.setPercentage(100);
    }

    private void failProgress(ProgressMeter progressMeter, I18NBean i18NBean) {
        progressMeter.setStatus(i18NBean.getText("progress.remove.space.failed"));
        progressMeter.setCompletedSuccessfully(false);
    }

    private Space cloneSpaceForRemoveEvent(Space space) {
        Space zombieSpace = new Space(space.getKey());
        zombieSpace.setName(space.getName());
        zombieSpace.setSpaceStatus(space.getSpaceStatus());
        zombieSpace.setSpaceType(space.getSpaceType());
        zombieSpace.setCreator(space.getCreator());
        zombieSpace.setLastModifier(space.getLastModifier());
        zombieSpace.setCreationDate(space.getCreationDate());
        zombieSpace.setLastModificationDate(space.getLastModificationDate());
        zombieSpace.setId(space.getId());
        SpaceDescription zombieSpaceDesc = new SpaceDescription(zombieSpace);
        SpaceDescription spaceDesc = space.getDescription();
        if (spaceDesc != null) {
            zombieSpaceDesc.setBodyContents(spaceDesc.getBodyContents());
            zombieSpaceDesc.setContentStatus(spaceDesc.getContentStatus());
            zombieSpaceDesc.setCreator(spaceDesc.getCreator());
            zombieSpaceDesc.setLastModifier(spaceDesc.getLastModifier());
            zombieSpaceDesc.setTitle(spaceDesc.getTitle());
            zombieSpaceDesc.setVersion(spaceDesc.getVersion());
            zombieSpaceDesc.setVersionComment(spaceDesc.getVersionComment());
            zombieSpaceDesc.setCreationDate(spaceDesc.getCreationDate());
            zombieSpaceDesc.setLastModificationDate(spaceDesc.getLastModificationDate());
            zombieSpaceDesc.setId(spaceDesc.getId());
        }
        zombieSpace.setDescription(zombieSpaceDesc);
        return zombieSpace;
    }

    @Override
    public void removeSpacesInGroup(SpaceGroup spaceGroup) {
        List<Space> spaces = this.getAllSpaces(SpacesQuery.newQuery().inSpaceGroup(spaceGroup).build());
        for (Space space : spaces) {
            this.removeSpace(space);
        }
    }

    @Override
    public void saveSpace(Space space) {
        this.saveSpaceNoEvent(space);
        SpaceUpdateEvent updateEvent = new SpaceUpdateEvent(this, space);
        this.eventPublisher.publish((Object)updateEvent);
    }

    @Override
    public void saveSpace(Space space, Space originalSpace) {
        this.spaceDescriptionManager.saveContentEntity(space.getDescription(), originalSpace.getDescription(), null);
        this.spaceDao.save(space);
        SpaceUpdateEvent updateEvent = new SpaceUpdateEvent(this, space, originalSpace);
        this.eventPublisher.publish((Object)updateEvent);
        this.emitArchivingEventsIfNeeded(space, originalSpace);
    }

    private void emitArchivingEventsIfNeeded(Space newSpace, Space oldSpace) {
        SpaceStatus oldStatus = oldSpace.getSpaceStatus();
        SpaceStatus newStatus = newSpace.getSpaceStatus();
        if (oldStatus == SpaceStatus.CURRENT && newStatus == SpaceStatus.ARCHIVED) {
            this.eventPublisher.publish((Object)new SpaceArchivedEvent(this, newSpace));
        } else if (oldStatus == SpaceStatus.ARCHIVED && newStatus == SpaceStatus.CURRENT) {
            this.eventPublisher.publish((Object)new SpaceUnArchivedEvent(this, newSpace));
        }
    }

    @Override
    public @NonNull List<Space> getAllSpaces() {
        return this.spaceDao.findAllSorted("name");
    }

    @Override
    public @NonNull List getAuthoredSpacesByUser(String username) {
        return this.spaceDao.getSpacesCreatedByUser(username);
    }

    @Override
    public @NonNull List getSpacesContainingPagesEditedBy(String username) {
        return this.spaceDao.getSpacesContainingPagesEditedByUser(username);
    }

    @Override
    public @NonNull List getSpacesContainingCommentsBy(String username) {
        return this.spaceDao.getSpacesContainingCommentsByUser(username);
    }

    @Override
    public @NonNull Space createSpace(String key, String name, @Nullable String description, User creator) {
        return this.createSpace(key, name, description, FindUserHelper.getUser(creator), false);
    }

    @Override
    public @NonNull Space createPrivateSpace(String key, String name, @Nullable String description, User creator) {
        return this.createSpace(key, name, description, FindUserHelper.getUser(creator), true);
    }

    private @NonNull Space createSpace(String key, String name, @Nullable String description, ConfluenceUser creator, boolean isPrivate) {
        Space space = this.setupSpace(key, name, description);
        space.setCreator(creator);
        if (!Space.isValidGlobalSpaceKey(space.getKey())) {
            throw new IllegalArgumentException("Invalid space key.");
        }
        if (StringUtils.isBlank((CharSequence)space.getName())) {
            throw new IllegalArgumentException("Name is blank. Cannot create space with no name.");
        }
        if (space.getCreator() == null) {
            throw new IllegalArgumentException("Cannot create spaces anonymously. You must specify a valid user for creator.");
        }
        space.setName(space.getName().trim());
        if (space.getSpaceType() == null) {
            space.setSpaceType(SpaceType.GLOBAL);
        }
        this.saveSpaceNoEvent(space);
        if (isPrivate) {
            this.spacePermissionManager.createPrivateSpacePermissions(space);
        } else {
            this.spacePermissionManager.createDefaultSpacePermissions(space);
        }
        SpaceCreateEvent spaceCreateEvent = new SpaceCreateEvent(this, space);
        this.eventPublisher.publish((Object)spaceCreateEvent);
        return space;
    }

    @Override
    public @NonNull Space createPersonalSpace(String name, @Nullable String description, User owner) {
        return this.createPersonalSpace(name, description, FindUserHelper.getUser(owner), false);
    }

    @Override
    public @NonNull Space createPrivatePersonalSpace(String name, @Nullable String description, User owner) {
        return this.createPersonalSpace(name, description, FindUserHelper.getUser(owner), true);
    }

    private @NonNull Space createPersonalSpace(String name, @Nullable String description, ConfluenceUser owner, boolean isPrivate) {
        DefaultSpaceManager.checkArgumentNotNull(owner, "Personal spaces can not be created for Anonymous users");
        Object key = "~" + owner.getName();
        if (!Space.isValidPersonalSpaceKey((String)key)) {
            throw new IllegalArgumentException("Invalid space key.");
        }
        key = this.spaceDao.findUniqueVersionOfSpaceKey((String)key);
        Space space = this.setupSpace((String)key, name, description);
        space.setSpaceType(SpaceType.PERSONAL);
        space.setCreationDate(Calendar.getInstance().getTime());
        space.setCreator(owner);
        log.debug("Creating personal space for user {} using space key {}", (Object)owner, key);
        this.saveSpaceNoEvent(space);
        if (isPrivate) {
            this.spacePermissionManager.createPrivateSpacePermissions(space);
        } else {
            this.spacePermissionManager.createDefaultSpacePermissions(space);
        }
        Label label = new Label("favourite", Namespace.PERSONAL, owner);
        this.labelManager.addLabel(space.getDescription(), label);
        SpaceCreateEvent spaceCreateEvent = new SpaceCreateEvent(this, space);
        this.eventPublisher.publish((Object)spaceCreateEvent);
        return space;
    }

    @Override
    @Deprecated
    public @NonNull Space createSpace(Space space) {
        Preconditions.checkArgument((boolean)Space.isValidGlobalSpaceKey(space.getKey()), (Object)"Invalid space key.");
        Preconditions.checkArgument((boolean)StringUtils.isNotBlank((CharSequence)space.getName()), (Object)"Name is blank. Cannot create space with no name.");
        DefaultSpaceManager.checkArgumentNotNull(space.getCreator(), "Cannot create spaces anonymously. You must specify a valid user for creator.");
        DefaultSpaceManager.checkArgumentNotNull(space.getDescription(), "Description is not set. Cannot create space with no description.");
        space.setName(space.getName().trim());
        if (space.getSpaceType() == null) {
            space.setSpaceType(SpaceType.GLOBAL);
        }
        this.saveSpaceNoEvent(space);
        this.spacePermissionManager.createDefaultSpacePermissions(space);
        SpaceCreateEvent spaceCreateEvent = new SpaceCreateEvent(this, space);
        this.eventPublisher.publish((Object)spaceCreateEvent);
        return space;
    }

    @Override
    public @Nullable Space getPersonalSpace(@Nullable ConfluenceUser user) {
        return this.spaceDao.getPersonalSpace(user);
    }

    @Override
    public long findPageTotal(Space space) {
        return this.spaceDao.findPageTotal(space);
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public int getNumberOfBlogPosts(Space space) {
        return this.spaceDao.getNumberOfBlogPosts(space);
    }

    @Override
    public @Nullable String getSpaceFromPageId(long pageId) {
        Space space = this.spaceDao.getSpaceByContentId(pageId);
        return space == null ? null : space.getKey();
    }

    @Override
    public void ensureSpaceDescriptionExists(Space space) {
        DefaultSpaceManager.checkArgumentNotNull(space, "space must not be null");
        if (space.getDescription() == null) {
            SpaceDescription spaceDescription = new SpaceDescription();
            spaceDescription.setSpace(space);
            spaceDescription.setBodyAsString("");
            space.setDescription(spaceDescription);
            this.saveSpace(space);
        }
    }

    @Override
    public @NonNull List<Space> getSpacesCreatedAfter(Date creationDate) {
        return this.spaceDao.getSpacesCreatedAfter(creationDate);
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    private Space setupSpace(String key, String name, @Nullable String description) {
        Preconditions.checkArgument((boolean)StringUtils.isNotBlank((CharSequence)key), (Object)"Key is blank. Cannot create space with no key.");
        Preconditions.checkArgument((boolean)StringUtils.isNotBlank((CharSequence)name), (Object)"Name is blank. Cannot create space with no name.");
        Space space = new Space();
        space.setKey(key);
        space.setName(name);
        SpaceDescription spaceDescription = new SpaceDescription();
        spaceDescription.setSpace(space);
        spaceDescription.setBodyAsString(description);
        space.setDescription(spaceDescription);
        return space;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    private void saveSpaceNoEvent(Space space) {
        this.spaceDescriptionManager.saveContentEntity(space.getDescription(), null);
        this.spaceDao.save(space);
    }

    @Override
    public @NonNull SpaceLogo getLogoForSpace(@Nullable String spaceKey) {
        String path;
        Space space;
        if (spaceKey != null && (space = this.getSpace(spaceKey)) != null && space.getDescription() != null && (path = this.attachmentManager.getAttachmentDownloadPath(space.getDescription(), spaceKey)) != null) {
            return new SpaceLogo(path, 2);
        }
        return this.getLogoForGlobalcontext();
    }

    @Override
    public @NonNull SpaceLogo getLogoForGlobalcontext() {
        String path;
        GlobalDescription globalDescription = this.settingsManager.getGlobalDescription();
        if (globalDescription != null && !this.settingsManager.getGlobalSettings().isDisableLogo() && (path = this.attachmentManager.getAttachmentDownloadPath(globalDescription, "global.logo")) != null) {
            return new SpaceLogo(path, 1);
        }
        return SpaceLogo.DEFAULT_SPACE_LOGO;
    }

    @Override
    public @NonNull ListBuilder<Space> getSpaces(SpacesQuery query) {
        return (ListBuilder)this.toSpacesQueryWithPermissionQueryBuilder(query).fold(accessDenied -> EMPTY_SPACE_LIST_BUILDER_RESULT, this::defaultListBuilderFromSpacesQuery);
    }

    private @NonNull DefaultListBuilder<Space> defaultListBuilderFromSpacesQuery(final SpacesQueryWithPermissionQueryBuilder builder) {
        return DefaultListBuilder.newInstance(new ListBuilderCallback<Space>(){

            @Override
            public List<Space> getElements(int offset, int maxResults) {
                if (!this.hasOneSpaceKeysConfigured(builder.getSpaceKeys()) && DefaultSpaceManager.this.bulkPermissionService != null && DefaultSpaceManager.this.denormalisedDarkFeatureEnabled()) {
                    return DefaultSpaceManager.this.bulkPermissionService.getPermittedSpaces(builder.getSpacesQuery(), offset, maxResults);
                }
                StopWatch stopWatch = StopWatch.createStarted();
                boolean isExempt = DefaultSpaceManager.this.permissionCheckExemptions.isExempt(builder.getUser());
                List<Space> spaces = DefaultSpaceManager.this.spaceDao.getSpaces(builder, offset, maxResults);
                DefaultSpaceManager.this.eventPublisher.publish((Object)new PermittedSpacesAnalyticsEvent(spaces.size(), maxResults, false, true, stopWatch.getTime(), isExempt, null));
                return spaces;
            }

            private boolean hasOneSpaceKeysConfigured(List<String> spaceKeys) {
                return spaceKeys != null && spaceKeys.size() == 1;
            }

            @Override
            public int getAvailableSize() {
                return DefaultSpaceManager.this.spaceDao.countSpaces(builder);
            }
        });
    }

    private boolean denormalisedDarkFeatureEnabled() {
        return this.denormalisedPermissionDarkFeature != null && this.denormalisedPermissionDarkFeature.isEnabled();
    }

    @Override
    @SafeVarargs
    public final @NonNull PageResponse<Space> getSpaces(SpacesQuery query, LimitedRequest limitedRequest, Predicate<? super Space> ... filter) {
        List result = (List)this.toSpacesQueryWithPermissionQueryBuilder(query).fold(accessDenied -> Collections.emptyList(), spacesQueryWithPermissionClauseBuilder -> this.spaceDao.getSpaces((SpacesQueryWithPermissionQueryBuilder)spacesQueryWithPermissionClauseBuilder, limitedRequest.getStart(), limitedRequest.getLimit() + 1));
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, (List)result, t -> Arrays.stream(filter).allMatch(p -> p.test(t)));
    }

    @Override
    public @NonNull Either<AccessDenied, SpacesQueryWithPermissionQueryBuilder> toSpacesQueryWithPermissionQueryBuilder(SpacesQuery spacesQuery) {
        User user = spacesQuery.getUser();
        String permissionType = spacesQuery.getPermissionType();
        if (permissionType == null) {
            log.debug("Created 'SpacesQueryWithPermissionQueryBuilder' without permission check - permissionType is null");
            return Either.right((Object)SpacesQueryWithPermissionQueryBuilder.spacesQueryWithoutPermissionCheck(spacesQuery));
        }
        if (this.accessModeManager.shouldEnforceReadOnlyAccess() && !SpacePermission.READ_ONLY_SPACE_PERMISSIONS.contains(permissionType)) {
            return Either.left((Object)AccessDenied.INSTANCE);
        }
        if (this.permissionCheckExemptions.isExempt(user)) {
            log.debug("Created 'SpacesQueryWithPermissionQueryBuilder' without permission check - user is exempt from permission checks");
            return Either.right((Object)SpacesQueryWithPermissionQueryBuilder.spacesQueryWithoutPermissionCheck(spacesQuery));
        }
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        return FugueConversionUtil.toIoEither(this.spacePermissionQueryManager.createSpacePermissionQueryBuilder(confluenceUser, permissionType).map(permissionQueryBuilder -> SpacesQueryWithPermissionQueryBuilder.spacesQueryWithPermissionCheck(spacesQuery, permissionQueryBuilder)));
    }

    @Override
    public @NonNull List<Space> getAllSpaces(SpacesQuery query) {
        return this.getSpaces(query).getPage(0, Integer.MAX_VALUE);
    }

    @Override
    public @NonNull List<User> getSpaceAdmins(Space space) {
        return this.getSpaceAdmins(space, Integer.MAX_VALUE);
    }

    @Override
    public @NonNull List<User> getSpaceAdmins(Space space, int limit) {
        TreeSet adminsSet = Sets.newTreeSet((Comparator)new UserComparator());
        for (SpacePermission perm : space.getPermissions()) {
            if (adminsSet.size() >= limit) break;
            if (!perm.getType().equals("SETSPACEPERMISSIONS")) continue;
            ConfluenceUser userSubject = perm.getUserSubject();
            if (userSubject != null) {
                adminsSet.add(userSubject);
                continue;
            }
            if (!perm.isGroupPermission()) continue;
            try {
                Group group = this.groupManager.getGroup(perm.getGroup());
                for (ConfluenceUser user : this.userAccessor.getMembers(group)) {
                    if (!adminsSet.add(user) || adminsSet.size() < limit) continue;
                }
            }
            catch (EntityException e) {
                log.error("Failed to query group " + perm.getGroup() + " in space manager.", (Throwable)e);
            }
        }
        return Lists.newArrayList((Iterable)adminsSet);
    }

    public void setAccessModeManager(AccessModeManager accessModeManager) {
        this.accessModeManager = accessModeManager;
    }

    public void setBulkPermissionService(BulkPermissionService bulkPermissionService) {
        this.bulkPermissionService = bulkPermissionService;
    }

    public void setDenormalisedPermissionDarkFeature(DenormalisedPermissionDarkFeature denormalisedPermissionDarkFeature) {
        this.denormalisedPermissionDarkFeature = denormalisedPermissionDarkFeature;
    }

    @Override
    public void archiveSpace(Space space) {
        if (space.isArchived()) {
            return;
        }
        try {
            Space oldSpace = (Space)space.clone();
            space.setSpaceStatus(SpaceStatus.ARCHIVED);
            this.saveSpace(space, oldSpace);
        }
        catch (CloneNotSupportedException e) {
            throw new ServiceException("Unable to archive space with key '" + space.getKey() + "'", (Throwable)e);
        }
    }

    @Override
    public void unarchiveSpace(Space space) {
        if (!space.isArchived()) {
            return;
        }
        try {
            Space oldSpace = (Space)space.clone();
            space.setSpaceStatus(SpaceStatus.CURRENT);
            this.saveSpace(space, oldSpace);
        }
        catch (CloneNotSupportedException e) {
            throw new ServiceException("Unable to unarchive space with key '" + space.getKey() + "'", (Throwable)e);
        }
    }

    @Override
    public @NonNull Collection<String> getAllSpaceKeys(SpaceStatus status) {
        return this.spaceDao.findSpaceKeysWithStatus(status.toString());
    }

    private boolean removeBundledSpaceContent(String spaceKey, @NonNull ProgressMeter progressMeter) {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        try {
            Space spaceToBeRemoved = this.getSpace(spaceKey);
            if (spaceToBeRemoved == null) {
                return false;
            }
            progressMeter.setStatus(i18NBean.getText("progress.remove.space.bundled.content.pages"));
            this.pageManager.removeAllPages(spaceToBeRemoved, progressMeter);
            spaceToBeRemoved.setHomePage(null);
            progressMeter.setStatus(i18NBean.getText("progress.remove.space.bundled.content.blogs"));
            this.pageManager.removeAllBlogPosts(spaceToBeRemoved, progressMeter);
            progressMeter.setPercentage(progressMeter.getPercentageComplete() + 10);
            progressMeter.setStatus(i18NBean.getText("progress.remove.space.bundled.content.template"));
            this.pageTemplateManager.removeAllPageTemplates(spaceToBeRemoved);
            progressMeter.setPercentage(progressMeter.getPercentageComplete() + 2);
            progressMeter.setStatus(i18NBean.getText("progress.remove.space.bundled.content.description.labels"));
            this.labelManager.removeAllLabels(spaceToBeRemoved.getDescription());
            progressMeter.setPercentage(progressMeter.getPercentageComplete() + 2);
            progressMeter.setStatus(i18NBean.getText("progress.remove.space.bundled.content.bandana"));
            this.bandanaPersister.remove((BandanaContext)new ConfluenceBandanaContext(spaceToBeRemoved));
            progressMeter.setPercentage(progressMeter.getPercentageComplete() + 4);
            if (spaceToBeRemoved.getDescription() != null) {
                SpaceDescription desc = spaceToBeRemoved.getDescription();
                spaceToBeRemoved.setDescription(null);
                desc.setSpace(null);
                progressMeter.setStatus(i18NBean.getText("progress.remove.space.bundled.content.description"));
                this.spaceDescriptionManager.removeContentEntity(desc);
            }
            progressMeter.setPercentage(progressMeter.getPercentageComplete() + 5);
            return true;
        }
        catch (RuntimeException e) {
            progressMeter.setCompletedSuccessfully(false);
            progressMeter.setStatus(i18NBean.getText("progress.remove.space.failed"));
            throw e;
        }
    }

    @Override
    public Boolean removeSpace(String spaceKey) {
        return this.removeSpace(spaceKey, new ProgressMeter());
    }

    private static void checkArgumentNotNull(@Nullable Object reference, String errorMessage) {
        Preconditions.checkArgument((reference != null ? 1 : 0) != 0, (Object)errorMessage);
    }

    private static class UserComparator
    implements Comparator<User> {
        private UserComparator() {
        }

        @Override
        public int compare(User user1, User user2) {
            String fullName1 = user1.getFullName();
            String fullName2 = user2.getFullName();
            if (StringUtils.isBlank((CharSequence)fullName1) && StringUtils.isNotBlank((CharSequence)fullName2)) {
                return 1;
            }
            if (StringUtils.isBlank((CharSequence)fullName2) && StringUtils.isNotBlank((CharSequence)fullName1)) {
                return -1;
            }
            if (StringUtils.isBlank((CharSequence)fullName1) && StringUtils.isBlank((CharSequence)fullName2)) {
                return 0;
            }
            return fullName1.compareTo(fullName2);
        }
    }
}

