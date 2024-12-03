/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.FileUploadUtils$FileUploadException
 *  com.atlassian.xwork.FileUploadUtils$UploadedFile
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupContainerReader;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupContainerReaderFactory;
import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.actions.AbstractBackupRestoreAction;
import com.atlassian.confluence.importexport.actions.ImportLongRunningTask;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.impl.UnexpectedImportZipFileContents;
import com.atlassian.confluence.internal.longrunning.LongRunningTaskManagerInternal;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.util.StrutsUtil;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskUtils;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.xwork.FileUploadUtils;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class AbstractImportAction
extends AbstractBackupRestoreAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractImportAction.class);
    public static final String RESTORE_DIR = "restore";
    private String localFileName;
    private boolean buildIndex = false;
    private EventPublisher eventPublisher;
    private IndexManager indexManager;
    private DarkFeatureManager salDarkFeatureManager;
    private String taskId;
    private File uploadedFileCopy;
    private LongRunningTaskManagerInternal longRunningTaskManager;
    private BackupContainerReaderFactory backupContainerReaderFactory;

    public String execute() throws Exception {
        try {
            ExportDescriptor exportDescriptor = ExportDescriptor.getExportDescriptor(this.getRestoreFile());
            if (this.isImportAllowed(exportDescriptor)) {
                this.doRestore(exportDescriptor);
            }
        }
        catch (ImportExportException iee) {
            this.addActionError(iee.getMessage());
            log.debug(iee.getMessage(), (Throwable)iee);
        }
        catch (UnexpectedImportZipFileContents contentsException) {
            this.addActionError(this.getI18n().getText(contentsException.getI18nMessage()));
        }
        if (this.getActionErrors().size() > 0) {
            return "error";
        }
        return "success";
    }

    private Properties getBackupPropertiesDirectlyFromZipFile(File backupFile) throws BackupRestoreException {
        try (BackupContainerReader containerReader = this.backupContainerReaderFactory.createBackupContainerReader(backupFile);){
            Properties properties = containerReader.getLegacyBackupProperties();
            return properties;
        }
    }

    public final LongRunningTask getTask() {
        LongRunningTask task = StringUtils.isNotBlank((CharSequence)this.taskId) ? this.longRunningTaskManager.getLongRunningTask(this.getAuthenticatedUser(), LongRunningTaskId.valueOf(this.taskId)) : LongRunningTaskUtils.retrieveTask();
        return task;
    }

    private void doRestore(ExportDescriptor exportDescriptor) throws ImportExportException, UnexpectedImportZipFileContents {
        DefaultImportContext context = this.createImportContext(exportDescriptor);
        context.setRebuildIndex(this.isBuildIndex());
        LongRunningTask task = this.createImportTask(context);
        if (this.isSynchronous()) {
            task.run();
        } else {
            this.taskId = this.shouldDeferStart(exportDescriptor) ? this.longRunningTaskManager.queueLongRunningTask(task).toString() : LongRunningTaskUtils.startTask(task, null);
        }
    }

    protected LongRunningTask createImportTask(DefaultImportContext context) {
        return new ImportLongRunningTask(this.eventPublisher, this.indexManager, this.getImportExportManager(), context, () -> this.salDarkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false) == false);
    }

    private boolean shouldDeferStart(ExportDescriptor exportDescriptor) {
        try {
            return exportDescriptor.getScope() == ExportScope.ALL;
        }
        catch (ExportScope.IllegalExportScopeException e) {
            return false;
        }
    }

    protected File getRestoreFileFromUpload() throws ImportExportException {
        log.debug("uploadedFileCopy = {}", (Object)this.uploadedFileCopy);
        if (this.uploadedFileCopy != null) {
            return this.uploadedFileCopy;
        }
        try {
            FileUploadUtils.UploadedFile uploadedFile = FileUploadUtils.getSingleUploadedFile();
            if (uploadedFile == null || uploadedFile.getFile() == null) {
                throw new ImportExportException("No files uploaded.");
            }
            this.uploadedFileCopy = this.moveUploadedFile(uploadedFile, uploadedFile.getFile());
            return this.uploadedFileCopy;
        }
        catch (FileUploadUtils.FileUploadException e) {
            StrutsUtil.localizeMultipartErrorMessages(e).forEach(arg_0 -> ((AbstractImportAction)this).addActionError(arg_0));
            throw new ImportExportException("Error uploading file.");
        }
    }

    private File moveUploadedFile(FileUploadUtils.UploadedFile uploadedFile, File file) throws ImportExportException {
        try {
            File targetDir = new File(file.getParent(), file.getName().replace(".tmp", ""));
            File targetFile = new File(targetDir, uploadedFile.getFileName());
            log.debug("moving uploaded file {}, to {}", (Object)file, (Object)targetFile);
            FileUtils.moveFile((File)file, (File)targetFile);
            if (!targetFile.isFile()) {
                throw new ImportExportException("Failed to move uploaded file");
            }
            return targetFile;
        }
        catch (IOException e) {
            throw new ImportExportException("Error moving uploaded file " + file, e);
        }
    }

    protected File getRestoreFileFromFileSystem() throws ImportExportException {
        if (!StringUtils.isNotEmpty((CharSequence)this.getLocalFileName())) {
            throw new ImportExportException("No local file specified");
        }
        File restoreDirectory = new File(this.getConfluenceHome(), RESTORE_DIR);
        if (restoreDirectory.isDirectory()) {
            File localRestoreFile = new File(restoreDirectory, this.getLocalFileName());
            if (localRestoreFile.isFile()) {
                return localRestoreFile;
            }
            throw new ImportExportException(this.getLocalFileName() + " does not exist in your restore directory.");
        }
        throw new ImportExportException("Restore directory doesn't exist. You need to create a directory called 'restore' in your Confluence home folder and then copy your restore file there");
    }

    protected boolean isImportAllowed(ExportDescriptor exportDescriptor) throws ImportExportException, UnexpectedImportZipFileContents {
        return true;
    }

    public String getLocalFileName() {
        return this.localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public boolean isBuildIndex() {
        return this.buildIndex;
    }

    public void setBuildIndex(boolean buildIndex) {
        this.buildIndex = buildIndex;
    }

    public final void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setSalDarkFeatureManager(DarkFeatureManager salDarkFeatureManager) {
        this.salDarkFeatureManager = salDarkFeatureManager;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public void setLongRunningTaskManager(LongRunningTaskManagerInternal longRunningTaskManager) {
        this.longRunningTaskManager = longRunningTaskManager;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    protected IndexManager getIndexManager() {
        return this.indexManager;
    }

    protected abstract DefaultImportContext createImportContext(ExportDescriptor var1) throws ImportExportException, UnexpectedImportZipFileContents;

    protected abstract File getRestoreFile() throws ImportExportException;

    public void setBackupContainerReaderFactory(BackupContainerReaderFactory backupContainerReaderFactory) {
        this.backupContainerReaderFactory = backupContainerReaderFactory;
    }
}

