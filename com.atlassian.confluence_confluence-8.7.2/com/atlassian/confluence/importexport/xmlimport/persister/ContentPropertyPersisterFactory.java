/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.persister.ContentPropertyPersister;

@Deprecated
public class ContentPropertyPersisterFactory
implements ObjectPersisterFactory {
    public static final String STATUS_LAST_MODIFIER = "status-lastmodifier";

    @Override
    public ObjectPersister createPersisterFor(ImportedObject importedObject) {
        String packageName = importedObject.getPackageName();
        String className = importedObject.getClassName();
        if ("com.atlassian.confluence.content".equals(packageName) && "ContentProperty".equals(className) && STATUS_LAST_MODIFIER.equals(importedObject.getStringProperty("name"))) {
            return new ContentPropertyPersister();
        }
        return null;
    }
}

