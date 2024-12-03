/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ClassIntrospector;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract class ClassBasedModelFactory
implements TemplateHashModel {
    private final BeansWrapper wrapper;
    private final Map<String, TemplateModel> cache = new ConcurrentHashMap<String, TemplateModel>();
    private final Set<String> classIntrospectionsInProgress = new HashSet<String>();

    protected ClassBasedModelFactory(BeansWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        try {
            return this.getInternal(key);
        }
        catch (Exception e) {
            if (e instanceof TemplateModelException) {
                throw (TemplateModelException)e;
            }
            throw new _TemplateModelException((Throwable)e, "Failed to get value for key ", new _DelayedJQuote(key), "; see cause exception.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private TemplateModel getInternal(String key) throws TemplateModelException, ClassNotFoundException {
        int classIntrospectorClearingCounter;
        ClassIntrospector classIntrospector;
        TemplateModel model;
        Object sharedLock;
        TemplateModel model2 = this.cache.get(key);
        if (model2 != null) {
            return model2;
        }
        Object object = sharedLock = this.wrapper.getSharedIntrospectionLock();
        synchronized (object) {
            model = this.cache.get(key);
            if (model != null) {
                return model;
            }
            while (model == null && this.classIntrospectionsInProgress.contains(key)) {
                try {
                    sharedLock.wait();
                    model = this.cache.get(key);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException("Class inrospection data lookup aborted: " + e);
                }
            }
            if (model != null) {
                return model;
            }
            this.classIntrospectionsInProgress.add(key);
            classIntrospector = this.wrapper.getClassIntrospector();
            classIntrospectorClearingCounter = classIntrospector.getClearingCounter();
        }
        try {
            Object object2;
            Class clazz = ClassUtil.forName(key);
            classIntrospector.get(clazz);
            model = this.createModel(clazz);
            if (model != null) {
                object2 = sharedLock;
                synchronized (object2) {
                    if (classIntrospector == this.wrapper.getClassIntrospector() && classIntrospectorClearingCounter == classIntrospector.getClearingCounter()) {
                        this.cache.put(key, model);
                    }
                }
            }
            object2 = model;
            return object2;
        }
        finally {
            Object object3 = sharedLock;
            synchronized (object3) {
                this.classIntrospectionsInProgress.remove(key);
                sharedLock.notifyAll();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void clearCache() {
        Object object = this.wrapper.getSharedIntrospectionLock();
        synchronized (object) {
            this.cache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeFromCache(Class<?> clazz) {
        Object object = this.wrapper.getSharedIntrospectionLock();
        synchronized (object) {
            this.cache.remove(clazz.getName());
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    protected abstract TemplateModel createModel(Class<?> var1) throws TemplateModelException;

    protected BeansWrapper getWrapper() {
        return this.wrapper;
    }
}

