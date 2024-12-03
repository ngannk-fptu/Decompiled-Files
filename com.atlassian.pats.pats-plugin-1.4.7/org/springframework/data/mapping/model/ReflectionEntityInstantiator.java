/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanInstantiationException
 *  org.springframework.beans.BeanUtils
 */
package org.springframework.data.mapping.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.mapping.model.MappingInstantiationException;
import org.springframework.data.mapping.model.ParameterValueProvider;

enum ReflectionEntityInstantiator implements EntityInstantiator
{
    INSTANCE;

    private static final Object[] EMPTY_ARGS;

    @Override
    public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity, ParameterValueProvider<P> provider) {
        PreferredConstructor<T, P> constructor = entity.getPersistenceConstructor();
        if (constructor == null) {
            try {
                Class<T> clazz = entity.getType();
                if (clazz.isArray()) {
                    Class<Object> ctype = clazz;
                    int dims = 0;
                    while (ctype.isArray()) {
                        ctype = ctype.getComponentType();
                        ++dims;
                    }
                    return (T)Array.newInstance(clazz, dims);
                }
                return (T)BeanUtils.instantiateClass(entity.getType());
            }
            catch (BeanInstantiationException e) {
                throw new MappingInstantiationException(entity, Collections.emptyList(), (Exception)((Object)e));
            }
        }
        int parameterCount = constructor.getConstructor().getParameterCount();
        Object[] params = parameterCount == 0 ? EMPTY_ARGS : new Object[parameterCount];
        int i = 0;
        for (PreferredConstructor.Parameter<Object, P> parameter : constructor.getParameters()) {
            params[i++] = provider.getParameterValue(parameter);
        }
        try {
            return (T)BeanUtils.instantiateClass(constructor.getConstructor(), (Object[])params);
        }
        catch (BeanInstantiationException e) {
            throw new MappingInstantiationException(entity, new ArrayList<Object>(Arrays.asList(params)), (Exception)((Object)e));
        }
    }

    static {
        EMPTY_ARGS = new Object[0];
    }
}

