/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider.bean;

import com.opensymphony.provider.BeanProvider;
import com.opensymphony.provider.ProviderConfigurationException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

public class DefaultBeanProvider
implements BeanProvider {
    private static String GET = "get";
    private static String SET = "set";
    private static String IS = "is";

    @Override
    public boolean setProperty(Object object, String property, Object value) {
        if (property == null || object == null) {
            return false;
        }
        StringTokenizer st = new StringTokenizer(property, ".");
        if (st.countTokens() == 0) {
            return false;
        }
        Object current = object;
        try {
            int i = 0;
            while (st.hasMoreTokens()) {
                String currentPropertyName = st.nextToken();
                if (i >= st.countTokens()) {
                    try {
                        Class<?> cls = current.getClass();
                        PropertyDescriptor pd = new PropertyDescriptor(currentPropertyName, current.getClass());
                        pd.getWriteMethod().invoke(current, value);
                        return true;
                    }
                    catch (Exception e) {
                        return false;
                    }
                }
                current = this.invokeProperty(current, currentPropertyName);
                ++i;
            }
            return true;
        }
        catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public Object getProperty(Object object, String property) {
        if (property == null || object == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(property, ".");
        if (st.countTokens() == 0) {
            return null;
        }
        Object result = object;
        try {
            while (st.hasMoreTokens()) {
                String currentPropertyName = st.nextToken();
                result = this.invokeProperty(result, currentPropertyName);
            }
            return result;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ProviderConfigurationException {
    }

    private String createMethodName(String prefix, String propertyName) {
        return prefix + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
    }

    private Object invokeProperty(Object obj, String property) {
        if (property == null || property.length() == 0) {
            return null;
        }
        Class<?> cls = obj.getClass();
        Object[] oParams = new Object[]{};
        Class[] cParams = new Class[]{};
        try {
            Method method = cls.getMethod(this.createMethodName(GET, property), cParams);
            return method.invoke(obj, oParams);
        }
        catch (Exception e1) {
            try {
                Method method = cls.getMethod(this.createMethodName(IS, property), cParams);
                return method.invoke(obj, oParams);
            }
            catch (Exception e2) {
                try {
                    Method method = cls.getMethod(property, cParams);
                    return method.invoke(obj, oParams);
                }
                catch (Exception e3) {
                    try {
                        Field field = cls.getField(property);
                        return field.get(obj);
                    }
                    catch (Exception e4) {
                        return null;
                    }
                }
            }
        }
    }
}

