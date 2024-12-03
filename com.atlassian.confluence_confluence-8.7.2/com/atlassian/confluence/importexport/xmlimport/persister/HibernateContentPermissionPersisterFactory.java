/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.persister.HibernateContentPermissionPersister;

@Deprecated
public class HibernateContentPermissionPersisterFactory
implements ObjectPersisterFactory {
    @Override
    public HibernateContentPermissionPersister createPersisterFor(ImportedObject importedObject) {
        String packageName = importedObject.getPackageName();
        String className = importedObject.getClassName();
        if ("com.atlassian.confluence.security".equals(packageName) && "ContentPermission".equals(className)) {
            return new HibernateContentPermissionPersister();
        }
        return null;
    }
}

