/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugin.copyspace.task;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionFailureDescriptor;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.event.SpaceCopyEvent;
import com.atlassian.confluence.plugin.copyspace.exception.CopySpaceFlowException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.BlogPostService;
import com.atlassian.confluence.plugin.copyspace.service.ContextHolder;
import com.atlassian.confluence.plugin.copyspace.service.CopySpaceContextService;
import com.atlassian.confluence.plugin.copyspace.service.CopySpaceProgressBarCacheService;
import com.atlassian.confluence.plugin.copyspace.service.PageService;
import com.atlassian.confluence.plugin.copyspace.service.PermissionService;
import com.atlassian.confluence.plugin.copyspace.service.ProgressMeterService;
import com.atlassian.confluence.plugin.copyspace.service.SidebarLinkCopier;
import com.atlassian.confluence.plugin.copyspace.service.SpaceService;
import com.atlassian.confluence.plugin.copyspace.util.EventFactory;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.IOException;

public class BulkCopySpaceLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    public static final String TASK_NAME = "Copy space long running task";
    private static final String BANDANA_KEY_COPYING_SPACE_KEY = "copyspace.copier.spacekey";
    private final TransactionTemplate transactionTemplate;
    private final BlogPostService blogPostService;
    private final PageService pageService;
    private final SpaceService spaceService;
    private final EventPublisher eventPublisher;
    private final SidebarLinkCopier sidebarLinkCopier;
    private final BandanaManager bandanaManager;
    private final CopySpaceContext copySpaceContext;
    private final PermissionService permissionService;
    private final ProgressMeterService progressMeterService;
    private final CopySpaceProgressBarCacheService copySpaceProgressBarCacheService;

    public BulkCopySpaceLongRunningTask(CopySpaceRequest copySpaceRequest, TransactionTemplate transactionTemplate, BlogPostService blogPostService, PageService pageService, SpaceService spaceService, ContextHolder contextHolder, CopySpaceContextService copySpaceContextService, EventPublisher eventPublisher, SidebarLinkCopier sidebarLinkCopier, BandanaManager bandanaManager, PermissionService permissionService, ProgressMeterService progressMeterService, CopySpaceProgressBarCacheService copySpaceProgressBarCacheService) {
        this.transactionTemplate = transactionTemplate;
        this.blogPostService = blogPostService;
        this.pageService = pageService;
        this.spaceService = spaceService;
        this.eventPublisher = eventPublisher;
        this.sidebarLinkCopier = sidebarLinkCopier;
        this.permissionService = permissionService;
        this.progressMeterService = progressMeterService;
        this.copySpaceProgressBarCacheService = copySpaceProgressBarCacheService;
        this.bandanaManager = bandanaManager;
        this.copySpaceContext = copySpaceContextService.createContext(copySpaceRequest);
        contextHolder.put(this.copySpaceContext.getUuid(), this.copySpaceContext);
    }

    public String getName() {
        return TASK_NAME;
    }

    protected void runInternal() {
        try {
            this.progress = this.copySpaceContext.getProgressMeter();
            log.debug("Starting copy process for space {}...", (Object)this.copySpaceContext.getOriginalSpaceKey());
            this.eventPublisher.publish((Object)EventFactory.createCopySpaceStartedEvent(this.copySpaceContext));
            this.execute();
            this.eventPublisher.publish((Object)EventFactory.createCopySpaceSuccessEvent(this.copySpaceContext));
        }
        catch (CopySpaceFlowException e) {
            log.error("Error: ", (Throwable)e);
            this.checkForIOException(e);
            this.eventPublisher.publish((Object)EventFactory.createCopySpaceFailedEvent(this.copySpaceContext, e.getFailureDescriptor()));
            this.copySpaceContext.getProgressMeter().setCompletedSuccessfully(false);
            throw e;
        }
        catch (Exception e) {
            log.error("Error: ", (Throwable)e);
            this.checkForIOException(e);
            this.eventPublisher.publish((Object)EventFactory.createCopySpaceFailedEvent(this.copySpaceContext, ExecutionFailureDescriptor.unknown()));
            this.copySpaceContext.getProgressMeter().setCompletedSuccessfully(false);
            throw e;
        }
        finally {
            log.debug("Copying permissions...");
            this.copySpacePermissions();
            try {
                this.spaceService.copySpaceWatchers(this.copySpaceContext);
                this.blogPostService.copyWholeBlogWatchers(this.copySpaceContext);
            }
            catch (Exception e) {
                log.error("Error: ", (Throwable)e);
            }
            this.copySpaceProgressBarCacheService.removeProgressBarData(this.copySpaceContext.getOriginalSpaceKey());
        }
    }

    private void checkForIOException(Exception e) {
        for (Throwable cause = e.getCause(); cause != null; cause = cause.getCause()) {
            if (!(cause instanceof IOException)) continue;
            this.progressMeterService.setAttachmentErrorMessage(this.progress);
        }
    }

    private void copySpacePermissions() {
        this.transactionTemplate.execute(() -> {
            try {
                this.permissionService.copySpacePermissions(this.spaceService.getSpace(this.copySpaceContext.getOriginalSpaceKey()), this.spaceService.getSpace(this.copySpaceContext.getTargetSpaceKey()), this.copySpaceContext.isCopyMetadata());
            }
            catch (Exception e) {
                log.error("Error copying space permissions", (Throwable)e);
            }
            return null;
        });
    }

    protected final void execute() {
        this.progress.setPercentage(0);
        this.progressMeterService.setStatusMessage("copyspace.progress.message.started", this.progress);
        CopySpaceContext context = this.copySpaceContext;
        this.spaceService.createNewSpace(this.copySpaceContext);
        if (context.isCopyPages()) {
            log.debug("Initiating copying of pages...");
            this.pageService.copyPages(this.copySpaceContext);
        }
        if (context.isCopyBlogPosts()) {
            log.debug("Initiating copying of blog posts...");
            this.blogPostService.copyBlogPosts(this.copySpaceContext);
        }
        this.sidebarLinkCopier.copyNonRewritableLinks(context.getOriginalSpaceKey(), context.getTargetSpaceKey());
        this.publishEvents(context);
        this.progressMeterService.setTimeTaken(this.getElapsedTime(), this.progress);
        this.progress.setPercentage(100);
        this.progress.setCompletedSuccessfully(true);
        this.recordCopyingSpaceAgainstCopiedSpace(context);
        log.debug("Space copy process finished.");
    }

    private void publishEvents(CopySpaceContext context) {
        Space targetSpace = this.spaceService.getSpace(context.getTargetSpaceKey());
        this.transactionTemplate.execute(() -> {
            this.eventPublisher.publish((Object)new SpaceCopyEvent((Object)this, targetSpace, context.isCopyComments(), context.isCopyLabels(), context.isCopyAttachments(), context.isCopyMetadata(), context.isPreserveWatchers(), context.isCopyBlogPosts(), context.isCopyPages(), context.getOriginalSpaceKey()));
            return null;
        });
    }

    private void recordCopyingSpaceAgainstCopiedSpace(CopySpaceContext context) {
        this.transactionTemplate.execute(() -> {
            ConfluenceBandanaContext bandanaContext = new ConfluenceBandanaContext(this.spaceService.getSpace(context.getTargetSpaceKey()));
            this.bandanaManager.setValue((BandanaContext)bandanaContext, BANDANA_KEY_COPYING_SPACE_KEY, (Object)context.getOriginalSpaceKey());
            return null;
        });
    }
}

