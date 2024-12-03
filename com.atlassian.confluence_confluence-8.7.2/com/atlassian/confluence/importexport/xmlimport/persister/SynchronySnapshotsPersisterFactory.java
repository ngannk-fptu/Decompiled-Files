/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.persister.NoopPersister;

@Deprecated
@Internal
public class SynchronySnapshotsPersisterFactory
implements ObjectPersisterFactory {
    @Override
    public ObjectPersister createPersisterFor(ImportedObject importedObject) {
        String packageName = importedObject.getPackageName();
        String className = importedObject.getClassName();
        if ("com.atlassian.synchrony".equals(packageName) && "Snapshots".equals(className)) {
            return new NoopPersister();
        }
        return null;
    }
}

