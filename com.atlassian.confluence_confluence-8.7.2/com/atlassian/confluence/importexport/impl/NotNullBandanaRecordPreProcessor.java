/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;

public class NotNullBandanaRecordPreProcessor
implements ImportedObjectPreProcessor {
    @Override
    public boolean handles(ImportedObject object) {
        String className = object.getClassName();
        return className.equals("ConfluenceBandanaRecord");
    }

    @Override
    public ImportedObject process(ImportedObject object) {
        if (object.getStringProperty("context") == null || object.getStringProperty("key") == null) {
            return null;
        }
        return object;
    }
}

