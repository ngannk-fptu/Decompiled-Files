/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.commons.beanutils.BeanIntrospector;
import org.apache.commons.beanutils.IntrospectionContext;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultBeanIntrospector
implements BeanIntrospector {
    public static final BeanIntrospector INSTANCE = new DefaultBeanIntrospector();
    private static final Class<?>[] EMPTY_CLASS_PARAMETERS = new Class[0];
    private static final Class<?>[] LIST_CLASS_PARAMETER = new Class[]{List.class};
    private final Log log = LogFactory.getLog(this.getClass());

    private DefaultBeanIntrospector() {
    }

    @Override
    public void introspect(IntrospectionContext icontext) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(icontext.getTargetClass());
        }
        catch (IntrospectionException e) {
            this.log.error((Object)("Error when inspecting class " + icontext.getTargetClass()), (Throwable)e);
            return;
        }
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[]{};
        }
        this.handleIndexedPropertyDescriptors(icontext.getTargetClass(), descriptors);
        icontext.addPropertyDescriptors(descriptors);
    }

    private void handleIndexedPropertyDescriptors(Class<?> beanClass, PropertyDescriptor[] descriptors) {
        for (PropertyDescriptor pd : descriptors) {
            String methodName;
            Method readMethod;
            if (!(pd instanceof IndexedPropertyDescriptor)) continue;
            IndexedPropertyDescriptor descriptor = (IndexedPropertyDescriptor)pd;
            String propName = descriptor.getName().substring(0, 1).toUpperCase() + descriptor.getName().substring(1);
            if (descriptor.getReadMethod() == null && (readMethod = MethodUtils.getMatchingAccessibleMethod(beanClass, methodName = descriptor.getIndexedReadMethod() != null ? descriptor.getIndexedReadMethod().getName() : "get" + propName, EMPTY_CLASS_PARAMETERS)) != null) {
                try {
                    descriptor.setReadMethod(readMethod);
                }
                catch (Exception e) {
                    this.log.error((Object)"Error setting indexed property read method", (Throwable)e);
                }
            }
            if (descriptor.getWriteMethod() != null) continue;
            methodName = descriptor.getIndexedWriteMethod() != null ? descriptor.getIndexedWriteMethod().getName() : "set" + propName;
            Method writeMethod = MethodUtils.getMatchingAccessibleMethod(beanClass, methodName, LIST_CLASS_PARAMETER);
            if (writeMethod == null) {
                for (Method m : beanClass.getMethods()) {
                    Class<?>[] parameterTypes;
                    if (!m.getName().equals(methodName) || (parameterTypes = m.getParameterTypes()).length != 1 || !List.class.isAssignableFrom(parameterTypes[0])) continue;
                    writeMethod = m;
                    break;
                }
            }
            if (writeMethod == null) continue;
            try {
                descriptor.setWriteMethod(writeMethod);
            }
            catch (Exception e) {
                this.log.error((Object)"Error setting indexed property write method", (Throwable)e);
            }
        }
    }
}

