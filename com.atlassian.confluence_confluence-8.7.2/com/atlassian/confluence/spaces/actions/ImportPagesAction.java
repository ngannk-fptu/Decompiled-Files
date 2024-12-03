/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.opensymphony.util.FileUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.cluster.ClusterConfigurationHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.opensymphony.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
public class ImportPagesAction
extends AbstractSpaceAdminAction {
    private static final Logger log = LoggerFactory.getLogger(ImportPagesAction.class);
    private static final String PAGE_IMPORT_DIRECTORY = "page-imports";
    private String directory = null;
    private PageManager pageManager;
    private boolean trimExtension;
    private boolean overwriteExisting;
    private ClusterConfigurationHelper clusterConfigurationHelper;

    public PageManager getPageManager() {
        return this.pageManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public boolean isTrimExtension() {
        return this.trimExtension;
    }

    public void setTrimExtension(boolean trimExtension) {
        this.trimExtension = trimExtension;
    }

    public boolean isOverwriteExisting() {
        return this.overwriteExisting;
    }

    public void setOverwriteExisting(boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setClusterConfigurationHelper(ClusterConfigurationHelper clusterConfigurationHelper) {
        this.clusterConfigurationHelper = clusterConfigurationHelper;
    }

    public String getPageImportDirectory() {
        if (this.clusterConfigurationHelper == null) {
            return "";
        }
        Optional<File> possibleSharedHome = this.clusterConfigurationHelper.sharedHome();
        if (possibleSharedHome.isEmpty()) {
            return "";
        }
        return ImportPagesAction.getCanonicalPath(possibleSharedHome.get()) + File.separator + PAGE_IMPORT_DIRECTORY;
    }

    public boolean isValidDirectory() {
        String allowedImportDir = this.getPageImportDirectory();
        if (StringUtils.isBlank((CharSequence)allowedImportDir)) {
            return false;
        }
        File importDir = new File(this.directory);
        String importDirPath = ImportPagesAction.getCanonicalPath(importDir);
        if (StringUtils.isBlank((CharSequence)importDirPath)) {
            return false;
        }
        return importDirPath.startsWith(allowedImportDir) && importDir.exists() && importDir.isDirectory() && importDir.canRead();
    }

    @Override
    public String doDefault() throws Exception {
        if (this.getSpace() == null || this.isPersonalSpace()) {
            return "pagenotfound";
        }
        return super.doDefault();
    }

    public String execute() throws Exception {
        if (this.isPersonalSpace()) {
            return "pagenotfound";
        }
        File dirFile = new File(this.directory);
        File[] files = dirFile.listFiles(File::isFile);
        if (files != null) {
            for (File file : files) {
                try {
                    this.createPage(file);
                }
                catch (Exception e) {
                    log.error("An error occurred importing: " + file, (Throwable)e);
                }
            }
        }
        if (this.hasErrors()) {
            return "error";
        }
        return "success";
    }

    private void createPage(File file) {
        String title = this.getBaseTitle(file);
        String content = FileUtils.readFile((File)file);
        content = content.replaceAll(String.valueOf('\u0000'), "");
        Page page = this.pageManager.getPage(this.key, title);
        if (this.overwriteExisting && page != null) {
            page.setBodyAsString(content);
            this.pageManager.saveContentEntity(page, null);
            return;
        }
        if (page != null) {
            title = this.getNonClashingTitle(title);
        }
        page = new Page();
        page.setTitle(title);
        page.setBodyAsString(content);
        page.setSpace(this.getSpace());
        this.pageManager.saveContentEntity(page, null);
    }

    private String getBaseTitle(File file) {
        int extStart;
        String title = file.getName();
        if (this.isTrimExtension() && (extStart = title.lastIndexOf(46)) > 0) {
            title = title.substring(0, extStart);
        }
        return title;
    }

    private String getNonClashingTitle(String clashingTitle) {
        Page existingPage = this.pageManager.getPage(this.key, clashingTitle);
        Object title = clashingTitle;
        if (existingPage != null) {
            int suffix = 1;
            while (existingPage != null) {
                title = clashingTitle + suffix++;
                existingPage = this.pageManager.getPage(this.key, (String)title);
            }
        }
        return title;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    private static String getCanonicalPath(File path) {
        try {
            return path.getCanonicalPath();
        }
        catch (IOException e) {
            log.warn(String.format("Failed to canonicalize path '%s'", path), (Throwable)e);
            return "";
        }
    }
}

