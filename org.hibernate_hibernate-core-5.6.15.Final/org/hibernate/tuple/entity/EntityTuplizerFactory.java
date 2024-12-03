/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.entity.DynamicMapEntityTuplizer;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.tuple.entity.PojoEntityTuplizer;

public class EntityTuplizerFactory
implements Serializable {
    public static final Class[] ENTITY_TUP_CTOR_SIG = new Class[]{EntityMetamodel.class, PersistentClass.class};
    private Map<EntityMode, Class<? extends EntityTuplizer>> defaultImplClassByMode = EntityTuplizerFactory.buildBaseMapping();

    public void registerDefaultTuplizerClass(EntityMode entityMode, Class<? extends EntityTuplizer> tuplizerClass) {
        assert (this.isEntityTuplizerImplementor(tuplizerClass)) : "Specified tuplizer class [" + tuplizerClass.getName() + "] does not implement " + EntityTuplizer.class.getName();
        assert (this.hasProperConstructor(tuplizerClass, ENTITY_TUP_CTOR_SIG)) : "Specified tuplizer class [" + tuplizerClass.getName() + "] is not properly instantiatable";
        this.defaultImplClassByMode.put(entityMode, tuplizerClass);
    }

    public EntityTuplizer constructTuplizer(String tuplizerClassName, EntityMetamodel metamodel, PersistentClass persistentClass) {
        try {
            Class tuplizerClass = ReflectHelper.classForName(tuplizerClassName);
            return this.constructTuplizer(tuplizerClass, metamodel, persistentClass);
        }
        catch (ClassNotFoundException e) {
            throw new HibernateException("Could not locate specified tuplizer class [" + tuplizerClassName + "]");
        }
    }

    public EntityTuplizer constructTuplizer(Class<? extends EntityTuplizer> tuplizerClass, EntityMetamodel metamodel, PersistentClass persistentClass) {
        Constructor<? extends EntityTuplizer> constructor = this.getProperConstructor(tuplizerClass, ENTITY_TUP_CTOR_SIG);
        assert (constructor != null) : "Unable to locate proper constructor for tuplizer [" + tuplizerClass.getName() + "]";
        try {
            return constructor.newInstance(metamodel, persistentClass);
        }
        catch (Throwable t) {
            throw new HibernateException("Unable to instantiate default tuplizer [" + tuplizerClass.getName() + "]", t);
        }
    }

    public EntityTuplizer constructDefaultTuplizer(EntityMode entityMode, EntityMetamodel metamodel, PersistentClass persistentClass) {
        Class<? extends EntityTuplizer> tuplizerClass = this.defaultImplClassByMode.get((Object)entityMode);
        if (tuplizerClass == null) {
            throw new HibernateException("could not determine default tuplizer class to use [" + (Object)((Object)entityMode) + "]");
        }
        return this.constructTuplizer(tuplizerClass, metamodel, persistentClass);
    }

    private boolean isEntityTuplizerImplementor(Class tuplizerClass) {
        return ReflectHelper.implementsInterface(tuplizerClass, EntityTuplizer.class);
    }

    private boolean hasProperConstructor(Class<? extends EntityTuplizer> tuplizerClass, Class[] constructorArgs) {
        return this.getProperConstructor(tuplizerClass, constructorArgs) != null && !ReflectHelper.isAbstractClass(tuplizerClass);
    }

    private Constructor<? extends EntityTuplizer> getProperConstructor(Class<? extends EntityTuplizer> clazz, Class[] constructorArgs) {
        Constructor<? extends EntityTuplizer> constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor(constructorArgs);
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

    private static Map<EntityMode, Class<? extends EntityTuplizer>> buildBaseMapping() {
        ConcurrentHashMap<EntityMode, Class<? extends EntityTuplizer>> map = new ConcurrentHashMap<EntityMode, Class<? extends EntityTuplizer>>();
        map.put(EntityMode.POJO, PojoEntityTuplizer.class);
        map.put(EntityMode.MAP, DynamicMapEntityTuplizer.class);
        return map;
    }
}

