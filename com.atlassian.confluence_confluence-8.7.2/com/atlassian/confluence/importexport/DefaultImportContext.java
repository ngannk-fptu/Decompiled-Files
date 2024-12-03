/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportedObjectPostProcessor;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.ImportedPluginDataPreProcessor;
import com.atlassian.confluence.importexport.PostImportTask;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.user.User;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Deprecated
public class DefaultImportContext
implements ImportContext {
    private URL workingURL;
    private String workingFile;
    private ConfluenceUser importer;
    private boolean incrementalImport = false;
    private String defaultSpaceKey;
    private String defaultUsersGroup;
    private ProgressMeter progress = new ProgressMeter();
    private boolean deleteWorkingFile = true;
    private boolean rebuildIndex = false;
    private boolean requireUpgrades = false;
    private ImportedObjectPostProcessor postProcessor;
    private String spaceKeyOfSpaceImport;
    private ImportedObjectPreProcessor preProcessor;
    private ImportedPluginDataPreProcessor pluginDataPreProcessor;
    private List<PostImportTask> postImportTasks;
    private ExportDescriptor exportDescriptor;

    public DefaultImportContext(String workingFile, ExportDescriptor exportDescriptor, ConfluenceUser importer) {
        this.workingFile = Objects.requireNonNull(workingFile);
        this.exportDescriptor = Objects.requireNonNull(exportDescriptor);
        this.importer = importer;
    }

    public DefaultImportContext() {
    }

    public DefaultImportContext(String workingFile, ConfluenceUser importer) {
        this.workingFile = workingFile;
        this.importer = importer;
    }

    @Deprecated
    public DefaultImportContext(String workingFile, User importer) {
        this.workingFile = workingFile;
        this.importer = FindUserHelper.getUser(importer);
    }

    public DefaultImportContext(URL workingURL, ConfluenceUser importer) {
        this.workingURL = workingURL;
        this.importer = importer;
    }

    @Deprecated
    public DefaultImportContext(URL workingURL, User importer) {
        this.workingURL = workingURL;
        this.importer = FindUserHelper.getUser(importer);
    }

    @Override
    public String getWorkingFile() {
        return this.workingFile;
    }

    @Override
    public ImportedObjectPostProcessor getPostProcessor() {
        return this.postProcessor;
    }

    public void setWorkingFile(String workingFile) {
        this.workingFile = workingFile;
    }

    public void setPreProcessor(ImportedObjectPreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    @Override
    public ImportedObjectPreProcessor getPreProcessor() {
        return this.preProcessor;
    }

    public boolean isIncrementalImport() {
        return this.incrementalImport;
    }

    public void setIncrementalImport(boolean incrementalImport) {
        this.incrementalImport = incrementalImport;
    }

    public boolean isRequireUpgrades() {
        return this.requireUpgrades;
    }

    public void setRequireUpgrades(boolean requireUpgrades) {
        this.requireUpgrades = requireUpgrades;
    }

    public String getDefaultSpaceKey() {
        return this.defaultSpaceKey;
    }

    public void setDefaultSpaceKey(String defaultSpaceKey) {
        this.defaultSpaceKey = defaultSpaceKey;
    }

    @Override
    public String getDefaultUsersGroup() {
        return this.defaultUsersGroup;
    }

    public void setDefaultUsersGroup(String defaultUsersGroup) {
        this.defaultUsersGroup = defaultUsersGroup;
    }

    @Override
    public ProgressMeter getProgressMeter() {
        return this.progress;
    }

    @Override
    public void setProgressMeter(ProgressMeter progress) {
        this.progress = progress;
    }

    @Override
    public boolean isDeleteWorkingFile() {
        return this.deleteWorkingFile;
    }

    @Override
    public void setDeleteWorkingFile(boolean deleteWorkingFile) {
        this.deleteWorkingFile = deleteWorkingFile;
    }

    public URL getWorkingURL() {
        return this.workingURL;
    }

    public void setWorkingURL(URL workingURL) {
        this.workingURL = workingURL;
    }

    @Override
    public ConfluenceUser getUser() {
        return this.importer;
    }

    @Override
    public void setRebuildIndex(boolean rebuildIndex) {
        this.rebuildIndex = rebuildIndex;
    }

    @Override
    public boolean isRebuildIndex() {
        return this.rebuildIndex;
    }

    public void setPostProcessor(ImportedObjectPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    @Override
    public String getSpaceKeyOfSpaceImport() {
        return this.spaceKeyOfSpaceImport;
    }

    @Override
    public List<PostImportTask> getPostImportTasks() {
        return this.postImportTasks == null ? new ArrayList() : this.postImportTasks;
    }

    @Override
    public void setPostImportTasks(List<PostImportTask> postImportTasks) {
        this.postImportTasks = postImportTasks;
    }

    public void setSpaceKeyOfSpaceImport(String spaceKeyOfSpaceImport) {
        this.spaceKeyOfSpaceImport = spaceKeyOfSpaceImport;
    }

    @Override
    public ImportedPluginDataPreProcessor getPluginDataPreProcessor() {
        return this.pluginDataPreProcessor;
    }

    public void setPluginDataPreProcessor(ImportedPluginDataPreProcessor pluginDataPreProcessor) {
        this.pluginDataPreProcessor = pluginDataPreProcessor;
    }

    @Override
    public ExportDescriptor getExportDescriptor() {
        return this.exportDescriptor;
    }
}

