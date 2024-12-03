/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.ancestors;

import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.PostImportTask;
import com.atlassian.confluence.pages.ancestors.AncestorRebuildException;
import com.atlassian.confluence.pages.ancestors.PageAncestorManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public final class RebuildAncestorsPostImportTask
implements PostImportTask {
    private static final Logger log = LoggerFactory.getLogger(RebuildAncestorsPostImportTask.class);
    private final PageAncestorManager pageAncestorManager;
    private final SpaceManager spaceManager;

    public RebuildAncestorsPostImportTask(PageAncestorManager pageAncestorManager, SpaceManager spaceManager) {
        this.pageAncestorManager = pageAncestorManager;
        this.spaceManager = spaceManager;
    }

    @Override
    public void execute(ImportContext context) {
        log.info("Rebuilding page ancestors");
        try {
            this.rebuildPageAncestors(context);
        }
        catch (AncestorRebuildException e) {
            log.error("Problem rebuilding page ancestors after import", (Throwable)e);
        }
    }

    private void rebuildPageAncestors(ImportContext context) throws AncestorRebuildException {
        String spaceKey = context.getSpaceKeyOfSpaceImport();
        if (!StringUtils.isNotBlank((CharSequence)spaceKey)) {
            this.pageAncestorManager.rebuildAll();
            return;
        }
        Space importedSpace = this.spaceManager.getSpace(spaceKey);
        if (importedSpace == null) {
            log.error("Could not load valid space for imported space key: " + spaceKey);
            return;
        }
        this.pageAncestorManager.rebuildSpace(importedSpace);
    }
}

