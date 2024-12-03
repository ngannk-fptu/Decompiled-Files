/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.List;

@Deprecated
public class DelegatingObjectPersisterFactory
implements ObjectPersisterFactory {
    private final List<ObjectPersisterFactory> delegateFactories;
    private final ObjectPersisterFactory defaultFactory;

    public DelegatingObjectPersisterFactory(List<ObjectPersisterFactory> delegateFactories, ObjectPersisterFactory defaultFactory) {
        this.delegateFactories = delegateFactories;
        this.defaultFactory = defaultFactory;
    }

    @Override
    public ObjectPersister createPersisterFor(ImportedObject importedObject) {
        for (ObjectPersisterFactory delegate : this.delegateFactories) {
            ObjectPersister persister = delegate.createPersisterFor(importedObject);
            if (persister == null) continue;
            return persister;
        }
        return this.defaultFactory.createPersisterFor(importedObject);
    }
}

