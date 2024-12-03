/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.MethodUtils;

class BeanIntrospectionData {
    private final PropertyDescriptor[] descriptors;
    private final Map<String, String> writeMethodNames;

    public BeanIntrospectionData(PropertyDescriptor[] descs) {
        this(descs, BeanIntrospectionData.setUpWriteMethodNames(descs));
    }

    BeanIntrospectionData(PropertyDescriptor[] descs, Map<String, String> writeMethNames) {
        this.descriptors = descs;
        this.writeMethodNames = writeMethNames;
    }

    public PropertyDescriptor[] getDescriptors() {
        return this.descriptors;
    }

    public PropertyDescriptor getDescriptor(String name) {
        for (PropertyDescriptor pd : this.getDescriptors()) {
            if (!name.equals(pd.getName())) continue;
            return pd;
        }
        return null;
    }

    public Method getWriteMethod(Class<?> beanCls, PropertyDescriptor desc) {
        String methodName;
        Method method = desc.getWriteMethod();
        if (method == null && (methodName = this.writeMethodNames.get(desc.getName())) != null && (method = MethodUtils.getAccessibleMethod(beanCls, methodName, desc.getPropertyType())) != null) {
            try {
                desc.setWriteMethod(method);
            }
            catch (IntrospectionException introspectionException) {
                // empty catch block
            }
        }
        return method;
    }

    private static Map<String, String> setUpWriteMethodNames(PropertyDescriptor[] descs) {
        HashMap<String, String> methods = new HashMap<String, String>();
        for (PropertyDescriptor pd : descs) {
            Method method = pd.getWriteMethod();
            if (method == null) continue;
            methods.put(pd.getName(), method.getName());
        }
        return methods;
    }
}

