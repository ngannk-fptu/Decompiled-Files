/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.settings;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.GlobalDescriptionDao;
import com.atlassian.confluence.setup.settings.GlobalDescriptionManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGlobalDescriptionManager
implements GlobalDescriptionManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultGlobalDescriptionManager.class);
    private static final String GLOBAL_DESCRIPTION_RECORD_ID_BANDANA_KEY = "global-description.record-id";
    private final GlobalDescriptionDao globalDescriptionDao;
    private final BandanaManager bandanaManager;
    private final EventPublisher eventPublisher;
    private final AtomicReference<Long> cachedGlobalDescriptionId = new AtomicReference();

    public DefaultGlobalDescriptionManager(GlobalDescriptionDao globalDescriptionDao, BandanaManager bandanaManager, EventPublisher eventPublisher) {
        this.globalDescriptionDao = Objects.requireNonNull(globalDescriptionDao);
        this.bandanaManager = Objects.requireNonNull(bandanaManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @PostConstruct
    public void postConstruct() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        this.validateAndFixGlobalDescriptionRecordIdInLongTermMemoryIfRequired();
    }

    private void validateAndFixGlobalDescriptionRecordIdInLongTermMemoryIfRequired() {
        GlobalDescription globalDescription;
        Long globalDescriptionRecordId = this.getRecordIdFromLongTermMemory();
        GlobalDescription globalDescription2 = globalDescription = globalDescriptionRecordId != null ? this.globalDescriptionDao.getGlobalDescriptionById(globalDescriptionRecordId) : null;
        if (globalDescription != null) {
            this.cachedGlobalDescriptionId.set(globalDescription.getId());
            return;
        }
        globalDescription = this.globalDescriptionDao.getGlobalDescription();
        if (globalDescription != null) {
            this.saveRecordIdIntoLongTermMemory(globalDescription.getId());
            this.cachedGlobalDescriptionId.set(globalDescription.getId());
        }
    }

    @Override
    public GlobalDescription getGlobalDescription() {
        GlobalDescription globalDescription;
        Long recordId = this.cachedGlobalDescriptionId.get();
        GlobalDescription globalDescription2 = globalDescription = recordId != null ? this.globalDescriptionDao.getGlobalDescriptionById(recordId) : null;
        if (globalDescription != null) {
            return globalDescription;
        }
        globalDescription = this.globalDescriptionDao.getGlobalDescription();
        if (globalDescription != null) {
            this.cachedGlobalDescriptionId.set(globalDescription.getId());
        }
        return globalDescription;
    }

    @Override
    public void updateGlobalDescription(GlobalDescription globalDescription) {
        GlobalDescription oldDescription = this.getGlobalDescription();
        if (oldDescription != null && oldDescription != globalDescription && globalDescription.getId() != oldDescription.getId()) {
            log.error("One GlobalDescription object already exists. The new one will not be saved.");
            log.debug("Attempt to create the second GlobalDescription object: ", new Throwable());
            return;
        }
        this.globalDescriptionDao.save(globalDescription);
    }

    public Long getRecordIdFromLongTermMemory() {
        return (Long)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, GLOBAL_DESCRIPTION_RECORD_ID_BANDANA_KEY);
    }

    public void saveRecordIdIntoLongTermMemory(long id) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, GLOBAL_DESCRIPTION_RECORD_ID_BANDANA_KEY, (Object)id);
    }
}

