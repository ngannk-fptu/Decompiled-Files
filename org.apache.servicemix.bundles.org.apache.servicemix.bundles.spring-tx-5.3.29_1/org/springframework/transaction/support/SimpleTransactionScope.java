/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.beans.factory.config.Scope
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.support;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SimpleTransactionScope
implements Scope {
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object scopedObject;
        ScopedObjectsHolder scopedObjects = (ScopedObjectsHolder)TransactionSynchronizationManager.getResource(this);
        if (scopedObjects == null) {
            scopedObjects = new ScopedObjectsHolder();
            TransactionSynchronizationManager.registerSynchronization(new CleanupSynchronization(scopedObjects));
            TransactionSynchronizationManager.bindResource(this, scopedObjects);
        }
        if ((scopedObject = scopedObjects.scopedInstances.get(name)) == null) {
            scopedObject = objectFactory.getObject();
            scopedObjects.scopedInstances.put(name, scopedObject);
        }
        return scopedObject;
    }

    @Nullable
    public Object remove(String name) {
        ScopedObjectsHolder scopedObjects = (ScopedObjectsHolder)TransactionSynchronizationManager.getResource(this);
        if (scopedObjects != null) {
            scopedObjects.destructionCallbacks.remove(name);
            return scopedObjects.scopedInstances.remove(name);
        }
        return null;
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        ScopedObjectsHolder scopedObjects = (ScopedObjectsHolder)TransactionSynchronizationManager.getResource(this);
        if (scopedObjects != null) {
            scopedObjects.destructionCallbacks.put(name, callback);
        }
    }

    @Nullable
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Nullable
    public String getConversationId() {
        return TransactionSynchronizationManager.getCurrentTransactionName();
    }

    private class CleanupSynchronization
    implements TransactionSynchronization {
        private final ScopedObjectsHolder scopedObjects;

        public CleanupSynchronization(ScopedObjectsHolder scopedObjects) {
            this.scopedObjects = scopedObjects;
        }

        @Override
        public void suspend() {
            TransactionSynchronizationManager.unbindResource(SimpleTransactionScope.this);
        }

        @Override
        public void resume() {
            TransactionSynchronizationManager.bindResource(SimpleTransactionScope.this, this.scopedObjects);
        }

        @Override
        public void afterCompletion(int status) {
            TransactionSynchronizationManager.unbindResourceIfPossible(SimpleTransactionScope.this);
            for (Runnable callback : this.scopedObjects.destructionCallbacks.values()) {
                callback.run();
            }
            this.scopedObjects.destructionCallbacks.clear();
            this.scopedObjects.scopedInstances.clear();
        }
    }

    static class ScopedObjectsHolder {
        final Map<String, Object> scopedInstances = new HashMap<String, Object>();
        final Map<String, Runnable> destructionCallbacks = new LinkedHashMap<String, Runnable>();

        ScopedObjectsHolder() {
        }
    }
}

