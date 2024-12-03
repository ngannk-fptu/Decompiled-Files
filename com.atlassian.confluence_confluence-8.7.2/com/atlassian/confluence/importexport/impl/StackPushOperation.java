/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.Operation;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.Set;
import java.util.Stack;

@Internal
public class StackPushOperation
implements Operation {
    private final Stack<ImportedObject> pendingDeferredImportedObjects;
    private final Set<TransientHibernateHandle> unsatisfiedObjectDependencies;
    private final ImportedObject importedObject;

    public StackPushOperation(Stack<ImportedObject> pendingDeferredImportedObjects, Set<TransientHibernateHandle> unsatisfiedObjectDependencies, ImportedObject importedObject) {
        this.pendingDeferredImportedObjects = pendingDeferredImportedObjects;
        this.unsatisfiedObjectDependencies = unsatisfiedObjectDependencies;
        this.importedObject = importedObject;
    }

    @Override
    public void execute() throws Exception {
        this.pendingDeferredImportedObjects.push(this.importedObject);
    }

    @Override
    public String getDescription() throws Exception {
        return "Object " + this.importedObject + " waiting on dependencies " + this.unsatisfiedObjectDependencies;
    }
}

