/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.ImportProcessorSummary;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.plugin.ModuleDescriptor;
import java.io.File;

@Deprecated
public interface ImportedPluginDataPreProcessor {
    public File process(ModuleDescriptor<BackupRestoreProvider> var1, File var2, ImportProcessorSummary var3);
}

