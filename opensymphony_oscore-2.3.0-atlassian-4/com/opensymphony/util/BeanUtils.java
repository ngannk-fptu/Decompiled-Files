/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package com.opensymphony.util;

import com.opensymphony.provider.BeanProvider;
import com.opensymphony.provider.ProviderFactory;
import com.opensymphony.provider.bean.DefaultBeanProvider;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletRequest;

public class BeanUtils {
    private static final BeanProvider beanProvider;

    public static final String[] getPropertyNames(Object obj) {
        try {
            BeanInfo info = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] properties = info.getPropertyDescriptors();
            String[] result = new String[properties.length];
            for (int i = 0; i < properties.length; ++i) {
                result[i] = properties[i].getName();
            }
            return result;
        }
        catch (IntrospectionException e) {
            return null;
        }
    }

    public static final boolean setValue(Object obj, String property, Object value) {
        return beanProvider.setProperty(obj, property, value);
    }

    public static final Object getValue(Object obj, String property) {
        return beanProvider.getProperty(obj, property);
    }

    public static final void setValues(Object obj, Map valueMap, String[] allowedProperties) {
        Iterator keys = valueMap.keySet().iterator();
        while (keys.hasNext()) {
            String property = keys.next().toString();
            Object value = valueMap.get(property);
            if (!BeanUtils.allowed(property, allowedProperties)) continue;
            BeanUtils.setValue(obj, property, value);
        }
    }

    public static final void setValues(Object obj, Object src, String[] allowedProperties) {
        BeanUtils.setValues(obj, BeanUtils.getValues(src, allowedProperties), allowedProperties);
    }

    public static final void setValues(Object obj, ServletRequest request, String[] allowedProperties) {
        HashMap<String, String> params = new HashMap<String, String>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            params.put(paramName, paramValue);
        }
        BeanUtils.setValues(obj, params, allowedProperties);
    }

    public static final Map getValues(Object obj, String[] allowedProperties) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String[] propertyNames = BeanUtils.getPropertyNames(obj);
        for (int i = 0; i < propertyNames.length; ++i) {
            String propertyName = propertyNames[i];
            Object propertyValue = BeanUtils.getValue(obj, propertyName);
            if (propertyName == null || propertyValue == null || !BeanUtils.allowed(propertyName, allowedProperties)) continue;
            result.put(propertyName, propertyValue);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final boolean allowed(String string, String[] array) {
        if (array == null) {
            return true;
        }
        if (string == null) {
            return false;
        }
        String[] stringArray = array;
        synchronized (array) {
            for (int i = 0; i < array.length; ++i) {
                if (!string.equals(array[i])) continue;
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return true;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return false;
        }
    }

    private static final void providerModify() {
        HashMap<String, String> providerAliases = new HashMap<String, String>();
        providerAliases.put("default", "com.opensymphony.provider.bean.DefaultBeanProvider");
        providerAliases.put("ognl", "com.opensymphony.provider.bean.OGNLBeanProvider");
        if (System.getProperty("bean.provider") != null && providerAliases.containsKey(System.getProperty("bean.provider"))) {
            System.setProperty("bean.provider", (String)providerAliases.get(System.getProperty("bean.provider")));
        }
    }

    static {
        ProviderFactory factory = ProviderFactory.getInstance();
        BeanUtils.providerModify();
        beanProvider = (BeanProvider)factory.getProvider("bean.provider", DefaultBeanProvider.class.getName());
    }
}

