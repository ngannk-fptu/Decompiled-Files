/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersisterHelper;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import java.util.Collection;

public class ContentPersisterHelper
extends PersisterHelper {
    private static final int BATCH_SIZE = Integer.getInteger("confluence.restore.content-batch-size", 1000);
    private static final String PARENT_FIELD_NAME = "parent";
    private static final String ORIGINAL_VERSION_FIELD_NAME = "originalVersion";

    public ContentPersisterHelper(OnObjectsProcessingHandler onObjectsProcessingHandler) {
        super(onObjectsProcessingHandler);
    }

    @Override
    public int getBatchSize() {
        return BATCH_SIZE;
    }

    boolean isTopLevelPage(ImportedObjectV2 importedObjectV2) {
        return this.getNotEmptyDependencies(importedObjectV2, ContentEntityObject.class).isEmpty();
    }

    boolean isChildPage(Collection<HibernateField> nonEmptyContentEntityReferences) {
        if (nonEmptyContentEntityReferences.size() != 1) {
            return false;
        }
        HibernateField theOnlyField = nonEmptyContentEntityReferences.iterator().next();
        return PARENT_FIELD_NAME.equals(theOnlyField.getPropertyName());
    }

    boolean isHistoricPage(ImportedObjectV2 importedObjectV2) {
        return importedObjectV2.getFieldValue(ORIGINAL_VERSION_FIELD_NAME) != null;
    }
}

