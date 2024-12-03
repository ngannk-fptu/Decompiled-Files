/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.event.events.admin.AsyncExportFinishedEvent;
import com.atlassian.confluence.event.events.admin.ExportFinishedEvent;
import com.atlassian.confluence.importexport.DefaultImmutableImportProcessorSummary;
import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.Exporter;
import com.atlassian.confluence.importexport.ImmutableImportProcessorSummary;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.ImportProcessorSummary;
import com.atlassian.confluence.importexport.PostImportTask;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.xmlimport.BackupImporter;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.TreeBuilder;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.upgrade.BuildNumber;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DefaultImportExportManager
implements ImportExportManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultImportExportManager.class);
    public static final String EXPORT_ALL_FILE_PREFIX = "export-";
    private static final List<String> IMPORT_EXPORT_TYPES = ImmutableList.of((Object)"TYPE_ALL_DATA", (Object)"TYPE_XML", (Object)"TYPE_HTML", (Object)"TYPE_MOINMOIN", (Object)ExportScope.PAGE.getString(), (Object)ExportScope.SPACE.getString(), (Object)ExportScope.ALL.getString());
    @Deprecated
    public static final ArrayList<String> importExportTypes = new ArrayList<String>(IMPORT_EXPORT_TYPES);
    private final ContentPermissionManager contentPermissionManager;
    private final BootstrapManager bootstrapManager;
    private final PageManager pageManager;
    private final EventPublisher eventPublisher;
    private final UpgradeManager upgradeManager;

    public DefaultImportExportManager(ContentPermissionManager contentPermissionManager, BootstrapManager bootstrapManager, PageManager pageManager, EventPublisher eventPublisher, UpgradeManager upgradeManager, PluginController pluginController, PluginAccessor pluginAccessor) {
        this.contentPermissionManager = contentPermissionManager;
        this.bootstrapManager = bootstrapManager;
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
        this.upgradeManager = upgradeManager;
    }

    @Override
    public void doImport(ImportContext context) throws ImportExportException {
        try (Cleanup cleanup = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            this.performImportInternal(context);
        }
    }

    @Override
    public ImmutableImportProcessorSummary performImport(ImportContext context) throws ImportExportException {
        try (Cleanup cleanup = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            ImmutableImportProcessorSummary immutableImportProcessorSummary = this.doPerformImport(context);
            return immutableImportProcessorSummary;
        }
    }

    protected ImmutableImportProcessorSummary doPerformImport(ImportContext context) throws ImportExportException {
        ImportProcessorSummary importProcessorSummary = this.performImportInternal(context);
        return DefaultImmutableImportProcessorSummary.newInstance(importProcessorSummary);
    }

    private ImportProcessorSummary performImportInternal(ImportContext context) throws ImportExportException {
        ContainerContext containerContext = ContainerManager.getInstance().getContainerContext();
        Cleanup interferingPluginsTask = this.temporarilyShutdownInterferingPlugins(context);
        BackupImporter backupImporter = (BackupImporter)containerContext.getComponent((Object)"backupImporter");
        backupImporter.setContext(this.addPostImportTasks(context, context1 -> interferingPluginsTask.close()));
        return backupImporter.doImport();
    }

    private ImportContext addPostImportTasks(ImportContext context, PostImportTask ... tasks) {
        context.setPostImportTasks(Lists.newArrayList((Iterable)Iterables.concat(context.getPostImportTasks(), Arrays.asList(tasks))));
        return context;
    }

    @VisibleForTesting
    protected final Cleanup temporarilyShutdownInterferingPlugins(ImportContext ignored) {
        return () -> {};
    }

    @Override
    public String exportAs(ExportContext context, ProgressMeter progress) throws ImportExportException {
        try (Cleanup cleanup = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            String string = this.doExport(context, progress);
            return string;
        }
    }

    protected String doExport(ExportContext context, ProgressMeter progress) throws ImportExportException {
        Exporter exporter;
        String type = context.getType();
        if (type == null || !type.equals("TYPE_HTML") && !type.equals("TYPE_XML") && !type.equals("TYPE_ALL_DATA")) {
            throw new IllegalArgumentException("Type must be TYPE_HTML, TYPE_XML or TYPE_ALL_DATA!");
        }
        ContainerContext containerContext = ContainerManager.getInstance().getContainerContext();
        switch (type) {
            case "TYPE_HTML": {
                exporter = (Exporter)containerContext.getComponent((Object)"htmlExporter");
                break;
            }
            case "TYPE_XML": {
                exporter = (Exporter)containerContext.getComponent((Object)"xmlExporter");
                break;
            }
            case "TYPE_ALL_DATA": {
                exporter = (Exporter)containerContext.getComponent((Object)"backupExporter");
                break;
            }
            default: {
                throw new ImportExportException("Unrecognised export type: " + type);
            }
        }
        if (exporter == null) {
            throw new ImportExportException("Exporter missing for type: " + type);
        }
        exporter.setContext(context);
        String pathToExport = exporter.doExport(progress);
        this.eventPublisher.publish((Object)new ExportFinishedEvent(this, context.getType(), context.getExportScope().getString(), context.getSpaceKeyOfSpaceExport()));
        this.eventPublisher.publish((Object)new AsyncExportFinishedEvent(this, context.getType(), context.getExportScope().getString(), context.getSpaceKeyOfSpaceExport()));
        return pathToExport;
    }

    @Override
    public List getImportExportTypeSpecifications() {
        return IMPORT_EXPORT_TYPES;
    }

    @Override
    public ContentTree getContentTree(User user, Space space) {
        TreeBuilder treeBuilder = new TreeBuilder(user, this.contentPermissionManager, this.pageManager);
        return treeBuilder.createPageTree(space);
    }

    @Override
    public ContentTree getPageBlogTree(User user, Space space) {
        TreeBuilder treeBuilder = new TreeBuilder(user, this.contentPermissionManager, this.pageManager);
        return treeBuilder.createPageBlogTree(space);
    }

    @Override
    public String prepareDownloadPath(String path) throws IOException {
        String exportDir = this.bootstrapManager.getFilePathProperty("struts.multipart.saveDir");
        String canonicalPath = new File(path).getCanonicalPath();
        int exportDirIndex = canonicalPath.indexOf(exportDir);
        Object result = path;
        if (exportDirIndex != -1) {
            result = canonicalPath.substring(exportDirIndex + exportDir.length());
        } else {
            log.error("Path is not in export temp directory.");
        }
        result = "/download/export" + (String)result;
        result = ((String)result).replaceAll("\\".equals(File.separator) ? "\\\\" : File.separator, "/");
        return result;
    }

    @Override
    public boolean isImportAllowed(String buildNumber) {
        return BackupImporter.isBackupSupportedVersion(buildNumber);
    }

    @Override
    public BuildNumber getOldestSpaceImportAllowed() {
        return new BuildNumber(this.upgradeManager.getOldestSpaceImportAllowed());
    }

    @Deprecated
    public ContentPermissionManager getContentPermissionManager() {
        return this.contentPermissionManager;
    }
}

