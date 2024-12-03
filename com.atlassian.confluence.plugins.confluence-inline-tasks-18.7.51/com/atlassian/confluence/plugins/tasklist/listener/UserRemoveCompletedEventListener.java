/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.core.BatchOperationManager
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.event.events.user.UserRemoveCompletedEvent
 *  com.atlassian.confluence.search.ConfluenceIndexer
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.listener;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.event.events.user.UserRemoveCompletedEvent;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.ao.dao.InlineTaskDao;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UserRemoveCompletedEventListener {
    private static final Logger log = LoggerFactory.getLogger(UserRemoveCompletedEventListener.class);
    private final InlineTaskDao inlineTaskDao;
    private final ContentEntityManager contentEntityManager;
    private final ConfluenceIndexer indexer;
    private final BatchOperationManager batchOperationManager;
    private final EventPublisher eventPublisher;
    private final int entityCommitSize = Integer.getInteger("reindex.entity.commit.size", 100);

    @Autowired
    public UserRemoveCompletedEventListener(EventPublisher eventPublisher, InlineTaskDao inlineTaskDao, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ConfluenceIndexer indexer, BatchOperationManager batchOperationManager) {
        this.inlineTaskDao = inlineTaskDao;
        this.contentEntityManager = contentEntityManager;
        this.indexer = indexer;
        this.batchOperationManager = batchOperationManager;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public final void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public final void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void userRemoveCompleted(UserRemoveCompletedEvent event) {
        User removedUser = event.getUser();
        if (!(removedUser instanceof ConfluenceUser)) {
            return;
        }
        List<Task> tasks = this.inlineTaskDao.findByAssignee(((ConfluenceUser)removedUser).getKey());
        List contentIDs = tasks.stream().map(Task::getContentId).distinct().collect(Collectors.toList());
        long startOperation = System.currentTimeMillis();
        this.batchOperationManager.applyInBatches(contentIDs, this.entityCommitSize, contentIDs.size(), contentID -> {
            this.indexer.reIndexExcludingDependents((Searchable)this.contentEntityManager.getById(contentID.longValue()));
            return null;
        });
        log.debug("Committed {} entities for reindex in {}ms", (Object)contentIDs.size(), (Object)(System.currentTimeMillis() - startOperation));
    }
}

