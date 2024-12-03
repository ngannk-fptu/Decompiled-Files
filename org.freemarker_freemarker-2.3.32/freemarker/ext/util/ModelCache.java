/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.util;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class ModelCache {
    private boolean useCache = false;
    private Map<Object, ModelReference> modelCache = null;
    private ReferenceQueue<TemplateModel> refQueue = null;

    protected ModelCache() {
    }

    public synchronized void setUseCache(boolean useCache) {
        this.useCache = useCache;
        if (useCache) {
            this.modelCache = new IdentityHashMap<Object, ModelReference>();
            this.refQueue = new ReferenceQueue();
        } else {
            this.modelCache = null;
            this.refQueue = null;
        }
    }

    public synchronized boolean getUseCache() {
        return this.useCache;
    }

    public TemplateModel getInstance(Object object) {
        if (object instanceof TemplateModel) {
            return (TemplateModel)object;
        }
        if (object instanceof TemplateModelAdapter) {
            return ((TemplateModelAdapter)object).getTemplateModel();
        }
        if (this.useCache && this.isCacheable(object)) {
            TemplateModel model = this.lookup(object);
            if (model == null) {
                model = this.create(object);
                this.register(model, object);
            }
            return model;
        }
        return this.create(object);
    }

    protected abstract TemplateModel create(Object var1);

    protected abstract boolean isCacheable(Object var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearCache() {
        if (this.modelCache != null) {
            Map<Object, ModelReference> map = this.modelCache;
            synchronized (map) {
                this.modelCache.clear();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final TemplateModel lookup(Object object) {
        ModelReference ref = null;
        Map<Object, ModelReference> map = this.modelCache;
        synchronized (map) {
            ref = this.modelCache.get(object);
        }
        if (ref != null) {
            return ref.getModel();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void register(TemplateModel model, Object object) {
        Map<Object, ModelReference> map = this.modelCache;
        synchronized (map) {
            ModelReference queuedRef;
            while ((queuedRef = (ModelReference)this.refQueue.poll()) != null) {
                this.modelCache.remove(queuedRef.object);
            }
            this.modelCache.put(object, new ModelReference(model, object, this.refQueue));
        }
    }

    private static final class ModelReference
    extends SoftReference<TemplateModel> {
        Object object;

        ModelReference(TemplateModel ref, Object object, ReferenceQueue<TemplateModel> refQueue) {
            super(ref, refQueue);
            this.object = object;
        }

        TemplateModel getModel() {
            return (TemplateModel)this.get();
        }
    }
}

