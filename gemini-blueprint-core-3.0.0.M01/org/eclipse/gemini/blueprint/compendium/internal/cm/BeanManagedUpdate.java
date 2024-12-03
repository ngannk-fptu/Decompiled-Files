/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.gemini.blueprint.compendium.internal.cm.UpdateCallback;
import org.eclipse.gemini.blueprint.compendium.internal.cm.UpdateMethodAdapter;

class BeanManagedUpdate
implements UpdateCallback {
    private final String methodName;
    private final Map<Class<?>, WeakReference<UpdateMethodAdapter>> classCache = new WeakHashMap(2);

    public BeanManagedUpdate(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public void update(Object instance, Map properties) {
        this.getUpdateMethod(instance).invoke(instance, properties);
    }

    private UpdateMethodAdapter getUpdateMethod(Object instance) {
        UpdateMethodAdapter adapter;
        Class<?> type = instance.getClass();
        WeakReference<UpdateMethodAdapter> adapterReference = this.classCache.get(type);
        if (adapterReference != null && (adapter = (UpdateMethodAdapter)adapterReference.get()) != null) {
            return adapter;
        }
        adapter = new UpdateMethodAdapter(this.methodName, type);
        this.classCache.put(type, new WeakReference<UpdateMethodAdapter>(adapter));
        return adapter;
    }
}

