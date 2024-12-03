/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskId
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskManager
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.interceptor.ServletRequestAware
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.PdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.impl.BigBrotherPdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfExportLongRunningTask;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfExportLongRunningTaskFactory;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.user.User;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

public class BetterExportSpaceAction
extends AbstractSpaceAction
implements ServletRequestAware {
    public static final String CONTENT_OPTION_CUSTOM_EXPORT = "visible";
    protected HttpServletRequest servletRequest;
    protected PdfExportLongRunningTaskFactory pdfExportLongRunningTaskFactory;
    protected PdfExporterService pdfExporterService;
    protected LongRunningTaskId taskId;
    protected LongRunningTaskManager longRunningTaskManager;
    protected String downloadPath;
    private List<String> contentToBeExported;
    private String contentOption;
    private boolean addPageNumbers;
    private ServletContext servletContext;
    private PermissionManager permissionManager;

    public String doExport() {
        List<String> contentToBeExported = StringUtils.equals((CharSequence)this.contentOption, (CharSequence)CONTENT_OPTION_CUSTOM_EXPORT) ? this.getContentToBeExported() : null;
        PdfExportLongRunningTask task = this.pdfExportLongRunningTaskFactory.createNewLongRunningTask(contentToBeExported, this.getSpace(), (User)this.getAuthenticatedUser(), this.servletRequest.getContextPath(), this.getDecorations());
        this.taskId = this.longRunningTaskManager.startLongRunningTask((User)this.getAuthenticatedUser(), (LongRunningTask)task);
        return "success";
    }

    public String execute() {
        return this.doExport();
    }

    public boolean isPermitted() {
        UtilTimerStack.push((String)"BetterExportSpaceAction.isPermitted");
        boolean isPermitted = this.pdfExporterService.isPermitted(this.getRemoteUser(), this.getSpace());
        UtilTimerStack.pop((String)"BetterExportSpaceAction.isPermitted");
        return isPermitted;
    }

    public boolean exportableContentExists() {
        UtilTimerStack.push((String)"BetterExportSpaceAction.exportableContentExists");
        boolean containsStuff = this.pdfExporterService.exportableContentExists(this.getSpace());
        UtilTimerStack.pop((String)"BetterExportSpaceAction.exportableContentExists");
        return containsStuff;
    }

    private DecorationPolicy getDecorations() {
        return this.addPageNumbers ? DecorationPolicy.pageNumbers() : DecorationPolicy.none();
    }

    public String getSpaceAdvancedUrl() {
        return "/spaces/viewspacesummary.action?key=" + this.getHelper().getSpaceKey();
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    public List<String> getContentToBeExported() {
        return this.contentToBeExported;
    }

    public void setContentToBeExported(List<String> contentToBeExported) {
        this.contentToBeExported = contentToBeExported;
    }

    public void setContentOption(String contentOption) {
        this.contentOption = contentOption;
    }

    public void setAddPageNumbers(boolean addPageNumbers) {
        this.addPageNumbers = addPageNumbers;
    }

    public boolean isSpaceAdmin() {
        return this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, (Object)this.getSpace());
    }

    public ServletContext getServletContext() {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        return ServletActionContext.getServletContext();
    }

    public LongRunningTaskId getTaskId() {
        return this.taskId;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setPdfExportLongRunningTaskFactory(PdfExportLongRunningTaskFactory pdfExportLongRunningTaskFactory) {
        this.pdfExportLongRunningTaskFactory = pdfExportLongRunningTaskFactory;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public void setDiagnosticsPdfExporterService(BigBrotherPdfExporterService diagnosticsPdfExporterService) {
        this.pdfExporterService = diagnosticsPdfExporterService;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setLongRunningTaskManager(LongRunningTaskManager longRunningTaskManager) {
        this.longRunningTaskManager = longRunningTaskManager;
    }
}

