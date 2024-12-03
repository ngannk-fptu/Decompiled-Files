/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.interceptor.ServletRequestAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.actions.ExportSpaceLongRunningTask;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.xmlimport.BackupImporter;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

@Deprecated
public class ExportSpaceAction
extends AbstractSpaceAction
implements ServletContextAware,
ServletRequestAware {
    private static final long serialVersionUID = 1L;
    private static final String DOWNLOAD = "download";
    private static final Logger log = LoggerFactory.getLogger(ExportSpaceAction.class);
    private LongRunningTaskManager longRunningTaskManager;
    private ImportExportManager importExportManager;
    private String exportType;
    private String downloadPath;
    private boolean includeComments;
    private String[] contentToBeExported = new String[0];
    private String[] contentToBeExcluded = new String[0];
    private GateKeeper gateKeeper;
    private ContentTree contentTree;
    private ServletContext servletContext;
    private HttpServletRequest servletRequest;
    private String contentOption;
    private boolean synchronous;
    private PageManager pageManager;
    private LongRunningTaskId taskId;
    private boolean preloadContentTree;
    private transient DarkFeatureManager salDarkFeatureManager;

    public boolean isPreloadContentTree() {
        return this.preloadContentTree;
    }

    public void setPreloadContentTree(boolean preloadContentTree) {
        this.preloadContentTree = preloadContentTree;
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getExportType() {
        return this.exportType;
    }

    public boolean isIncludeComments() {
        return this.includeComments;
    }

    public void setIncludeComments(boolean includeComments) {
        this.includeComments = includeComments;
    }

    public String[] getContentToBeExported() {
        return this.contentToBeExported;
    }

    public void setContentToBeExported(String[] contentToBeExported) {
        this.contentToBeExported = contentToBeExported;
    }

    public String[] getContentToBeExcluded() {
        return this.contentToBeExcluded;
    }

    public void setContentToBeExcluded(String[] contentToBeExcluded) {
        this.contentToBeExcluded = contentToBeExcluded;
    }

    public void setImportExportManager(ImportExportManager importExportManager) {
        this.importExportManager = importExportManager;
    }

    private static Set<Long> extractContentIds(String[] strings) {
        return Arrays.stream(strings).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toSet());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doDefaultHtml() {
        this.exportType = "TYPE_HTML";
        return this.doDefault();
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doDefaultXml() {
        this.exportType = "TYPE_XML";
        return this.doDefault();
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doDefaultPdf() {
        this.exportType = "TYPE_PDF";
        return this.doDefault();
    }

    @Override
    public String doDefault() {
        this.includeComments = true;
        return "input";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doDefaultContentTree() {
        return "success";
    }

    private boolean isLongRunningTaskSupported() {
        String runningServer = this.getServletContext().getServerInfo().toLowerCase();
        String unsupportedContainers = this.getServletContext().getInitParameter("unsupportedContainersForExportLongRunningTask");
        if (StringUtils.isNotBlank((CharSequence)unsupportedContainers)) {
            String[] containers = unsupportedContainers.split(",");
            for (int i = 0; i < containers.length; ++i) {
                String container = StringUtils.trim((String)containers[i]);
                if (!runningServer.contains(container)) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public void validate() {
        super.validate();
        if ("TYPE_XML".equals(this.exportType) && "all".equals(this.contentOption) && !this.isSpaceAdminOrConfAdmin()) {
            this.addActionError(this.getText("export.space.validation.insufficient.privileges.export.all.content"));
        }
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        DefaultExportContext exportContext = new DefaultExportContext();
        exportContext.setExportScope(ExportScope.SPACE);
        exportContext.setType(this.getExportType());
        exportContext.setExportComments(this.isIncludeComments());
        exportContext.setExportAttachments(true);
        exportContext.setUser(this.getAuthenticatedUser());
        exportContext.setSpaceKey(this.space.getKey());
        ExportSpaceLongRunningTask task = new ExportSpaceLongRunningTask(this.getAuthenticatedUser(), this.servletRequest.getContextPath(), exportContext, ExportSpaceAction.extractContentIds(this.contentToBeExported), ExportSpaceAction.extractContentIds(this.contentToBeExcluded), this.gateKeeper, this.importExportManager, this.permissionManager, this.spaceManager, this.getSpaceKey(), this.exportType, this.contentOption);
        if (this.isLongRunningTaskSupported() && !this.isSynchronous()) {
            this.taskId = this.longRunningTaskManager.startLongRunningTask(this.getAuthenticatedUser(), (LongRunningTask)task);
            log.info("Started log-running task {} for export of space {}", (Object)this.taskId, (Object)this.getSpaceKey());
            return "success";
        }
        log.info("Starting synchronous export of space {}; long-running tasks not enabled", (Object)this.getSpaceKey());
        task.run();
        this.downloadPath = task.getDownloadPath();
        return DOWNLOAD;
    }

    public boolean isSpaceAdminOrConfAdmin() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, this.space) || this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public ServletContext getServletContext() {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        return ServletActionContext.getServletContext();
    }

    public ContentTree getContentTree() {
        if (this.contentTree == null) {
            this.contentTree = "TYPE_XML".equals(this.exportType) ? this.importExportManager.getPageBlogTree(this.getAuthenticatedUser(), this.getSpace()) : this.importExportManager.getContentTree(this.getAuthenticatedUser(), this.getSpace());
        }
        return this.contentTree;
    }

    protected List getPermissionTypes() {
        List<String> permissionTypes = super.getPermissionTypes();
        this.addPermissionTypeTo("EXPORTSPACE", permissionTypes);
        if (permissionTypes.contains("EDITSPACE")) {
            permissionTypes.remove("EDITSPACE");
        }
        return permissionTypes;
    }

    public String getTaskId() {
        return this.taskId.toString();
    }

    @Override
    public boolean isPermitted() {
        return super.isPermitted();
    }

    public void setGateKeeper(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getContentOption() {
        return this.contentOption;
    }

    public void setContentOption(String contentOption) {
        this.contentOption = contentOption;
    }

    public boolean isSynchronous() {
        return this.synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public boolean spaceHasPages() {
        return this.pageManager.getPages(this.space, false).size() > 0;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setLongRunningTaskManager(LongRunningTaskManager longRunningTaskManager) {
        this.longRunningTaskManager = longRunningTaskManager;
    }

    public String getSpaceExportBackwardsCompatibility() {
        return BackupImporter.SPACE_EXPORT_BACKWARDS_COMPATIBILITY.getVersion();
    }

    public BuildInformation getBuildInformation() {
        return BuildInformation.INSTANCE;
    }

    public void setSalDarkFeatureManager(DarkFeatureManager salDarkFeatureManager) {
        this.salDarkFeatureManager = salDarkFeatureManager;
    }

    public boolean isCustomExportAllowed() {
        return true;
    }

    private boolean isFastBackupEnabled() {
        return false;
    }
}

