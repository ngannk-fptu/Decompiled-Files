/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.mapping.Component;
import org.hibernate.tuple.component.ComponentTuplizer;
import org.hibernate.tuple.component.DynamicMapComponentTuplizer;
import org.hibernate.tuple.component.PojoComponentTuplizer;

public class ComponentTuplizerFactory
implements Serializable {
    private static final Class[] COMPONENT_TUP_CTOR_SIG = new Class[]{Component.class};
    private Map<EntityMode, Class<? extends ComponentTuplizer>> defaultImplClassByMode = ComponentTuplizerFactory.buildBaseMapping();
    private final ClassLoaderAccess classLoaderAccess;

    @Deprecated
    public ComponentTuplizerFactory(MetadataBuildingOptions metadataBuildingOptions) {
        this.classLoaderAccess = new ClassLoaderAccessImpl(metadataBuildingOptions.getTempClassLoader(), metadataBuildingOptions.getServiceRegistry().getService(ClassLoaderService.class));
    }

    public ComponentTuplizerFactory(BootstrapContext bootstrapContext) {
        this.classLoaderAccess = bootstrapContext.getClassLoaderAccess();
    }

    public void registerDefaultTuplizerClass(EntityMode entityMode, Class<? extends ComponentTuplizer> tuplizerClass) {
        assert (this.isComponentTuplizerImplementor(tuplizerClass)) : "Specified tuplizer class [" + tuplizerClass.getName() + "] does not implement " + ComponentTuplizer.class.getName();
        assert (this.hasProperConstructor(tuplizerClass)) : "Specified tuplizer class [" + tuplizerClass.getName() + "] is not properly instantiatable";
        this.defaultImplClassByMode.put(entityMode, tuplizerClass);
    }

    public ComponentTuplizer constructTuplizer(String tuplizerClassName, Component metadata) {
        try {
            Class tuplizerClass = this.classLoaderAccess.classForName(tuplizerClassName);
            return this.constructTuplizer(tuplizerClass, metadata);
        }
        catch (ClassLoadingException e) {
            throw new HibernateException("Could not locate specified tuplizer class [" + tuplizerClassName + "]");
        }
    }

    public ComponentTuplizer constructTuplizer(Class<? extends ComponentTuplizer> tuplizerClass, Component metadata) {
        Constructor<? extends ComponentTuplizer> constructor = this.getProperConstructor(tuplizerClass);
        assert (constructor != null) : "Unable to locate proper constructor for tuplizer [" + tuplizerClass.getName() + "]";
        try {
            return constructor.newInstance(metadata);
        }
        catch (Throwable t) {
            throw new HibernateException("Unable to instantiate default tuplizer [" + tuplizerClass.getName() + "]", t);
        }
    }

    public ComponentTuplizer constructDefaultTuplizer(EntityMode entityMode, Component metadata) {
        Class<? extends ComponentTuplizer> tuplizerClass = this.defaultImplClassByMode.get((Object)entityMode);
        if (tuplizerClass == null) {
            throw new HibernateException("could not determine default tuplizer class to use [" + (Object)((Object)entityMode) + "]");
        }
        return this.constructTuplizer(tuplizerClass, metadata);
    }

    private boolean isComponentTuplizerImplementor(Class tuplizerClass) {
        return ReflectHelper.implementsInterface(tuplizerClass, ComponentTuplizer.class);
    }

    private boolean hasProperConstructor(Class tuplizerClass) {
        return this.getProperConstructor(tuplizerClass) != null;
    }

    private Constructor<? extends ComponentTuplizer> getProperConstructor(Class<? extends ComponentTuplizer> clazz) {
        Constructor<? extends ComponentTuplizer> constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor(COMPONENT_TUP_CTOR_SIG);
            try {
                ReflectHelper.ensureAccessibility(constructor);
            }
            catch (SecurityException e) {
                constructor = null;
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        return constructor;
    }

    private static Map<EntityMode, Class<? extends ComponentTuplizer>> buildBaseMapping() {
        ConcurrentHashMap<EntityMode, Class<? extends ComponentTuplizer>> map = new ConcurrentHashMap<EntityMode, Class<? extends ComponentTuplizer>>();
        map.put(EntityMode.POJO, PojoComponentTuplizer.class);
        map.put(EntityMode.MAP, DynamicMapComponentTuplizer.class);
        return map;
    }
}

