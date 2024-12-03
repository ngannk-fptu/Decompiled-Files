/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedConstructor;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.HandleMetaClass;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.metaclass.MixedInMetaClass;
import org.codehaus.groovy.runtime.metaclass.MixinInstanceMetaMethod;
import org.codehaus.groovy.runtime.metaclass.MixinInstanceMetaProperty;
import org.codehaus.groovy.runtime.metaclass.NewInstanceMetaMethod;
import org.codehaus.groovy.util.ManagedConcurrentMap;
import org.codehaus.groovy.util.ReferenceBundle;

public class MixinInMetaClass
extends ManagedConcurrentMap {
    final ExpandoMetaClass emc;
    final CachedClass mixinClass;
    final CachedConstructor constructor;
    private static ReferenceBundle softBundle = ReferenceBundle.getSoftBundle();

    public MixinInMetaClass(ExpandoMetaClass emc, CachedClass mixinClass) {
        super(softBundle);
        this.emc = emc;
        this.mixinClass = mixinClass;
        this.constructor = MixinInMetaClass.findDefaultConstructor(mixinClass);
        emc.addMixinClass(this);
    }

    private static CachedConstructor findDefaultConstructor(CachedClass mixinClass) {
        for (CachedConstructor constr : mixinClass.getConstructors()) {
            CachedClass[] classes;
            if (!Modifier.isPublic(constr.getModifiers()) || (classes = constr.getParameterTypes()).length != 0) continue;
            return constr;
        }
        throw new GroovyRuntimeException("No default constructor for class " + mixinClass.getName() + "! Can't be mixed in.");
    }

    public synchronized Object getMixinInstance(Object object) {
        Object mixinInstance = this.get(object);
        if (mixinInstance == null) {
            mixinInstance = this.constructor.invoke(MetaClassHelper.EMPTY_ARRAY);
            new MixedInMetaClass(mixinInstance, object);
            this.put(object, mixinInstance);
        }
        return mixinInstance;
    }

    public synchronized void setMixinInstance(Object object, Object mixinInstance) {
        if (mixinInstance == null) {
            this.remove(object);
        } else {
            this.put(object, mixinInstance);
        }
    }

    public CachedClass getInstanceClass() {
        return this.emc.getTheCachedClass();
    }

    public CachedClass getMixinClass() {
        return this.mixinClass;
    }

    public static void mixinClassesToMetaClass(MetaClass self, List<Class> categoryClasses) {
        Class selfClass = self.getTheClass();
        if (self instanceof HandleMetaClass) {
            self = (MetaClass)((Object)((HandleMetaClass)self).replaceDelegate());
        }
        if (!(self instanceof ExpandoMetaClass)) {
            if (self instanceof DelegatingMetaClass && ((DelegatingMetaClass)self).getAdaptee() instanceof ExpandoMetaClass) {
                self = ((DelegatingMetaClass)self).getAdaptee();
            } else {
                throw new GroovyRuntimeException("Can't mixin methods to meta class: " + self);
            }
        }
        ExpandoMetaClass mc = (ExpandoMetaClass)self;
        ArrayList<MetaMethod> arr = new ArrayList<MetaMethod>();
        for (Class clazz : categoryClasses) {
            CachedClass cachedCategoryClass = ReflectionCache.getCachedClass(clazz);
            MixinInMetaClass mixin = new MixinInMetaClass(mc, cachedCategoryClass);
            MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(clazz);
            List<MetaProperty> propList = metaClass.getProperties();
            for (MetaProperty prop : propList) {
                if (self.getMetaProperty(prop.getName()) != null) continue;
                mc.registerBeanProperty(prop.getName(), new MixinInstanceMetaProperty(prop, mixin));
            }
            for (CachedField prop : cachedCategoryClass.getFields()) {
                if (self.getMetaProperty(prop.getName()) != null) continue;
                mc.registerBeanProperty(prop.getName(), new MixinInstanceMetaProperty(prop, mixin));
            }
            for (MetaMethod method : metaClass.getMethods()) {
                int mod = method.getModifiers();
                if (!Modifier.isPublic(mod) || method instanceof CachedMethod && ((CachedMethod)method).getCachedMethod().isSynthetic()) continue;
                if (Modifier.isStatic(mod)) {
                    if (!(method instanceof CachedMethod)) continue;
                    MixinInMetaClass.staticMethod(self, arr, (CachedMethod)method);
                    continue;
                }
                if (method.getDeclaringClass().getTheClass() == Object.class && !method.getName().equals("toString")) continue;
                MixinInstanceMetaMethod metaMethod = new MixinInstanceMetaMethod(method, mixin);
                arr.add(metaMethod);
            }
        }
        for (Class<Object> clazz : arr) {
            MetaMethod metaMethod = (MetaMethod)((Object)clazz);
            if (metaMethod.getDeclaringClass().isAssignableFrom(selfClass)) {
                mc.registerInstanceMethod(metaMethod);
                continue;
            }
            mc.registerSubclassInstanceMethod(metaMethod);
        }
    }

    private static void staticMethod(final MetaClass self, List<MetaMethod> arr, CachedMethod method) {
        CachedClass[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            return;
        }
        if (paramTypes[0].isAssignableFrom(self.getTheClass())) {
            NewInstanceMetaMethod metaMethod = paramTypes[0].getTheClass() == self.getTheClass() ? new NewInstanceMetaMethod(method) : new NewInstanceMetaMethod(method){

                @Override
                public CachedClass getDeclaringClass() {
                    return ReflectionCache.getCachedClass(self.getTheClass());
                }
            };
            arr.add(metaMethod);
        } else if (self.getTheClass().isAssignableFrom(paramTypes[0].getTheClass())) {
            NewInstanceMetaMethod metaMethod = new NewInstanceMetaMethod(method);
            arr.add(metaMethod);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MixinInMetaClass)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MixinInMetaClass that = (MixinInMetaClass)o;
        return !(this.mixinClass != null ? !this.mixinClass.equals(that.mixinClass) : that.mixinClass != null);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.emc != null ? this.emc.hashCode() : 0);
        result = 31 * result + (this.mixinClass != null ? this.mixinClass.hashCode() : 0);
        result = 31 * result + (this.constructor != null ? this.constructor.hashCode() : 0);
        return result;
    }
}

