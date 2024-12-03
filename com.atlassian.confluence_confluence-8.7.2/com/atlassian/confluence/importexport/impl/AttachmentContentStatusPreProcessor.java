/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

public class AttachmentContentStatusPreProcessor
implements ImportedObjectPreProcessor {
    @VisibleForTesting
    static final String CONTENT_STATUS_PROPERTY_KEY = "contentStatus";
    @VisibleForTesting
    static final PrimitiveProperty DEFAULT_CONTENT_STATUS_PROPERTY = new PrimitiveProperty("contentStatus", null, "current");
    private static final Predicate<ImportedProperty> REMOVE_CONTENT_STATUS_FILTER = property -> !CONTENT_STATUS_PROPERTY_KEY.equals(property.getName());

    @Override
    public boolean handles(ImportedObject object) {
        return "Attachment".equals(object.getClassName());
    }

    @Override
    public ImportedObject process(ImportedObject object) {
        if (StringUtils.isNotEmpty((CharSequence)object.getStringProperty(CONTENT_STATUS_PROPERTY_KEY))) {
            return object;
        }
        ArrayList<ImportedProperty> properties = new ArrayList<ImportedProperty>(Collections2.filter(object.getProperties(), REMOVE_CONTENT_STATUS_FILTER));
        properties.add(DEFAULT_CONTENT_STATUS_PROPERTY);
        return new ImportedObject(object.getClassName(), object.getPackageName(), properties, object.getCompositeId());
    }
}

