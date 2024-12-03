/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.Set;

@Deprecated(since="8.3.0", forRemoval=true)
public class ContentPropertyFilterPreProcessor
implements ImportedObjectPreProcessor {
    private static final String PROPERTY_NAME_KEY = "name";
    private final Set<String> propertiesToSkip;

    public ContentPropertyFilterPreProcessor(Set<String> valuesToSkip) {
        this.propertiesToSkip = valuesToSkip;
    }

    @Override
    public boolean handles(ImportedObject object) {
        return ContentProperty.class.getCanonicalName().equals(String.format("%s.%s", object.getPackageName(), object.getClassName()));
    }

    @Override
    public ImportedObject process(ImportedObject object) {
        String propertyValue = object.getStringProperty(PROPERTY_NAME_KEY);
        if (propertyValue != null && this.propertiesToSkip.contains(propertyValue)) {
            return null;
        }
        return object;
    }
}

