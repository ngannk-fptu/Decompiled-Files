/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportContext
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.importexport;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.importexport.SynchronyCredentialsPostImportTask;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchrony-credentials-post-import-task")
@ExportAsService(value={SynchronyCredentialsPostImportTask.class})
public class SynchronyCredentialsPostImportTaskImpl
implements SynchronyCredentialsPostImportTask {
    private SynchronyConfigurationManager synchronyConfigurationManager;

    @Autowired
    public SynchronyCredentialsPostImportTaskImpl(SynchronyConfigurationManager synchronyConfigurationManager) {
        this.synchronyConfigurationManager = synchronyConfigurationManager;
    }

    public void execute(ImportContext context) throws ImportExportException {
        this.synchronyConfigurationManager.removeSynchronyCredentials();
    }
}

