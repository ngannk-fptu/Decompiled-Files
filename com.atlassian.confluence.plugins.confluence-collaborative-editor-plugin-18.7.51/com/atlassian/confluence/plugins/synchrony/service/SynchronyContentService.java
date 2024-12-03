/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.event.events.content.page.synchrony.ContentUpdatedEvent
 *  com.atlassian.confluence.event.events.content.page.synchrony.SynchronyLockEvent
 *  com.atlassian.confluence.event.events.content.page.synchrony.SynchronyRecoveryEvent
 *  com.atlassian.confluence.event.events.content.page.synchrony.SynchronyUnlockEvent
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 *  org.springframework.transaction.TransactionException
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.event.events.content.page.synchrony.ContentUpdatedEvent;
import com.atlassian.confluence.event.events.content.page.synchrony.SynchronyLockEvent;
import com.atlassian.confluence.event.events.content.page.synchrony.SynchronyRecoveryEvent;
import com.atlassian.confluence.event.events.content.page.synchrony.SynchronyUnlockEvent;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.synchrony.events.SynchronyRecoveryErrorEvent;
import com.atlassian.confluence.plugins.synchrony.model.SynchronyError;
import com.atlassian.confluence.plugins.synchrony.service.ConfluenceRecoveryManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyExternalChangesManager;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyLockService;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyRecoveryManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

@Component(value="synchrony-content-manager")
public class SynchronyContentService
implements DisposableBean,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(SynchronyContentService.class);
    private final Set<Long> ALL_ENTITIES_WILDCARD = Collections.emptySet();
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;
    private final SynchronyRecoveryManager synchronyRecoveryManager;
    private final ConfluenceRecoveryManager confluenceRecoveryManager;
    private final SynchronyExternalChangesManager externalChangesManager;
    private final SynchronyLockService synchronyLockService;
    private final Cache<Set<Long>, String> lockIdByEntities;

    @Autowired
    public SynchronyContentService(@ComponentImport EventPublisher eventPublisher, @ComponentImport TransactionTemplate transactionTemplate, @Qualifier(value="synchrony-recovery-manager") SynchronyRecoveryManager synchronyRecoveryManager, ConfluenceRecoveryManager confluenceRecoveryManager, SynchronyExternalChangesManager externalChangesManager, @Qualifier(value="synchrony-lock-service") SynchronyLockService synchronyLockService, @ComponentImport CacheManager cacheManager) {
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = transactionTemplate;
        this.synchronyRecoveryManager = synchronyRecoveryManager;
        this.confluenceRecoveryManager = confluenceRecoveryManager;
        this.externalChangesManager = externalChangesManager;
        this.synchronyLockService = synchronyLockService;
        this.lockIdByEntities = cacheManager.getCache(SynchronyContentService.class + ".lockIdByEntities", null, new CacheSettingsBuilder().local().expireAfterWrite(30L, TimeUnit.MINUTES).maxEntries(50000).build());
    }

    @EventListener
    public void recovery(SynchronyRecoveryEvent synchronyRecoveryEvent) {
        long contentId = synchronyRecoveryEvent.getCurrentContentId().asLong();
        ConfluenceUser user = synchronyRecoveryEvent.getUser();
        switch (synchronyRecoveryEvent.getRecoveryState()) {
            case "restored": {
                this.restoredRecovery(synchronyRecoveryEvent.getRecoveryState(), contentId, user);
                break;
            }
            case "synchrony-recovery": 
            case "synchrony-recovery-with-external-change": 
            case "limited": {
                this.synchronyRecovery(synchronyRecoveryEvent.getRecoveryState(), contentId, user);
                break;
            }
            case "confluence-recovery": 
            case "confluence-recovery-with-external-change": {
                this.confluenceRecovery(synchronyRecoveryEvent.getRecoveryState(), contentId, user);
                break;
            }
        }
    }

    private boolean restoredRecovery(String recoveryState, long contentId, ConfluenceUser user) {
        log.debug("Launching synchrony recovery for restored content {}", (Object)contentId);
        return this.runRecoveryInTransaction(contentId, recoveryState, (TransactionCallback<Boolean>)((TransactionCallback)() -> this.synchronyRecoveryManager.reconcile(contentId, user, true)));
    }

    public boolean synchronyRecovery(String recoveryState, long contentId, ConfluenceUser user) {
        log.debug("Launching synchrony recovery for content {}", (Object)contentId);
        return this.runRecoveryInTransaction(contentId, recoveryState, (TransactionCallback<Boolean>)((TransactionCallback)() -> this.synchronyRecoveryManager.reconcile(contentId, user)));
    }

    public boolean confluenceRecovery(String recoveryState, long contentId, ConfluenceUser user) {
        log.debug("Launching confluence recovery for content {}", (Object)contentId);
        return this.runRecoveryInTransaction(contentId, recoveryState, (TransactionCallback<Boolean>)((TransactionCallback)() -> this.confluenceRecoveryManager.reconcile(contentId, user)));
    }

    public boolean confluenceRecovery(long contentId, ConfluenceUser user, String conflictingRev) {
        return this.runRecoveryInTransaction(contentId, "confluence-recovery", (TransactionCallback<Boolean>)((TransactionCallback)() -> this.confluenceRecoveryManager.reconcile(contentId, user, conflictingRev)));
    }

    private boolean runRecoveryInTransaction(long contentId, String recoveryState, TransactionCallback<Boolean> callback) {
        try {
            return (Boolean)this.transactionTemplate.execute(callback);
        }
        catch (TransactionException e) {
            this.eventPublisher.publish((Object)new SynchronyRecoveryErrorEvent(recoveryState));
            log.warn("Could not reconcile content for " + recoveryState + ". ID: " + contentId + " Caused by: " + e.getRootCause());
            return false;
        }
        catch (Exception e) {
            this.eventPublisher.publish((Object)new SynchronyRecoveryErrorEvent(recoveryState));
            log.warn("Could not reconcile content for " + recoveryState + ". ID: " + contentId + " Caused by: " + e);
            return false;
        }
    }

    @EventListener
    public void syncContentOnUpdate(ContentUpdatedEvent contentUpdatedEvent) {
        this.externalChangesManager.syncContentOnUpdate(contentUpdatedEvent.getContentId(), contentUpdatedEvent.getContentStatus(), contentUpdatedEvent.getSpaceKey(), contentUpdatedEvent.getUser(), contentUpdatedEvent.getUpdateTrigger());
    }

    public Either<SynchronyError, JSONObject> discardUnpublishedChanges(ContentId contentId, ConfluenceUser user) {
        return this.externalChangesManager.performExternalChange(user, contentId, PageUpdateTrigger.DISCARD_CHANGES);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void lockEntities(SynchronyLockEvent lockEvent) {
        Set<Long> entities = this.setOfEntities(lockEvent);
        String lockId = UUID.randomUUID().toString();
        this.lockIdByEntities.put(entities, (Object)lockId);
        if (lockEvent.isGlobal()) {
            this.synchronyLockService.lockContent(lockId, lockEvent.getTimeout());
        } else {
            this.synchronyLockService.lockContent(lockId, entities, lockEvent.getTimeout());
        }
    }

    @EventListener
    public void unlockEntities(SynchronyUnlockEvent unlockEvent) {
        Set<Long> entities = this.setOfEntities(unlockEvent);
        String lockId = (String)this.lockIdByEntities.get(entities);
        if (lockId == null) {
            return;
        }
        this.lockIdByEntities.remove(entities);
        this.synchronyLockService.unlockContent(lockId);
    }

    private Set<Long> setOfEntities(SynchronyLockEvent synchronyLockEvent) {
        return synchronyLockEvent.isGlobal() ? this.ALL_ENTITIES_WILDCARD : new HashSet(synchronyLockEvent.getContentIds());
    }

    private Set<Long> setOfEntities(SynchronyUnlockEvent synchronyUnlockEvent) {
        return synchronyUnlockEvent.isGlobal() ? this.ALL_ENTITIES_WILDCARD : new HashSet(synchronyUnlockEvent.getContentIds());
    }
}

