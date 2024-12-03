/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.persistence.hibernate.CacheMode
 *  com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal
 *  com.atlassian.confluence.importexport.ImportExportManager
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.security.GateKeeper
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.Cleanup
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.PdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.analytic.SpaceExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfExportSemaphore;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

public class PdfExportLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    private TransactionTemplate transactionTemplate;
    private ImportExportManager importExportManager;
    private SpaceManager spaceManager;
    private PdfExporterService flyingPdfExporterService;
    private GateKeeper gateKeeper;
    private final List<String> contentToBeExported;
    private final long spaceId;
    private final User remoteUser;
    private final String contextPath;
    private final PdfExportSemaphore pdfExportSemaphore;
    private final DecorationPolicy decorations;
    private String downloadPath;
    private PermissionManager permissionManager;

    @Deprecated
    public PdfExportLongRunningTask(List<String> contentToBeExported, Space space, User remoteUser, String contextPath, PdfExportSemaphore pdfExportSemaphore, DecorationPolicy decorations) {
        this(contentToBeExported, space, remoteUser, contextPath, pdfExportSemaphore, decorations, (PermissionManager)ContainerManager.getComponent((String)"permissionManager"));
    }

    public PdfExportLongRunningTask(List<String> contentToBeExported, Space space, User remoteUser, String contextPath, PdfExportSemaphore pdfExportSemaphore, DecorationPolicy decorations, PermissionManager permissionManager) {
        this.contentToBeExported = contentToBeExported;
        this.spaceId = space.getId();
        this.remoteUser = remoteUser;
        this.contextPath = contextPath;
        this.pdfExportSemaphore = pdfExportSemaphore;
        this.decorations = decorations;
        this.permissionManager = permissionManager;
    }

    protected void runInternal() {
        try {
            this.pdfExportSemaphore.run(this::doRunInternal);
        }
        catch (RuntimeException e) {
            this.progress.setCompletedSuccessfully(false);
            this.progress.setStatus(e.getMessage());
        }
    }

    private void doRunInternal() {
        try (Cleanup cleanup = SessionCacheModeThreadLocal.temporarilySetCacheMode((CacheMode)CacheMode.IGNORE);){
            this.transactionTemplate.execute(() -> {
                AuthenticatedUserThreadLocal.setUser((User)this.remoteUser);
                RequestCacheThreadLocal.getRequestCache().put("confluence.context.path", this.contextPath);
                PdfExportProgressMonitor monitor = this.flyingPdfExporterService.createProgressMonitor(this.progress);
                try {
                    monitor.beginCalculationOfContentTree();
                    Space space = this.spaceManager.getSpace(this.spaceId);
                    ContentTree contentTree = this.importExportManager.getContentTree(this.remoteUser, space);
                    if (this.contentToBeExported != null && !this.contentToBeExported.isEmpty()) {
                        contentTree.filter(this.contentToBeExported);
                    }
                    monitor.completedCalculationOfContentTree(contentTree.size());
                    File pdfFile = this.flyingPdfExporterService.createPdfForSpace(this.remoteUser, space, contentTree, monitor, this.contextPath, new SpaceExportMetrics(), this.decorations);
                    this.downloadPath = this.importExportManager.prepareDownloadPath(pdfFile.getAbsolutePath());
                    Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission(u, Permission.EXPORT, (Object)space);
                    this.gateKeeper.addKey(this.downloadPath, this.remoteUser, permissionPredicate);
                    this.downloadPath = this.contextPath + this.downloadPath;
                    monitor.completed(this.downloadPath);
                }
                catch (Exception ex) {
                    log.error("Error during PDF export", (Throwable)ex);
                    String exceptionMessage = ex.getMessage();
                    if (StringUtils.isBlank((CharSequence)exceptionMessage)) {
                        exceptionMessage = ex.getClass().getName();
                    }
                    monitor.errored(exceptionMessage);
                }
                return null;
            });
        }
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    public String getName() {
        return "PDF Space Export";
    }

    public String getNameKey() {
        return "com.atlassian.confluence.extra.flyingpdf.exporttaskname";
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public void setImportExportManager(ImportExportManager importExportManager) {
        this.importExportManager = importExportManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setFlyingPdfExporterService(PdfExporterService flyingPdfExporterService) {
        this.flyingPdfExporterService = flyingPdfExporterService;
    }

    public void setGateKeeper(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

