/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.pagination.ContentCursor
 *  com.atlassian.confluence.api.model.pagination.CursorType
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.pages;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.pagination.ContentCursor;
import com.atlassian.confluence.api.model.pagination.CursorType;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentPurgedFromTrashEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.event.events.space.SpaceTrashEmptyEvent;
import com.atlassian.confluence.internal.pages.TrashManagerInternal;
import com.atlassian.confluence.internal.persistence.ContentEntityObjectDaoInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class DefaultTrashManager
implements TrashManagerInternal {
    private static final Logger log = LoggerFactory.getLogger(DefaultTrashManager.class);
    static final String TRASH_DATE_MIGRATION_TIME_KEY = "trash.date.migration.time";
    private static final int OBJECTS_PER_TRANSACTION = 100;
    private final ContentEntityObjectDaoInternal<ContentEntityObject> contentEntityObjectDao;
    private final ContentEntityManager contentEntityManager;
    private final CustomContentManager customContentManager;
    private final PlatformTransactionManager transactionManager;
    private final PageManager pageManager;
    private final EventPublisher eventPublisher;
    private final AttachmentManager attachmentManager;
    private final BandanaManager bandanaManager;
    private final PaginationService paginationService;
    private final ContentFactory contentFactory;

    @Deprecated
    public DefaultTrashManager(ContentEntityObjectDaoInternal<ContentEntityObject> contentEntityObjectDao, ContentEntityManager contentEntityManager, CustomContentManager customContentManager, PlatformTransactionManager transactionManager, PageManager pageManager, EventPublisher eventPublisher, AttachmentManager attachmentManager, BandanaManager bandanaManager) {
        this.contentEntityObjectDao = contentEntityObjectDao;
        this.contentEntityManager = contentEntityManager;
        this.customContentManager = customContentManager;
        this.transactionManager = transactionManager;
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
        this.attachmentManager = attachmentManager;
        this.bandanaManager = bandanaManager;
        this.paginationService = null;
        this.contentFactory = null;
    }

    public DefaultTrashManager(ContentEntityObjectDaoInternal<ContentEntityObject> contentEntityObjectDao, ContentEntityManager contentEntityManager, CustomContentManager customContentManager, PlatformTransactionManager transactionManager, PageManager pageManager, EventPublisher eventPublisher, AttachmentManager attachmentManager, BandanaManager bandanaManager, PaginationService paginationService, ContentFactory contentFactory) {
        this.contentEntityObjectDao = contentEntityObjectDao;
        this.contentEntityManager = contentEntityManager;
        this.customContentManager = customContentManager;
        this.transactionManager = transactionManager;
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
        this.attachmentManager = attachmentManager;
        this.bandanaManager = bandanaManager;
        this.paginationService = paginationService;
        this.contentFactory = contentFactory;
    }

    @PostConstruct
    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void onShutdown() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public void emptyTrash(Space space) {
        if (log.isInfoEnabled()) {
            int count = this.getNumberOfItemsInTrash(space);
            log.info("Starting to purge {} items from trash for space {}.", (Object)count, (Object)space);
        }
        while (this.deleteBlock(space.getKey())) {
        }
        this.eventPublisher.publish((Object)new SpaceTrashEmptyEvent(this, space));
        log.info("Finished purging trash");
    }

    @Override
    public int getNumberOfItemsInTrash(Space space) {
        return this.contentEntityObjectDao.countContentBySpaceIdAndStatus(space.getId(), "deleted");
    }

    @Override
    public boolean purge(String spaceKey, long contentId) {
        ContentEntityObject obj = this.contentEntityManager.getById(contentId);
        if (obj == null) {
            return false;
        }
        if (!obj.isDeleted() || !(obj instanceof SpaceContentEntityObject)) {
            throw new IllegalStateException("Only objects in the trash can be purged: " + obj);
        }
        if (!((SpaceContentEntityObject)obj).getSpaceKey().equals(spaceKey)) {
            throw new IllegalStateException("Object " + obj + " was not in expected space " + spaceKey);
        }
        this.deleteContentEntity(obj, true);
        return true;
    }

    @Override
    public List<ContentEntityObject> getTrashContents(Space space, int offset, int count) {
        return this.contentEntityObjectDao.getTrashedContents(space.getKey(), offset, count);
    }

    @Override
    public PageResponse<Content> getTrashContents(Space space, LimitedRequest request, Expansion[] expansions) throws ServiceException {
        this.validateCursor(request);
        return this.paginationService.performPaginationListRequestWithCursor(request, limitedRequest -> this.contentEntityObjectDao.getTrashedContents(space.getKey(), request, null), items -> this.contentFactory.buildFrom(items, new Expansions(expansions)), DefaultTrashManager::calculateCursorFromContent);
    }

    private static ContentCursor calculateCursorFromContent(ContentEntityObject content, boolean isReverse) {
        return ContentCursor.createCursor((boolean)isReverse, (long)content.getId());
    }

    private void validateCursor(LimitedRequest request) {
        if (request.getCursor().getCursorType() != CursorType.CONTENT) {
            throw new IllegalArgumentException(String.format("Cursor type is incorrect. Received: %s, but %s was expected", request.getCursor().getCursorType(), CursorType.CONTENT));
        }
    }

    @Override
    public void migrateTrashDate(Instant trashTime) {
        Objects.requireNonNull(trashTime);
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, TRASH_DATE_MIGRATION_TIME_KEY, (Object)trashTime);
    }

    @Override
    public void migrateTrashDate(String spaceKey, Instant trashTime) {
        Objects.requireNonNull(spaceKey);
        Objects.requireNonNull(trashTime);
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), TRASH_DATE_MIGRATION_TIME_KEY, (Object)trashTime);
    }

    @EventListener
    public void onSpaceRemoval(SpaceRemoveEvent event) {
        this.bandanaManager.removeValue((BandanaContext)new ConfluenceBandanaContext(event.getSpace().getKey()), TRASH_DATE_MIGRATION_TIME_KEY);
    }

    @Override
    public Optional<Instant> getTrashDateMigrationTime() {
        Instant migrationTime = (Instant)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, TRASH_DATE_MIGRATION_TIME_KEY);
        return Optional.ofNullable(migrationTime);
    }

    @Override
    public Optional<Instant> findTrashDate(ContentEntityObject ceo) {
        String spaceKey;
        Instant spaceTrashDate;
        Objects.requireNonNull(ceo);
        if (!ceo.isDeleted()) {
            return Optional.empty();
        }
        Optional<Instant> ceoTrashDate = ceo.getTrashDate();
        if (ceoTrashDate.isPresent()) {
            return ceoTrashDate;
        }
        if (ceo instanceof Spaced && (spaceTrashDate = (Instant)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(spaceKey = ((Spaced)((Object)ceo)).getSpace().getKey()), TRASH_DATE_MIGRATION_TIME_KEY)) != null) {
            return Optional.of(spaceTrashDate);
        }
        return this.getTrashDateMigrationTime();
    }

    @Override
    public Optional<Instant> findTrashDate(Content content) {
        String spaceKey;
        Instant spaceTrashDate;
        Objects.requireNonNull(content);
        if (content.getStatus() != ContentStatus.TRASHED) {
            return Optional.empty();
        }
        if (content.getMetadata().isEmpty()) {
            return Optional.empty();
        }
        if (!content.getMetadata().containsKey("trashdate")) {
            return Optional.empty();
        }
        long trashDateLong = (Long)content.getMetadata().get("trashdate");
        Optional<Instant> trashDate = Optional.ofNullable(trashDateLong == -1L ? null : Instant.ofEpochMilli(trashDateLong));
        if (trashDate.isPresent()) {
            return trashDate;
        }
        if (content instanceof Spaced && (spaceTrashDate = (Instant)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(spaceKey = ((Spaced)content).getSpace().getKey()), TRASH_DATE_MIGRATION_TIME_KEY)) != null) {
            return Optional.of(spaceTrashDate);
        }
        return this.getTrashDateMigrationTime();
    }

    @Override
    public List<SpaceContentEntityObject> getTrashedEntities(long contentIdOffset, int limit) {
        return this.contentEntityObjectDao.getTrashedEntities(contentIdOffset, limit);
    }

    @Override
    public void purge(List<SpaceContentEntityObject> trashEntities) {
        trashEntities.forEach(trash -> this.deleteContentEntity((ContentEntityObject)trash, false));
    }

    private boolean deleteBlock(final String spaceKey) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(3);
        return Objects.requireNonNull((Boolean)new TransactionTemplate(this.transactionManager, (TransactionDefinition)definition).execute((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try (Ticker ignored = Timers.start((String)(this.getClass().getName() + " Deleting 100"));){
                    List<ContentEntityObject> pagesToDelete = DefaultTrashManager.this.contentEntityObjectDao.getTrashedContents(spaceKey, 0, 100);
                    for (ContentEntityObject contentEntityObject : pagesToDelete) {
                        if (contentEntityObject instanceof Contained && ((Contained)((Object)contentEntityObject)).getContainer() == null) continue;
                        DefaultTrashManager.this.deleteContentEntity(contentEntityObject, false);
                    }
                    Boolean bl = pagesToDelete.size() != 0;
                    return bl;
                }
            }
        }));
    }

    private void deleteContentEntity(ContentEntityObject content, boolean notify) {
        if (content instanceof AbstractPage) {
            this.eventPublisher.publish((Object)new ContentPurgedFromTrashEvent((AbstractPage)content));
            ((AbstractPage)content).remove(this.pageManager);
        } else if (content instanceof Attachment) {
            if (notify) {
                this.attachmentManager.removeAttachmentFromServer((Attachment)content);
            } else {
                this.attachmentManager.removeAttachmentWithoutNotifications((Attachment)content);
            }
        } else if (content instanceof CustomContentEntityObject) {
            this.customContentManager.removeContentEntity(content);
        } else {
            this.contentEntityManager.removeContentEntity(content);
        }
    }
}

