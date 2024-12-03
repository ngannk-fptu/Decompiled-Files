/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.UnaryOperator;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Undefined;

public class JavaToJSONConverters {
    public static final UnaryOperator<Object> STRING = o -> o.toString();
    public static final UnaryOperator<Object> UNDEFINED = o -> Undefined.instance;
    public static final UnaryOperator<Object> EMPTY_OBJECT = o -> Collections.EMPTY_MAP;
    public static final UnaryOperator<Object> THROW_TYPE_ERROR = o -> {
        throw ScriptRuntime.typeErrorById("msg.json.cant.serialize", o.getClass().getName());
    };
    public static final UnaryOperator<Object> BEAN = value -> {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(value.getClass(), Object.class);
        }
        catch (IntrospectionException e) {
            return null;
        }
        LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>();
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            Object propValue;
            if (descriptor.getReadMethod() == null) continue;
            try {
                propValue = descriptor.getReadMethod().invoke(value, new Object[0]);
            }
            catch (Exception e) {
                continue;
            }
            properties.put(descriptor.getName(), propValue);
        }
        if (properties.size() == 0) {
            return null;
        }
        LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
        BeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();
        obj.put("beanClass", beanDescriptor.getBeanClass().getName());
        obj.put("properties", properties);
        return obj;
    };

    private JavaToJSONConverters() {
    }
}

