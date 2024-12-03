/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.restore;

import com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.CustomEmoticonService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SpaceImportFinishEventListener {
    private static final Logger logger = LoggerFactory.getLogger(SpaceImportFinishEventListener.class);
    private final EventPublisher eventPublisher;
    private final CustomEmoticonService customEmoticonService;

    public SpaceImportFinishEventListener(@ComponentImport EventPublisher eventPublisher, @Qualifier(value="customEmoticonService") CustomEmoticonService customEmoticonService) {
        this.eventPublisher = eventPublisher;
        this.customEmoticonService = customEmoticonService;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handleEvent(AsyncImportFinishedEvent event) {
        if (event == null) {
            logger.error("Invalid import event (null) received. Will skip it");
            return;
        }
        if (StringUtils.isEmpty((CharSequence)event.getImportContext().getSpaceKeyOfSpaceImport())) {
            logger.debug("Skip Custom Emoticon cleanup for Site Import");
            return;
        }
        logger.debug("Start Custom Emoticon cleanup for Space Import");
        try {
            this.customEmoticonService.cleanupInvalidEmoticon();
        }
        catch (Exception ex) {
            logger.error("Exception happen while cleanup invalid custom emoticon", (Throwable)ex);
        }
    }
}

