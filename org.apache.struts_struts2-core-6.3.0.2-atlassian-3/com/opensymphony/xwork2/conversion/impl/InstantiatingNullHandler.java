/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InstantiatingNullHandler
implements NullHandler {
    private static final Logger LOG = LogManager.getLogger(InstantiatingNullHandler.class);
    private ReflectionProvider reflectionProvider;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;

    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer det) {
        this.objectTypeDeterminer = det;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Override
    public Object nullMethodResult(Map<String, Object> context, Object target, String methodName, Object[] args) {
        LOG.debug("Entering nullMethodResult");
        return null;
    }

    @Override
    public Object nullPropertyValue(Map<String, Object> context, Object target, Object property) {
        LOG.debug("Entering nullPropertyValue [target={}, property={}]", target, property);
        boolean c = ReflectionContextState.isCreatingNullObjects(context);
        if (!c) {
            return null;
        }
        if (target == null || property == null) {
            return null;
        }
        try {
            String propName = property.toString();
            Object realTarget = this.reflectionProvider.getRealTarget(propName, context, target);
            Class<?> clazz = null;
            if (realTarget != null) {
                PropertyDescriptor pd = this.reflectionProvider.getPropertyDescriptor(realTarget.getClass(), propName);
                if (pd == null) {
                    return null;
                }
                clazz = pd.getPropertyType();
            }
            if (clazz == null) {
                return null;
            }
            Object param = this.createObject(clazz, realTarget, propName, context);
            this.reflectionProvider.setValue(propName, context, realTarget, param);
            return param;
        }
        catch (Exception e) {
            LOG.error("Could not create and/or set value back on to object", (Throwable)e);
            return null;
        }
    }

    private Object createObject(Class clazz, Object target, String property, Map<String, Object> context) throws Exception {
        if (Set.class.isAssignableFrom(clazz)) {
            return new HashSet();
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return new ArrayList();
        }
        if (clazz == Map.class) {
            return new HashMap();
        }
        if (clazz == EnumMap.class) {
            Class keyClass = this.objectTypeDeterminer.getKeyClass(target.getClass(), property);
            return new EnumMap(keyClass);
        }
        return this.objectFactory.buildBean(clazz, context);
    }
}

