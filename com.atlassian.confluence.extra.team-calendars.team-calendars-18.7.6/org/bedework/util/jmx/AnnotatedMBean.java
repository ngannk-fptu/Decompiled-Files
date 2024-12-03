/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.jmx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import org.bedework.util.jmx.MBeanInfo;
import org.bedework.util.jmx.ManagementContext;

public class AnnotatedMBean
extends StandardMBean {
    private static final Map<String, Class<?>> primitives;

    public static void registerMBean(ManagementContext context, Object object, ObjectName objectName) throws Exception {
        String mbeanName = object.getClass().getName() + "MBean";
        for (Class<?> c : object.getClass().getInterfaces()) {
            if (!mbeanName.equals(c.getName())) continue;
            context.registerMBean(new AnnotatedMBean(object, c), objectName);
            return;
        }
        context.registerMBean(object, objectName);
    }

    public <T> AnnotatedMBean(T impl, Class<T> mbeanInterface) throws NotCompliantMBeanException {
        super(impl, mbeanInterface);
    }

    protected AnnotatedMBean(Class<?> mbeanInterface) throws NotCompliantMBeanException {
        super(mbeanInterface);
    }

    @Override
    protected String getDescription(MBeanAttributeInfo info) {
        MBeanInfo d;
        String descr = info.getDescription();
        Method m = AnnotatedMBean.getMethod(this.getMBeanInterface(), "get" + info.getName().substring(0, 1).toUpperCase() + info.getName().substring(1), new String[0]);
        if (m == null) {
            m = AnnotatedMBean.getMethod(this.getMBeanInterface(), "is" + info.getName().substring(0, 1).toUpperCase() + info.getName().substring(1), new String[0]);
        }
        if (m == null) {
            m = AnnotatedMBean.getMethod(this.getMBeanInterface(), "does" + info.getName().substring(0, 1).toUpperCase() + info.getName().substring(1), new String[0]);
        }
        if (m != null && (d = m.getAnnotation(MBeanInfo.class)) != null) {
            descr = d.value();
        }
        return descr;
    }

    @Override
    protected String getDescription(MBeanOperationInfo op) {
        MBeanInfo d;
        String descr = op.getDescription();
        Method m = this.getMethod(op);
        if (m != null && (d = m.getAnnotation(MBeanInfo.class)) != null) {
            descr = d.value();
        }
        return descr;
    }

    @Override
    protected String getParameterName(MBeanOperationInfo op, MBeanParameterInfo param, int paramNo) {
        String name = param.getName();
        Method m = this.getMethod(op);
        if (m != null) {
            for (Annotation a : m.getParameterAnnotations()[paramNo]) {
                if (!MBeanInfo.class.isInstance(a)) continue;
                name = ((MBeanInfo)MBeanInfo.class.cast(a)).value();
            }
        }
        return name;
    }

    private Method getMethod(MBeanOperationInfo op) {
        MBeanParameterInfo[] params = op.getSignature();
        String[] paramTypes = new String[params.length];
        for (int i = 0; i < params.length; ++i) {
            paramTypes[i] = params[i].getType();
        }
        return AnnotatedMBean.getMethod(this.getMBeanInterface(), op.getName(), paramTypes);
    }

    private static Method getMethod(Class<?> mbean, String method, String ... params) {
        try {
            ClassLoader loader = mbean.getClassLoader();
            Class[] paramClasses = new Class[params.length];
            for (int i = 0; i < params.length; ++i) {
                paramClasses[i] = primitives.get(params[i]);
                if (paramClasses[i] != null) continue;
                paramClasses[i] = Class.forName(params[i], false, loader);
            }
            return mbean.getMethod(method, paramClasses);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            return null;
        }
    }

    static {
        Class[] p;
        primitives = new HashMap();
        for (Class c : p = new Class[]{Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Character.TYPE, Boolean.TYPE}) {
            primitives.put(c.getName(), c);
        }
    }
}

