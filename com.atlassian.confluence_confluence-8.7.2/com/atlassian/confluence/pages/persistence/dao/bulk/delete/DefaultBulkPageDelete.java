/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.impl.hibernate.bulk.BulkTransaction
 *  com.atlassian.confluence.impl.hibernate.bulk.HibernateBulkTransaction
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.delete;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.core.DefaultDeleteContext;
import com.atlassian.confluence.impl.hibernate.bulk.BulkAction;
import com.atlassian.confluence.impl.hibernate.bulk.BulkStatusReport;
import com.atlassian.confluence.impl.hibernate.bulk.BulkStatusReportEnum;
import com.atlassian.confluence.impl.hibernate.bulk.BulkTransaction;
import com.atlassian.confluence.impl.hibernate.bulk.HibernateBulkTransaction;
import com.atlassian.confluence.impl.hibernate.bulk.RecursiveHibernateBulkAction;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.bulk.AbstractBulkPageAction;
import com.atlassian.confluence.pages.persistence.dao.bulk.delete.BulkPageDelete;
import com.atlassian.confluence.pages.persistence.dao.bulk.delete.BulkPageDeleteExecutionContext;
import com.atlassian.confluence.pages.persistence.dao.bulk.delete.PageDeleteOptions;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.LogProgressMeterWrapper;
import com.atlassian.confluence.util.SubProgressMeter;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBulkPageDelete
implements BulkPageDelete {
    private static final String LOCK_PREFIX = BulkAction.LOCK_PREFIX;
    private static final float DELETE_PAGE_PERCENTAGE = 0.9f;
    private static final float MOVE_PAGE_PERCENTAGE = 0.1f;
    private static final int PERCENTAGE_COMPLETE = 100;
    private final Logger log = LoggerFactory.getLogger(DefaultBulkPageDelete.class);
    private final PermissionManager permissionManager;
    private final PageManagerInternal pageManager;
    private final SessionFactory sessionFactory;
    private final ClusterLockService lockService;
    private final Map<Long, Long> childToParentMap;
    private final List<Long> childToRootList;
    private final Set<Long> trashedPageIds;
    private final Set<Long> viewRestrictedIgnoredPageIds;
    private final Set<Long> editRestrictedIgnoredPageIds;
    private final ObjectMapper objectMapper;
    private final BulkStatusReport.Builder statusReportBuilder;

    public DefaultBulkPageDelete(PermissionManager permissionManager, SessionFactory sessionFactory, ClusterLockService clusterLockService, PageManagerInternal pageManager) {
        this.permissionManager = permissionManager;
        this.sessionFactory = sessionFactory;
        this.pageManager = pageManager;
        this.lockService = clusterLockService;
        this.childToParentMap = new LinkedHashMap<Long, Long>();
        this.childToRootList = new ArrayList<Long>();
        this.trashedPageIds = new HashSet<Long>();
        this.viewRestrictedIgnoredPageIds = new HashSet<Long>();
        this.editRestrictedIgnoredPageIds = new HashSet<Long>();
        this.objectMapper = new ObjectMapper();
        this.statusReportBuilder = BulkStatusReport.getBuilder();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void deepDelete(PageDeleteOptions deleteOptions, Page targetPage) {
        Preconditions.checkNotNull((Object)deleteOptions, (Object)"PageDeleteOptions should not be null");
        Preconditions.checkNotNull((Object)targetPage, (Object)"Target page should not be null");
        ConfluenceUser user = deleteOptions.getUser();
        ConfluenceUser lastLoggedInUser = AuthenticatedUserThreadLocal.get();
        try {
            AuthenticatedUserThreadLocal.set(user);
            this.deepDeleteInternal(deleteOptions, targetPage, user);
        }
        finally {
            AuthenticatedUserThreadLocal.set(lastLoggedInUser);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void deepDeleteInternal(PageDeleteOptions options, Page target, ConfluenceUser user) {
        int actionedEntities;
        LogProgressMeterWrapper logProgressMeter;
        block10: {
            ClusterLock lock;
            RecursiveHibernateBulkAction<BulkPageDeleteExecutionContext, Page> bulkAction;
            BulkPageDeleteExecutionContext context;
            SubProgressMeter movePageProgressMeter;
            SubProgressMeter deletePageProgressMeter;
            block8: {
                block9: {
                    String spaceKey = target.getSpaceKey();
                    ProgressMeter progressMeter = options.getProgressMeter();
                    logProgressMeter = new LogProgressMeterWrapper(progressMeter);
                    actionedEntities = 0;
                    int totalPageNeedToDelete = options.getTargetPageIds().size();
                    deletePageProgressMeter = new SubProgressMeter(logProgressMeter, 0.9f, totalPageNeedToDelete);
                    movePageProgressMeter = new SubProgressMeter(logProgressMeter, 0.1f, options.getBatchSize());
                    HibernateBulkTransaction bulkTransaction = new HibernateBulkTransaction(this.sessionFactory);
                    context = new BulkPageDeleteExecutionContext(deletePageProgressMeter, user, target.getParent());
                    bulkAction = new RecursiveHibernateBulkAction<BulkPageDeleteExecutionContext, Page>((BulkTransaction)bulkTransaction, deletePageProgressMeter, options.getBatchSize(), options.getMaxProcessedEntries());
                    lock = this.lockService.getLockForName(LOCK_PREFIX + "." + spaceKey);
                    if (lock != null && lock.tryLock()) break block8;
                    this.log.error("Failed to acquire lock for delete page hierarchy: [{}]", (Object)lock);
                    logProgressMeter.setStatus(this.getStatusString(this.statusReportBuilder.addErrorMessage(BulkStatusReportEnum.ERROR_LOCK_FAILED.name(), spaceKey).build()));
                    logProgressMeter.setCompletedSuccessfully(false);
                    lock = null;
                    if (lock == null) break block9;
                    lock.unlock();
                }
                logProgressMeter.setStatus(this.getStatusString(this.statusReportBuilder.withMessageKey(BulkStatusReportEnum.STATUS_DELETED_PAGES.name(), String.valueOf(actionedEntities)).addWarnMessage(BulkStatusReportEnum.WARN_IGNORE_EDIT_RESTRICTED.name(), String.valueOf(this.editRestrictedIgnoredPageIds.size())).addWarnMessage(BulkStatusReportEnum.WARN_IGNORE_VIEW_RESTRICTED.name(), String.valueOf(this.viewRestrictedIgnoredPageIds.size())).build()));
                return;
            }
            try {
                logProgressMeter.setStatus(this.getStatusString(BulkStatusReport.getBuilder().withMessageKey(BulkStatusReportEnum.STATUS_DELETING_PAGES.name(), new String[0]).build()));
                Space targetSpace = target.getSpace();
                actionedEntities = bulkAction.execute(context, target, new BulkPageDeleteAction(options));
                deletePageProgressMeter.setPercentage(100);
                logProgressMeter.setStatus(this.getStatusString(BulkStatusReport.getBuilder().withMessageKey(BulkStatusReportEnum.STATUS_MOVING_PAGES.name(), new String[0]).build()));
                int totalNumberToMove = this.childToParentMap.size() + this.childToRootList.size();
                movePageProgressMeter.setTotalObjects(totalNumberToMove);
                int moveCounter = 0;
                ListIterator<Map.Entry<Long, Long>> entries = new ArrayList<Map.Entry<Long, Long>>(this.childToParentMap.entrySet()).listIterator(this.childToParentMap.size());
                while (entries.hasPrevious()) {
                    Map.Entry<Long, Long> entry = entries.previous();
                    Page child = this.pageManager.getPage(entry.getKey());
                    Page parent = this.pageManager.getPage(entry.getValue());
                    this.pageManager.movePageAsChild(child, parent);
                    movePageProgressMeter.setPercentage(++moveCounter, totalNumberToMove);
                }
                for (Long pageId : this.childToRootList) {
                    Page page = this.pageManager.getPage(pageId);
                    if (page == null) continue;
                    this.pageManager.movePageToTopLevel(page, targetSpace);
                    movePageProgressMeter.setPercentage(++moveCounter, totalNumberToMove);
                }
                movePageProgressMeter.setPercentage(100);
                logProgressMeter.setCompletedSuccessfully(true);
                if (lock == null) break block10;
            }
            catch (Exception exception) {
                try {
                    this.log.error(exception.getMessage(), (Throwable)exception);
                    String errorMsg = this.getStatusString(BulkStatusReport.getBuilder().withMessageKey(BulkStatusReportEnum.ERROR_UNKNOWN.name(), new String[0]).build());
                    logProgressMeter.setStatus(errorMsg);
                    logProgressMeter.setCompletedSuccessfully(false);
                    throw new RuntimeException(exception);
                }
                catch (Throwable throwable) {
                    if (lock != null) {
                        lock.unlock();
                    }
                    logProgressMeter.setStatus(this.getStatusString(this.statusReportBuilder.withMessageKey(BulkStatusReportEnum.STATUS_DELETED_PAGES.name(), String.valueOf(actionedEntities)).addWarnMessage(BulkStatusReportEnum.WARN_IGNORE_EDIT_RESTRICTED.name(), String.valueOf(this.editRestrictedIgnoredPageIds.size())).addWarnMessage(BulkStatusReportEnum.WARN_IGNORE_VIEW_RESTRICTED.name(), String.valueOf(this.viewRestrictedIgnoredPageIds.size())).build()));
                    throw throwable;
                }
            }
            lock.unlock();
        }
        logProgressMeter.setStatus(this.getStatusString(this.statusReportBuilder.withMessageKey(BulkStatusReportEnum.STATUS_DELETED_PAGES.name(), String.valueOf(actionedEntities)).addWarnMessage(BulkStatusReportEnum.WARN_IGNORE_EDIT_RESTRICTED.name(), String.valueOf(this.editRestrictedIgnoredPageIds.size())).addWarnMessage(BulkStatusReportEnum.WARN_IGNORE_VIEW_RESTRICTED.name(), String.valueOf(this.viewRestrictedIgnoredPageIds.size())).build()));
        return;
    }

    private String getStatusString(BulkStatusReport bulkStatusReport) {
        String message = "";
        try {
            message = this.objectMapper.writeValueAsString((Object)bulkStatusReport);
        }
        catch (IOException ex) {
            this.log.error("Could not report status for bulk delete action due could not serialize JSON status", (Throwable)ex);
        }
        return message;
    }

    private void markForMove(Page page, Page targetParent) {
        if (targetParent == null) {
            this.childToRootList.add(page.getId());
        } else {
            this.childToParentMap.put(page.getId(), targetParent.getId());
        }
    }

    class BulkPageDeleteAction
    extends AbstractBulkPageAction<PageDeleteOptions, BulkPageDeleteExecutionContext> {
        private final Set<Long> pageIds;

        public BulkPageDeleteAction(PageDeleteOptions options) {
            super(options);
            this.pageIds = options.getTargetPageIds();
        }

        @Override
        public BulkAction.Result<BulkPageDeleteExecutionContext, Page> process(BulkPageDeleteExecutionContext context, Page page) {
            long id = page.getId();
            if ((page = DefaultBulkPageDelete.this.pageManager.getPage(id)) == null) {
                DefaultBulkPageDelete.this.log.info("Page {} could not be refreshed. Was it already deleted?", (Object)id);
                return new BulkAction.Result<BulkPageDeleteExecutionContext, Page>(context, Collections.emptyList(), true);
            }
            List children = DefaultBulkPageDelete.this.pageManager.getChildren(page, LimitedRequestImpl.create((int)0x7FFFFFFE), Depth.ROOT).getResults();
            if (this.pageIds.contains(id) && DefaultBulkPageDelete.this.permissionManager.hasPermission((User)context.getUser(), Permission.REMOVE, page)) {
                DefaultBulkPageDelete.this.pageManager.trashPage(page, DefaultDeleteContext.BULK_OPERATION);
                DefaultBulkPageDelete.this.trashedPageIds.add(id);
                return new BulkAction.Result<BulkPageDeleteExecutionContext, Page>(context, children, true);
            }
            if (!this.pageIds.contains(id)) {
                DefaultBulkPageDelete.this.log.info("Ignore out of scope page [{}]", (Object)page);
            } else if (!DefaultBulkPageDelete.this.permissionManager.hasPermission((User)context.getUser(), Permission.VIEW, page)) {
                DefaultBulkPageDelete.this.log.info("No view permission on page [{}]", (Object)page);
                DefaultBulkPageDelete.this.viewRestrictedIgnoredPageIds.add(id);
            } else {
                DefaultBulkPageDelete.this.log.info("No delete permission on page [{}]", (Object)page);
                DefaultBulkPageDelete.this.editRestrictedIgnoredPageIds.add(id);
            }
            DefaultBulkPageDelete.this.markForMove(page, context.getTargetParent());
            return new BulkAction.Result<BulkPageDeleteExecutionContext, Page>(new BulkPageDeleteExecutionContext(context.getProgressMeter(), context.getUser(), page), children, false);
        }

        @Override
        public void report(ProgressMeter innerProgressMeter, int processedEntities, int actionedEntities, int maxProcessedDepthLevel) {
            ProgressMeter progressMeter = ((PageDeleteOptions)this.options).getProgressMeter();
            progressMeter.setStatus(DefaultBulkPageDelete.this.getStatusString(BulkStatusReport.getBuilder().withMessageKey(BulkStatusReportEnum.STATUS_DELETING_PAGES.name(), String.valueOf(actionedEntities), String.valueOf(innerProgressMeter.getTotal())).build()));
        }
    }
}

