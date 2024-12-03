/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.MapPropertyAccessor
 *  ognl.OgnlException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.util.Map;
import ognl.MapPropertyAccessor;
import ognl.OgnlException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XWorkMapPropertyAccessor
extends MapPropertyAccessor {
    private static final Logger LOG = LogManager.getLogger(XWorkMapPropertyAccessor.class);
    private static final String[] INDEX_ACCESS_PROPS = new String[]{"size", "isEmpty", "keys", "values"};
    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;

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

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        LOG.trace("Entering getProperty ({},{},{})", (Object)context, target, name);
        ReflectionContextState.updateCurrentPropertyPath(context, name);
        if (name instanceof String && this.contains(INDEX_ACCESS_PROPS, (String)name)) {
            return super.getProperty(context, target, name);
        }
        Object result = null;
        try {
            result = super.getProperty(context, target, name);
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (result == null) {
            Class lastClass = (Class)context.get("last.bean.accessed");
            String lastProperty = (String)context.get("last.property.accessed");
            if (lastClass == null || lastProperty == null) {
                return null;
            }
            Map map = (Map)target;
            Object key = this.getKey(context, name);
            result = map.get(key);
            if (result == null && Boolean.TRUE.equals(context.get("xwork.NullHandler.createNullObjects")) && this.objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, target, null, false)) {
                Class valueClass = this.objectTypeDeterminer.getElementClass(lastClass, lastProperty, key);
                try {
                    result = this.objectFactory.buildBean(valueClass, (Map<String, Object>)context);
                    map.put(key, result);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        return result;
    }

    private boolean contains(String[] array, String name) {
        for (String anArray : array) {
            if (!anArray.equals(name)) continue;
            return true;
        }
        return false;
    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        LOG.trace("Entering setProperty({},{},{},{})", (Object)context, target, name, value);
        Object key = this.getKey(context, name);
        Map map = (Map)target;
        map.put(key, this.getValue(context, value));
    }

    private Object getValue(Map context, Object value) {
        Class lastClass = (Class)context.get("last.bean.accessed");
        String lastProperty = (String)context.get("last.property.accessed");
        if (lastClass == null || lastProperty == null) {
            return value;
        }
        Class elementClass = this.objectTypeDeterminer.getElementClass(lastClass, lastProperty, null);
        if (elementClass == null) {
            return value;
        }
        return this.xworkConverter.convertValue(context, value, elementClass);
    }

    private Object getKey(Map context, Object name) {
        Class lastClass = (Class)context.get("last.bean.accessed");
        String lastProperty = (String)context.get("last.property.accessed");
        if (lastClass == null || lastProperty == null) {
            return name;
        }
        Class<String> keyClass = this.objectTypeDeterminer.getKeyClass(lastClass, lastProperty);
        if (keyClass == null) {
            keyClass = String.class;
        }
        return this.xworkConverter.convertValue(context, name, keyClass);
    }
}

