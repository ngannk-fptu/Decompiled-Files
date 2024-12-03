/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.persister.HibernateMembershipPersister;

@Deprecated
public class HibernateMembershipPersisterFactory
implements ObjectPersisterFactory {
    @Override
    public HibernateMembershipPersister createPersisterFor(ImportedObject importedObject) {
        String packageName = importedObject.getPackageName();
        String className = importedObject.getClassName();
        if ("com.atlassian.crowd.embedded.hibernate2".equals(packageName) && "HibernateMembership".equals(className)) {
            return new HibernateMembershipPersister();
        }
        return null;
    }
}

