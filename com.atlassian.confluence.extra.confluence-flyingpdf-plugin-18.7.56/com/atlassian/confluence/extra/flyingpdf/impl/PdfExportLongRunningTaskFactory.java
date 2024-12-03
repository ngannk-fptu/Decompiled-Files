/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportManager
 *  com.atlassian.confluence.importexport.impl.ExportFileNameGenerator
 *  com.atlassian.confluence.security.GateKeeper
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.impl.BigBrotherPdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.impl.ContentTreeLongRunningTask;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfExportLongRunningTask;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfExportSemaphore;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PdfExportLongRunningTaskFactory {
    private final GateKeeper gateKeeper;
    private final SpaceManager spaceManager;
    private final PdfExportSemaphore pdfExportSemaphore;
    private final ImportExportManager importExportManager;
    private final TransactionTemplate transactionTemplate;
    private final ExportFileNameGenerator pdfExportFileNameGenerator;
    private final BigBrotherPdfExporterService pdfExporterService;
    private final PermissionManager permissionManager;

    public PdfExportLongRunningTaskFactory(@ComponentImport GateKeeper gateKeeper, @ComponentImport SpaceManager spaceManager, @ComponentImport ImportExportManager importExportManager, @ComponentImport TransactionTemplate transactionTemplate, PdfExportSemaphore pdfExportSemaphore, ExportFileNameGenerator pdfExportFileNameGenerator, BigBrotherPdfExporterService pdfExporterService, @ComponentImport PermissionManager permissionManager) {
        this.gateKeeper = gateKeeper;
        this.spaceManager = spaceManager;
        this.pdfExportSemaphore = pdfExportSemaphore;
        this.importExportManager = importExportManager;
        this.transactionTemplate = transactionTemplate;
        this.pdfExportFileNameGenerator = pdfExportFileNameGenerator;
        this.pdfExporterService = pdfExporterService;
        this.permissionManager = permissionManager;
    }

    public PdfExportLongRunningTask createNewLongRunningTask(List<String> contentToBeExported, Space space, User remoteUser, String contextPath, DecorationPolicy decorations) {
        PdfExportLongRunningTask task = new PdfExportLongRunningTask(contentToBeExported, space, remoteUser, contextPath, this.pdfExportSemaphore, decorations, this.permissionManager);
        task.setImportExportManager(this.importExportManager);
        task.setSpaceManager(this.spaceManager);
        task.setFlyingPdfExporterService(this.pdfExporterService);
        task.setTransactionTemplate(this.transactionTemplate);
        task.setGateKeeper(this.gateKeeper);
        task.setPermissionManager(this.permissionManager);
        return task;
    }

    public ContentTreeLongRunningTask createNewContentTreeLongRunningTask(I18NBean i18NBean, Space space, User remoteUser, String contextPath) {
        return new ContentTreeLongRunningTask(this.importExportManager, this.transactionTemplate, i18NBean, space, remoteUser, this.gateKeeper, contextPath, this.pdfExportFileNameGenerator, this.pdfExportSemaphore, this.permissionManager);
    }
}

