/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.PostImportTask;
import com.atlassian.confluence.security.trust.KeyPairInitialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class KeyInitPostImportTask
implements PostImportTask {
    private static final Logger log = LoggerFactory.getLogger(KeyInitPostImportTask.class);
    private KeyPairInitialiser keyPairInitialiser;

    public KeyInitPostImportTask(KeyPairInitialiser keyPairInitialiser) {
        this.keyPairInitialiser = keyPairInitialiser;
    }

    @Override
    public void execute(ImportContext context) throws ImportExportException {
        try {
            log.info("Init Confluence key after a site import");
            this.keyPairInitialiser.initConfluenceKey();
        }
        catch (Exception e) {
            log.error("Problem when init Confluence key after a site import");
            throw new ImportExportException(e);
        }
    }
}

