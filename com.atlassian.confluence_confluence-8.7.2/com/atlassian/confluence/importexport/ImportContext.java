/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.ImportedObjectPostProcessor;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.ImportedPluginDataPreProcessor;
import com.atlassian.confluence.importexport.PostImportTask;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.util.ProgressMeter;
import java.util.List;

@Deprecated
public interface ImportContext {
    public ProgressMeter getProgressMeter();

    public void setProgressMeter(ProgressMeter var1);

    public ConfluenceUser getUser();

    public void setRebuildIndex(boolean var1);

    public boolean isRebuildIndex();

    public boolean isDeleteWorkingFile();

    public void setDeleteWorkingFile(boolean var1);

    public String getWorkingFile();

    public ImportedObjectPostProcessor getPostProcessor();

    public ImportedObjectPreProcessor getPreProcessor();

    public ImportedPluginDataPreProcessor getPluginDataPreProcessor();

    public String getSpaceKeyOfSpaceImport();

    public String getDefaultUsersGroup();

    public List<PostImportTask> getPostImportTasks();

    public void setPostImportTasks(List<PostImportTask> var1);

    public ExportDescriptor getExportDescriptor();
}

