/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.KeyInitPostImportTask;
import com.atlassian.confluence.importexport.actions.AbstractImportAction;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.impl.UnexpectedImportZipFileContents;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.upgrade.BuildNumber;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.spring.container.ContainerManager;
import java.io.File;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class AbstractFileRestoreAction
extends AbstractImportAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractFileRestoreAction.class);
    private SpaceManager spaceManager;
    private DocumentationBeanFactory docBeanFactory;

    @Override
    protected boolean isImportAllowed(ExportDescriptor exportDescriptor) throws ImportExportException, UnexpectedImportZipFileContents {
        BuildNumber exportCreatedByBuildNumber;
        ExportScope exportScope;
        if (this.isZduEnabled()) {
            this.addActionError(this.getText("restore.unavailable.zdu"));
            return false;
        }
        try {
            exportScope = exportDescriptor.getScope();
        }
        catch (ExportScope.IllegalExportScopeException e) {
            this.addActionError(this.getText("error.could.not.determine.export.type"));
            return false;
        }
        BuildNumber buildNumber = exportCreatedByBuildNumber = exportScope == ExportScope.SPACE ? exportDescriptor.getCreatedByBuildNumber() : exportDescriptor.getBuildNumber();
        if (exportCreatedByBuildNumber == null) {
            this.addActionError("error.restore.backup.version.not.supplied", "6.0.5", 7103);
            return false;
        }
        if (!this.getImportExportManager().isImportAllowed(exportCreatedByBuildNumber.toString()) && !this.isCreatedByImportAllowed(exportDescriptor)) {
            this.addActionError("error.restore.backup.version.not.supported", "6.0.5", 7103, exportCreatedByBuildNumber);
            return false;
        }
        switch (exportScope) {
            case PAGE: {
                this.addActionError(this.getText("error.cannot.import.backups.here"));
                break;
            }
            case SPACE: {
                BuildNumber oldestSpaceImportAllowed;
                String spaceKey = exportDescriptor.getSpaceKey();
                if (spaceKey == null) {
                    this.addActionError(this.getText("error.restore.descriptor.missing.space.key"));
                }
                if (this.spaceManager.getSpace(spaceKey) != null) {
                    this.addActionError("error.space.with.key.exists", "'" + spaceKey + "'");
                }
                if ((oldestSpaceImportAllowed = this.getImportExportManager().getOldestSpaceImportAllowed()).compareTo(exportCreatedByBuildNumber) > 0) {
                    this.addActionError(this.getText("error.restore.space.incompatible.version", new Object[]{oldestSpaceImportAllowed, exportCreatedByBuildNumber, this.docBeanFactory.getDocumentationBean().getLink("help.restore.space")}));
                }
                return this.getActionErrors().isEmpty();
            }
            case SITE: {
                String message = "Site backup cannot be restored because its format is not supported.";
                this.addActionError("Site backup cannot be restored because its format is not supported.");
                return false;
            }
            case ALL: {
                return true;
            }
        }
        return false;
    }

    @Override
    protected DefaultImportContext createImportContext(ExportDescriptor exportDescriptor) throws ImportExportException, UnexpectedImportZipFileContents {
        File restoreFile = this.getAndValidateRestoreFile();
        DefaultImportContext defaultImportContext = new DefaultImportContext(restoreFile.getAbsolutePath(), exportDescriptor, this.getAuthenticatedUser());
        defaultImportContext.setDeleteWorkingFile(this.isDeleteWorkingFile());
        defaultImportContext.setDefaultUsersGroup(exportDescriptor.getDefaultUserGroup());
        if (exportDescriptor.isSpaceImport()) {
            defaultImportContext.setSpaceKeyOfSpaceImport(exportDescriptor.getSpaceKey());
        } else if (exportDescriptor.isSiteImport()) {
            KeyInitPostImportTask keyInitPostImportTask = (KeyInitPostImportTask)ContainerManager.getComponent((String)"keyInitPostImportTask");
            defaultImportContext.setPostImportTasks(Collections.singletonList(keyInitPostImportTask));
        }
        return defaultImportContext;
    }

    private File getAndValidateRestoreFile() throws ImportExportException {
        File restoreFile = this.getRestoreFile();
        if (restoreFile == null) {
            throw new ImportExportException("Error loading restore file.");
        }
        if (!restoreFile.exists()) {
            throw new ImportExportException("Restore file does not exist: " + restoreFile);
        }
        if (!restoreFile.isFile()) {
            throw new ImportExportException("Restore file exists but is not a file: " + restoreFile);
        }
        log.info("File to import exists and is a file: {}", (Object)restoreFile);
        return restoreFile;
    }

    protected abstract boolean isDeleteWorkingFile();

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setDocumentationBeanFactory(DocumentationBeanFactory docBeanFactory) {
        this.docBeanFactory = docBeanFactory;
    }

    private boolean isCreatedByImportAllowed(ExportDescriptor exportDescriptor) {
        return exportDescriptor.getCreatedByBuildNumber() != null && this.getImportExportManager().isImportAllowed(exportDescriptor.getCreatedByBuildNumber().toString());
    }
}

