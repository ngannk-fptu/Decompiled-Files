/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.MetaBeanProperty;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.MixinInMetaClass;
import org.codehaus.groovy.reflection.ReflectionCache;

public class MixinInstanceMetaProperty
extends MetaBeanProperty {
    public MixinInstanceMetaProperty(MetaProperty property, MixinInMetaClass mixinInMetaClass) {
        super(property.getName(), property.getType(), MixinInstanceMetaProperty.createGetter(property, mixinInMetaClass), MixinInstanceMetaProperty.createSetter(property, mixinInMetaClass));
    }

    private static MetaMethod createSetter(final MetaProperty property, final MixinInMetaClass mixinInMetaClass) {
        return new MetaMethod(){
            final String name;
            {
                this.name = MetaProperty.getSetterName(property.getName());
                this.setParametersTypes(new CachedClass[]{ReflectionCache.getCachedClass(property.getType())});
            }

            @Override
            public int getModifiers() {
                return 1;
            }

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            public Class getReturnType() {
                return property.getType();
            }

            @Override
            public CachedClass getDeclaringClass() {
                return mixinInMetaClass.getInstanceClass();
            }

            @Override
            public Object invoke(Object object, Object[] arguments) {
                property.setProperty(mixinInMetaClass.getMixinInstance(object), arguments[0]);
                return null;
            }
        };
    }

    private static MetaMethod createGetter(final MetaProperty property, final MixinInMetaClass mixinInMetaClass) {
        return new MetaMethod(){
            final String name;
            {
                this.name = MetaProperty.getGetterName(property.getName(), property.getType());
                this.setParametersTypes(CachedClass.EMPTY_ARRAY);
            }

            @Override
            public int getModifiers() {
                return 1;
            }

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            public Class getReturnType() {
                return property.getType();
            }

            @Override
            public CachedClass getDeclaringClass() {
                return mixinInMetaClass.getInstanceClass();
            }

            @Override
            public Object invoke(Object object, Object[] arguments) {
                return property.getProperty(mixinInMetaClass.getMixinInstance(object));
            }
        };
    }
}

