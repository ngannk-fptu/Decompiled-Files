/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.page.PageCopyEvent
 *  com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.watcher;

import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions;
import com.atlassian.confluence.plugin.copyspace.chain.CopyHandler;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.ContextHolder;
import com.atlassian.confluence.plugin.copyspace.service.ProgressMeterService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="copyPageEventWatcher")
public class CopyPageEventWatcher {
    private final EventListenerRegistrar eventListenerRegistrar;
    private final List<CopyHandler> copyHandlers;
    private final ProgressMeterService progressMeterService;
    private final ContextHolder contextHolder;

    @Autowired
    public CopyPageEventWatcher(@ComponentImport(value="eventPublisher") EventListenerRegistrar eventListenerRegistrar, List<CopyHandler> copyHandlers, ProgressMeterService progressMeterService, ContextHolder contextHolder) {
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.copyHandlers = copyHandlers;
        this.progressMeterService = progressMeterService;
        this.contextHolder = contextHolder;
    }

    @PostConstruct
    public void registerEventListener() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unregisterEventListener() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onEvent(PageCopyEvent event) {
        CopySpaceContext context;
        boolean isRequestIdPresent;
        PageCopyOptions pageCopyOptions = event.getPageCopyOptions();
        boolean bl = isRequestIdPresent = pageCopyOptions != null && !StringUtils.isEmpty((CharSequence)pageCopyOptions.getRequestId());
        if (isRequestIdPresent && (context = this.contextHolder.getContext(pageCopyOptions.getRequestId())) != null) {
            for (CopyHandler copyHandler : this.copyHandlers) {
                copyHandler.checkAndCopy(event, context);
            }
            this.progressMeterService.incrementProgressMeterCount(context.getProgressMeter());
        }
    }
}

