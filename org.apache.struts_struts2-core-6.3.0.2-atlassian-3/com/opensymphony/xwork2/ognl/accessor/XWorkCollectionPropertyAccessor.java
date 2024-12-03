/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.ObjectPropertyAccessor
 *  ognl.OgnlException
 *  ognl.OgnlRuntime
 *  ognl.SetPropertyAccessor
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.ognl.accessor.SurrugateList;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.SetPropertyAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XWorkCollectionPropertyAccessor
extends SetPropertyAccessor {
    private static final Logger LOG = LogManager.getLogger(XWorkCollectionPropertyAccessor.class);
    public static final String KEY_PROPERTY_FOR_CREATION = "makeNew";
    private final ObjectPropertyAccessor _accessor = new ObjectPropertyAccessor();
    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;
    private OgnlUtil ognlUtil;

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer ot) {
        this.objectTypeDeterminer = ot;
    }

    @Inject
    public void setOgnlUtil(OgnlUtil util) {
        this.ognlUtil = util;
    }

    public Object getProperty(Map context, Object target, Object key) throws OgnlException {
        Class<?> keyType;
        LOG.trace("Entering getProperty()");
        if (!ReflectionContextState.isGettingByKeyProperty(context) && !key.equals(KEY_PROPERTY_FOR_CREATION)) {
            return super.getProperty(context, target, key);
        }
        ReflectionContextState.setGettingByKeyProperty(context, false);
        Collection c = (Collection)target;
        Class lastBeanClass = ReflectionContextState.getLastBeanClassAccessed(context);
        String lastPropertyClass = ReflectionContextState.getLastBeanPropertyAccessed(context);
        if (lastBeanClass == null || lastPropertyClass == null) {
            ReflectionContextState.updateCurrentPropertyPath(context, key);
            return super.getProperty(context, target, key);
        }
        String keyProperty = this.objectTypeDeterminer.getKeyProperty(lastBeanClass, lastPropertyClass);
        Class<?> collClass = this.objectTypeDeterminer.getElementClass(lastBeanClass, lastPropertyClass, key);
        Class<?> toGetTypeFrom = collClass != null ? collClass : c.iterator().next().getClass();
        try {
            keyType = OgnlRuntime.getPropertyDescriptor(toGetTypeFrom, (String)keyProperty).getPropertyType();
        }
        catch (Exception exc) {
            throw new OgnlException("Error getting property descriptor: " + exc.getMessage());
        }
        if (ReflectionContextState.isCreatingNullObjects(context)) {
            Map collMap = this.getSetMap(context, c, keyProperty);
            if (key.toString().equals(KEY_PROPERTY_FOR_CREATION)) {
                return collMap.get(null);
            }
            Object realKey = this.xworkConverter.convertValue(context, key, keyType);
            Object value = collMap.get(realKey);
            if (value == null && ReflectionContextState.isCreatingNullObjects(context) && this.objectTypeDeterminer.shouldCreateIfNew(lastBeanClass, lastPropertyClass, c, keyProperty, false)) {
                try {
                    value = this.objectFactory.buildBean(collClass, (Map<String, Object>)context);
                    this._accessor.setProperty(context, value, (Object)keyProperty, realKey);
                    c.add(value);
                    collMap.put(realKey, value);
                }
                catch (Exception exc) {
                    throw new OgnlException("Error adding new element to collection", (Throwable)exc);
                }
            }
            return value;
        }
        if (key.toString().equals(KEY_PROPERTY_FOR_CREATION)) {
            return null;
        }
        Object realKey = this.xworkConverter.convertValue(context, key, keyType);
        return this.getPropertyThroughIteration(context, c, keyProperty, realKey);
    }

    private Map getSetMap(Map context, Collection collection, String property) throws OgnlException {
        LOG.trace("getting set Map");
        String path = ReflectionContextState.getCurrentPropertyPath(context);
        Map<Object, Object> map = ReflectionContextState.getSetMap(context, path);
        if (map == null) {
            LOG.trace("creating set Map");
            map = new HashMap<Object, Object>();
            map.put(null, new SurrugateList(collection));
            for (Object currTest : collection) {
                Object currKey = this._accessor.getProperty(context, currTest, (Object)property);
                if (currKey == null) continue;
                map.put(currKey, currTest);
            }
            ReflectionContextState.setSetMap(context, map, path);
        }
        return map;
    }

    public Object getPropertyThroughIteration(Map context, Collection collection, String property, Object key) throws OgnlException {
        for (Object currTest : collection) {
            if (!this._accessor.getProperty(context, currTest, (Object)property).equals(key)) continue;
            return currTest;
        }
        return null;
    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        Class lastClass = (Class)context.get("last.bean.accessed");
        String lastProperty = (String)context.get("last.property.accessed");
        Class convertToClass = this.objectTypeDeterminer.getElementClass(lastClass, lastProperty, name);
        if (name instanceof String && value.getClass().isArray()) {
            Object[] values;
            Collection c = (Collection)target;
            for (Object v : values = (Object[])value) {
                try {
                    Object o = this.objectFactory.buildBean(convertToClass, (Map<String, Object>)context);
                    this.ognlUtil.setValue((String)name, context, o, v);
                    c.add(o);
                }
                catch (Exception e) {
                    throw new OgnlException("Error converting given String values for Collection.", (Throwable)e);
                }
            }
            return;
        }
        Object realValue = this.getRealValue(context, value, convertToClass);
        super.setProperty(context, target, name, realValue);
    }

    private Object getRealValue(Map context, Object value, Class convertToClass) {
        if (value == null || convertToClass == null) {
            return value;
        }
        return this.xworkConverter.convertValue(context, value, convertToClass);
    }
}

