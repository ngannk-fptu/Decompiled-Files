/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.preprocessing;

import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ImportedObjectPreprocessor;
import java.util.Optional;
import java.util.Set;

public class ContentPropertyFilterPreProcessor
implements ImportedObjectPreprocessor {
    private static final String PROPERTY_NAME_KEY = "name";
    private final Set<String> valuesToSkip;

    public ContentPropertyFilterPreProcessor(Set<String> valuesToSkip) {
        this.valuesToSkip = valuesToSkip;
    }

    @Override
    public Optional<ImportedObjectV2> apply(ImportedObjectV2 importedObject) {
        Object propertyValue;
        if (ContentProperty.class.equals(importedObject.getEntityClass()) && (propertyValue = importedObject.getPropertyValueMap().get(PROPERTY_NAME_KEY)) != null && this.valuesToSkip.contains(propertyValue)) {
            return Optional.empty();
        }
        return Optional.of(importedObject);
    }
}

