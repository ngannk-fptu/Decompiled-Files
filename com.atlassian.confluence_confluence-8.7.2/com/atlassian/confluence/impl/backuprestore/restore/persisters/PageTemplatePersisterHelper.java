/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersisterHelper;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.confluence.pages.templates.PageTemplate;

public class PageTemplatePersisterHelper
extends PersisterHelper {
    public PageTemplatePersisterHelper(OnObjectsProcessingHandler onObjectsProcessingHandler) {
        super(onObjectsProcessingHandler);
    }

    public boolean isLatestPageTemplate(ImportedObjectV2 importedObject) {
        return this.getNotEmptyDependencies(importedObject, PageTemplate.class).isEmpty();
    }
}

