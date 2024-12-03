/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.persistence.hibernate.CacheMode
 *  com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal
 *  com.atlassian.confluence.importexport.ImportExportManager
 *  com.atlassian.confluence.importexport.impl.ExportFileNameGenerator
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.security.GateKeeper
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.Cleanup
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfExportSemaphore;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.Context;

public class ContentTreeLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    private final ImportExportManager importExportManager;
    private final TransactionTemplate transactionTemplate;
    private final I18NBean i18NBean;
    private final Space space;
    private final User user;
    private final GateKeeper gateKeeper;
    private final String contextPath;
    private final ExportFileNameGenerator pdfExportFileNameGenerator;
    private final PdfExportSemaphore pdfExportSemaphore;
    private String downloadPath;
    private PermissionManager permissionManager;

    public ContentTreeLongRunningTask(ImportExportManager importExportManager, TransactionTemplate transactionTemplate, I18NBean i18NBean, Space space, User user, GateKeeper gateKeeper, String contextPath, ExportFileNameGenerator pdfExportFileNameGenerator, PdfExportSemaphore pdfExportSemaphore, PermissionManager permissionManager) {
        this.importExportManager = importExportManager;
        this.transactionTemplate = transactionTemplate;
        this.i18NBean = i18NBean;
        this.space = space;
        this.user = user;
        this.gateKeeper = gateKeeper;
        this.contextPath = contextPath;
        this.pdfExportFileNameGenerator = pdfExportFileNameGenerator;
        this.pdfExportSemaphore = pdfExportSemaphore;
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
                try {
                    this.initProgress();
                    ContentTree contentTree = this.importExportManager.getContentTree(this.user, this.space);
                    Context context = MacroUtils.createDefaultVelocityContext();
                    context.put("contentTree", (Object)contentTree);
                    File file = this.pdfExportFileNameGenerator.getExportFile(new String[]{"confluence.extra.content-tree-builder-"});
                    try (OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8);){
                        VelocityUtils.writeRenderedTemplate((Writer)writer, (String)"/templates/extra/pdfexport/export-space-common-tree.vm", (Context)context);
                    }
                    this.downloadPath = this.importExportManager.prepareDownloadPath(file.getAbsolutePath());
                    Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission(u, Permission.EXPORT, (Object)this.space);
                    this.gateKeeper.addKey(this.downloadPath, this.user, permissionPredicate);
                    this.downloadPath = this.contextPath + this.downloadPath;
                    this.updateProgress(this.downloadPath);
                }
                catch (Exception e) {
                    log.error("Error during building content tree for PDF export", (Throwable)e);
                    this.updateProgress(e);
                }
                return null;
            });
        }
    }

    private void updateProgress(Exception e) {
        String exceptionMessage = e.getMessage();
        if (StringUtils.isBlank((CharSequence)exceptionMessage)) {
            exceptionMessage = e.getClass().getName();
        }
        this.progress.setPercentage(100);
        this.progress.setCompletedSuccessfully(false);
        this.progress.setStatus(this.i18NBean.getText("com.atlassian.confluence.extra.flyingpdf.progress.contenttreeerrored", Collections.singletonList(exceptionMessage)));
    }

    private void updateProgress(String downloadPath) {
        this.progress.setPercentage(100);
        this.progress.setCompletedSuccessfully(true);
        this.progress.setStatus(downloadPath);
    }

    private void initProgress() {
        this.progress.setPercentage(0);
        this.progress.setStatus("");
    }

    public String getName() {
        return "Content tree build for PDF export";
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }
}

