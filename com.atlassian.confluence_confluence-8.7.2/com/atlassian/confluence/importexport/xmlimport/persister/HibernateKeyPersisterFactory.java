/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.persister.HibernateKeyPersister;

@Deprecated
public class HibernateKeyPersisterFactory
implements ObjectPersisterFactory {
    @Override
    public HibernateKeyPersister createPersisterFor(ImportedObject importedObject) {
        String packageName = importedObject.getPackageName();
        String className = importedObject.getClassName();
        if ("com.atlassian.confluence.security.persistence.dao.hibernate".equals(packageName) && "HibernateKey".equals(className)) {
            return new HibernateKeyPersister();
        }
        return null;
    }
}

