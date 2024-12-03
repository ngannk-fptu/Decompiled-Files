/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions
 *  com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions$Builder
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions;
import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionFailureDescriptor;
import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionStage;
import com.atlassian.confluence.plugin.copyspace.api.event.FailureReason;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.exception.CopySpaceException;
import com.atlassian.confluence.plugin.copyspace.exception.CopySpaceFlowException;
import com.atlassian.confluence.plugin.copyspace.service.PageService;
import com.atlassian.confluence.plugin.copyspace.service.ProgressMeterService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="pageServiceImpl")
public class PageServiceImpl
implements PageService {
    private static final Logger log = LoggerFactory.getLogger(PageServiceImpl.class);
    private static final int MAX_RETRY = 5;
    private static final int BATCH_CONTENT_SIZE = 100;
    private static final int MAX_PAGES = Integer.getInteger("confluence.cph.max.entries", 2000);
    private static final String TEMPORARY_HOMEPAGE_PREFIX = "Temporary homepage ";
    private final PageManager pageManager;
    private final ContentService contentService;
    private final ProgressMeterService progressMeterService;
    private final TransactionTemplate transactionTemplate;
    private final SpaceManager spaceManager;

    @Autowired
    public PageServiceImpl(@ComponentImport PageManager pageManager, @ComponentImport(value="apiContentService") ContentService contentService, ProgressMeterService progressMeterService, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport SpaceManager spaceManager) {
        this.pageManager = pageManager;
        this.contentService = contentService;
        this.progressMeterService = progressMeterService;
        this.transactionTemplate = transactionTemplate;
        this.spaceManager = spaceManager;
    }

    @Override
    public void copyPages(CopySpaceContext copySpaceContext) {
        long tempHomePageId = this.getTempHomePageId(copySpaceContext.getTargetSpaceKey());
        this.copyPagesTrees(tempHomePageId, copySpaceContext);
        this.removeTempHomePage(tempHomePageId);
    }

    private void copyPagesTrees(long tempHomePageId, CopySpaceContext copySpaceContext) {
        List<Long> originalSpaceTopLevelPageIds = this.getTopLevelPageIds(copySpaceContext.getOriginalSpaceKey());
        if (!originalSpaceTopLevelPageIds.isEmpty()) {
            this.progressMeterService.setStatusMessage("copyspace.progress.message.pages.in.progress", copySpaceContext.getProgressMeter());
        }
        for (long pageId : originalSpaceTopLevelPageIds) {
            this.transactionTemplate.execute(() -> {
                Page page = this.pageManager.getPage(pageId);
                try {
                    PageCopyOptions pageCopyOptions = this.createPageCopyOptions(copySpaceContext);
                    this.pageManager.deepCopyPage(pageCopyOptions, page, this.pageManager.getPage(tempHomePageId));
                }
                catch (PermissionException e) {
                    log.error(e.getMessage(), (Throwable)e);
                    throw new CopySpaceFlowException(new ExecutionFailureDescriptor(ExecutionStage.COPY_PAGE, FailureReason.MISSING_SPACE_PERMISSIONS), (Throwable)e);
                }
                return null;
            });
        }
    }

    private List<Long> getTopLevelPageIds(String spaceKey) {
        return (List)this.transactionTemplate.execute(() -> {
            ListBuilder topLevelPages = this.pageManager.getTopLevelPagesBuilder(this.spaceManager.getSpace(spaceKey));
            int totalTopLevelPages = topLevelPages.getAvailableSize();
            ArrayList topLevelPageIds = new ArrayList();
            for (int i = 0; i < totalTopLevelPages; ++i) {
                topLevelPageIds.addAll(topLevelPages.getPage(i, 1).stream().map(EntityObject::getId).collect(Collectors.toList()));
            }
            return topLevelPageIds;
        });
    }

    private long getTempHomePageId(String spaceKey) {
        return (Long)this.transactionTemplate.execute(() -> {
            Optional pageWithSameName;
            Space space = this.spaceManager.getSpace(spaceKey);
            Page homePage = space.getHomePage();
            this.changeTitle(homePage, TEMPORARY_HOMEPAGE_PREFIX);
            for (int i = 1; i <= 5 && (pageWithSameName = this.contentService.find(new Expansion[0]).withSpace(new com.atlassian.confluence.api.model.content.Space[]{com.atlassian.confluence.api.model.content.Space.builder().key(space.getKey()).build()}).withType(new ContentType[]{ContentType.PAGE}).withTitle(homePage.getTitle()).fetch()).isPresent(); ++i) {
                if (i == 5) {
                    throw new CopySpaceFlowException(new ExecutionFailureDescriptor(ExecutionStage.COPY_PAGE, FailureReason.UNABLE_TO_CREATE_HOMEPAGE), (Throwable)new CopySpaceException("Cannot find a unique name for the temporary homepage"));
                }
                this.changeTitle(homePage, homePage.getTitle());
            }
            return homePage.getId();
        });
    }

    private void changeTitle(Page homePage, String initialTitle) {
        homePage.setTitle(initialTitle + UUID.randomUUID());
    }

    private void removeTempHomePage(long tempHomePageId) {
        this.transactionTemplate.execute(() -> {
            Page page = this.pageManager.getPage(tempHomePageId);
            page.remove(this.pageManager);
            return null;
        });
    }

    private PageCopyOptions createPageCopyOptions(CopySpaceContext context) {
        return ((PageCopyOptions.Builder)((PageCopyOptions.Builder)((PageCopyOptions.Builder)((PageCopyOptions.Builder)PageCopyOptions.builder().withCopyAttachment(context.isCopyAttachments()).withCopyLabel(context.isCopyLabels()).withMaxProcessedEntries(MAX_PAGES)).withBatchSize(100)).withCopyPermission(true).withUser(AuthenticatedUserThreadLocal.get())).withPageContentTranformer((pageContent, originalPage, destinationPage) -> {
            destinationPage.setSynchronyRevisionSource("restored");
            return pageContent;
        }).withProgressMeter(new ProgressMeter())).withRequestId(context.getUuid()).withSkipLinkUpdates(true).build();
    }
}

