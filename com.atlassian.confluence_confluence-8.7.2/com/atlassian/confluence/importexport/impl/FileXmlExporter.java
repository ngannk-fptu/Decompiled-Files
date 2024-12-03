/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.impl.importexport.AbstractXmlExporter;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.impl.HibernateObjectHandleTranslator;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProviderManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDaoFactory;
import com.atlassian.confluence.pages.persistence.dao.FileSystemAttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.SimpleFileLocationResolver;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.core.util.FileUtils;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.spring.container.ContainerManager;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class FileXmlExporter
extends AbstractXmlExporter {
    private static final Logger log = LoggerFactory.getLogger(FileXmlExporter.class);
    protected AttachmentManager attachmentManager;
    private FilesystemPath confluenceHome;
    private BackupRestoreProviderManager backupRestoreProviderManager;

    @Override
    public String doExport(ProgressMeter progress) throws ImportExportException {
        try (Cleanup cleanup = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            String string = this.doExportInternal(progress);
            return string;
        }
    }

    protected String doExportInternal(ProgressMeter progress) throws ImportExportException {
        HibernateObjectHandleTranslator translator = new HibernateObjectHandleTranslator(this.sessionFactory5.getCurrentSession());
        super.doExport(translator, progress);
        progress.setStatus("Zipping export files.");
        String zipFileName = null;
        if (this.getWorkingExportContext() != null && this.getWorkingExportContext().getWorkingEntities().size() > 0) {
            ConfluenceEntityObject firstEntity = this.getWorkingExportContext().getWorkingEntities().get(0);
            zipFileName = this.prepareExportFileName(firstEntity);
        } else {
            zipFileName = this.getWorkingExportContext().getExportDirectory().getName();
        }
        String archivePath = this.bootstrapManager.getFilePathProperty("struts.multipart.saveDir") + File.separator + zipFileName + ".zip";
        try {
            File zippedExport = new File(archivePath);
            FileUtils.createZipFile((File)this.getWorkingExportContext().getExportDirectory(), (File)zippedExport);
            FileUtils.deleteDir((File)this.getWorkingExportContext().getExportDirectory());
            return archivePath;
        }
        catch (Exception e) {
            throw new ImportExportException(e);
        }
    }

    @Override
    protected void backupEverything(HibernateObjectHandleTranslator translator, ProgressMeter progress) throws ImportExportException {
        super.backupEverything(translator, progress);
        if (this.getContext().isExportAttachments()) {
            this.backupAttachments();
        }
        this.backupTemplates();
        if (ExportScope.ALL == this.getExportScope()) {
            this.backupConfigFiles();
            this.backupResources();
            this.backupPluginData();
        }
    }

    protected abstract List<Space> getIncludedSpaces();

    protected void backupAttachments() throws ImportExportException {
        AttachmentDaoFactory attachmentDaoFactory = (AttachmentDaoFactory)ContainerManager.getComponent((String)"attachmentDaoFactory");
        File attachmentDir = new File(this.getWorkingExportContext().getExportDirectory(), "attachments");
        this.ensureDirectoryCreated(attachmentDir);
        SimpleFileLocationResolver locationResolver = new SimpleFileLocationResolver(attachmentDir);
        FileSystemAttachmentDataDao exportDataDao = new FileSystemAttachmentDataDao();
        exportDataDao.setAttachmentsDirResolver(locationResolver);
        AttachmentDao exportDao = attachmentDaoFactory.getInstance(exportDataDao);
        AttachmentDao sourceDao = this.attachmentManager.getAttachmentDao();
        AttachmentDao.AttachmentCopier copier = sourceDao.getCopier(exportDao);
        copier.setParentContentToExclude(this.getObjectsExcludedFromExport());
        copier.setSpacesToInclude(this.getIncludedSpaces());
        copier.copy();
    }

    protected void backupResources() throws ImportExportException {
        File resourceDir = new File(this.getWorkingExportContext().getExportDirectory(), "resources");
        this.ensureDirectoryCreated(resourceDir);
        try {
            if (this.getExportScope() == ExportScope.ALL) {
                File baseResourceDir = new File(this.getConfluenceHome(), "resources");
                FileUtils.copyDirectory((File)baseResourceDir, (File)resourceDir);
            } else if (this.getExportScope() == ExportScope.SPACE) {
                List<File> baseResourceDirs = this.getEntityResourceDirectories();
                this.copyDirectories(baseResourceDirs, resourceDir);
            }
        }
        catch (IOException e) {
            log.error("Couldn't backup attachments.", (Throwable)e);
            throw new ImportExportException("Couldn't backup attachments.", e);
        }
    }

    private File getConfluenceHome() {
        if (this.confluenceHome != null) {
            return this.confluenceHome.asJavaFile();
        }
        return new File(this.bootstrapManager.getConfluenceHome());
    }

    protected void backupTemplates() throws ImportExportException {
        File templatesDir = this.getWorkingExportContext().getExportDirectory();
        this.ensureDirectoryCreated(templatesDir);
        try {
            List templateDirs = this.getSourceTemplateDirForCopying();
            this.copyDirectories(templateDirs, templatesDir);
        }
        catch (IOException e) {
            log.error("Couldn't backup template directory", (Throwable)e);
            throw new ImportExportException("Couldn't backup templates directory");
        }
    }

    protected void backupConfigFiles() throws ImportExportException {
        File configDirectory = this.getWorkingExportContext().getExportDirectory();
        File currentConfigDirectory = new File(this.bootstrapManager.getLocalHome(), "config");
        if (currentConfigDirectory.exists()) {
            ArrayList<File> currentConfigDirAsList = new ArrayList<File>();
            currentConfigDirAsList.add(currentConfigDirectory);
            try {
                this.copyDirectories(currentConfigDirAsList, configDirectory);
            }
            catch (IOException e) {
                log.error("Error copying config directory over to backup. Directory: " + currentConfigDirectory, (Throwable)e);
            }
        }
    }

    protected void backupPluginData() throws ImportExportException {
        File baseExportDir = this.getWorkingExportContext().getExportDirectory();
        for (ModuleDescriptor moduleDescriptor : this.backupRestoreProviderManager.getModuleDescriptors()) {
            try {
                File pluginDataFile = this.backupRestoreProviderManager.createModuleBackupFile(baseExportDir, (ModuleDescriptor<BackupRestoreProvider>)moduleDescriptor);
                try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(pluginDataFile));){
                    ((BackupRestoreProvider)moduleDescriptor.getModule()).backup(os);
                }
            }
            catch (IOException io) {
                throw new ImportExportException("IOException whilst exporting plugin data for : " + moduleDescriptor.getCompleteKey(), io);
            }
        }
    }

    private void copyDirectories(List sourceDirs, File targetDir) throws IOException {
        for (int i = 0; i < sourceDirs.size(); ++i) {
            File sourceDir = (File)sourceDirs.get(i);
            File destDir = new File(targetDir, sourceDir.getName());
            if (!sourceDir.isDirectory()) continue;
            FileUtils.copyDirectory((File)sourceDir, (File)destDir);
        }
    }

    private List<File> getEntityResourceDirectories() {
        File resourcePath = new File(this.getConfluenceHome(), "resources");
        ArrayList<File> entityResourceDirs = new ArrayList<File>();
        for (ConfluenceEntityObject o : this.context.getWorkingEntities()) {
            File spaceResourceDir;
            if (!(o instanceof Space) || !(spaceResourceDir = new File(resourcePath, ((Space)o).getKey())).exists()) continue;
            entityResourceDirs.add(spaceResourceDir);
        }
        return entityResourceDirs;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setBackupRestoreProviderManager(BackupRestoreProviderManager backupRestoreProviderManager) {
        this.backupRestoreProviderManager = backupRestoreProviderManager;
    }

    public void setConfluenceHome(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }
}

