/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.persister.PropertySetItemPersister;

@Deprecated
public class PropertySetItemPersisterFactory
implements ObjectPersisterFactory {
    @Override
    public PropertySetItemPersister createPersisterFor(ImportedObject importedObject) {
        String packageName = importedObject.getPackageName();
        String className = importedObject.getClassName();
        if ("bucket.user.propertyset".equals(packageName) && "BucketPropertySetItem".equals(className)) {
            return new PropertySetItemPersister();
        }
        return null;
    }
}

