/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.tagext.BodyContent
 *  javax.servlet.jsp.tagext.BodyTag
 *  javax.servlet.jsp.tagext.Tag
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 */
package org.apache.jasper.runtime;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.runtime.ExceptionUtils;
import org.apache.jasper.runtime.PageContextImpl;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.ServletResponseWrapperInclude;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;

public class JspRuntimeLibrary {
    public static final boolean GRAAL;

    public static Throwable getThrowable(ServletRequest request) {
        Throwable error = (Throwable)request.getAttribute("javax.servlet.error.exception");
        if (error == null && (error = (Throwable)request.getAttribute("javax.servlet.jsp.jspException")) != null) {
            request.setAttribute("javax.servlet.error.exception", (Object)error);
        }
        return error;
    }

    public static boolean coerceToBoolean(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        return Boolean.parseBoolean(s);
    }

    public static byte coerceToByte(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        return Byte.parseByte(s);
    }

    public static char coerceToChar(String s) {
        if (s == null || s.length() == 0) {
            return '\u0000';
        }
        return s.charAt(0);
    }

    public static double coerceToDouble(String s) {
        if (s == null || s.length() == 0) {
            return 0.0;
        }
        return Double.parseDouble(s);
    }

    public static float coerceToFloat(String s) {
        if (s == null || s.length() == 0) {
            return 0.0f;
        }
        return Float.parseFloat(s);
    }

    public static int coerceToInt(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        return Integer.parseInt(s);
    }

    public static short coerceToShort(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        return Short.parseShort(s);
    }

    public static long coerceToLong(String s) {
        if (s == null || s.length() == 0) {
            return 0L;
        }
        return Long.parseLong(s);
    }

    public static Object coerce(String s, Class<?> target) {
        boolean isNullOrEmpty;
        boolean bl = isNullOrEmpty = s == null || s.length() == 0;
        if (target == Boolean.class) {
            if (isNullOrEmpty) {
                s = "false";
            }
            return Boolean.valueOf(s);
        }
        if (target == Byte.class) {
            if (isNullOrEmpty) {
                return (byte)0;
            }
            return Byte.valueOf(s);
        }
        if (target == Character.class) {
            if (isNullOrEmpty) {
                return Character.valueOf('\u0000');
            }
            Character result = Character.valueOf(s.charAt(0));
            return result;
        }
        if (target == Double.class) {
            if (isNullOrEmpty) {
                return 0.0;
            }
            return Double.valueOf(s);
        }
        if (target == Float.class) {
            if (isNullOrEmpty) {
                return Float.valueOf(0.0f);
            }
            return Float.valueOf(s);
        }
        if (target == Integer.class) {
            if (isNullOrEmpty) {
                return 0;
            }
            return Integer.valueOf(s);
        }
        if (target == Short.class) {
            if (isNullOrEmpty) {
                return (short)0;
            }
            return Short.valueOf(s);
        }
        if (target == Long.class) {
            if (isNullOrEmpty) {
                return 0L;
            }
            return Long.valueOf(s);
        }
        return null;
    }

    public static Object convert(String propertyName, String s, Class<?> t, Class<?> propertyEditorClass) throws JasperException {
        try {
            if (s == null) {
                if (t.equals(Boolean.class) || t.equals(Boolean.TYPE)) {
                    s = "false";
                } else {
                    return null;
                }
            }
            if (propertyEditorClass != null) {
                return JspRuntimeLibrary.getValueFromBeanInfoPropertyEditor(t, propertyName, s, propertyEditorClass);
            }
            if (t.equals(Boolean.class) || t.equals(Boolean.TYPE)) {
                return Boolean.valueOf(s);
            }
            if (t.equals(Byte.class) || t.equals(Byte.TYPE)) {
                if (s.length() == 0) {
                    return (byte)0;
                }
                return Byte.valueOf(s);
            }
            if (t.equals(Character.class) || t.equals(Character.TYPE)) {
                if (s.length() == 0) {
                    return Character.valueOf('\u0000');
                }
                return Character.valueOf(s.charAt(0));
            }
            if (t.equals(Double.class) || t.equals(Double.TYPE)) {
                if (s.length() == 0) {
                    return 0.0;
                }
                return Double.valueOf(s);
            }
            if (t.equals(Integer.class) || t.equals(Integer.TYPE)) {
                if (s.length() == 0) {
                    return 0;
                }
                return Integer.valueOf(s);
            }
            if (t.equals(Float.class) || t.equals(Float.TYPE)) {
                if (s.length() == 0) {
                    return Float.valueOf(0.0f);
                }
                return Float.valueOf(s);
            }
            if (t.equals(Long.class) || t.equals(Long.TYPE)) {
                if (s.length() == 0) {
                    return 0L;
                }
                return Long.valueOf(s);
            }
            if (t.equals(Short.class) || t.equals(Short.TYPE)) {
                if (s.length() == 0) {
                    return (short)0;
                }
                return Short.valueOf(s);
            }
            if (t.equals(String.class)) {
                return s;
            }
            if (t.getName().equals("java.lang.Object")) {
                return new String(s);
            }
            return JspRuntimeLibrary.getValueFromPropertyEditorManager(t, propertyName, s);
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void introspect(Object bean, ServletRequest request) throws JasperException {
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            String value = request.getParameter(name);
            JspRuntimeLibrary.introspecthelper(bean, name, value, request, name, true);
        }
    }

    public static void introspecthelper(Object bean, String prop, String value, ServletRequest request, String param, boolean ignoreMethodNF) throws JasperException {
        Method method = null;
        Class<?> type = null;
        Class<?> propertyEditorClass = null;
        try {
            block16: {
                block15: {
                    if (!GRAAL) break block15;
                    method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
                    if (method.getParameterTypes().length <= 0) break block16;
                    type = method.getParameterTypes()[0];
                    break block16;
                }
                BeanInfo info = Introspector.getBeanInfo(bean.getClass());
                if (info != null) {
                    PropertyDescriptor[] pd;
                    for (PropertyDescriptor propertyDescriptor : pd = info.getPropertyDescriptors()) {
                        if (!propertyDescriptor.getName().equals(prop)) continue;
                        method = propertyDescriptor.getWriteMethod();
                        type = propertyDescriptor.getPropertyType();
                        propertyEditorClass = propertyDescriptor.getPropertyEditorClass();
                        break;
                    }
                }
            }
            if (method != null && type != null) {
                if (type.isArray()) {
                    if (request == null) {
                        throw new JasperException(Localizer.getMessage("jsp.error.beans.setproperty.noindexset"));
                    }
                    Class<?> t = type.getComponentType();
                    String[] values = request.getParameterValues(param);
                    if (values == null) {
                        return;
                    }
                    if (t.equals(String.class)) {
                        method.invoke(bean, new Object[]{values});
                    } else {
                        JspRuntimeLibrary.createTypedArray(prop, bean, method, values, t, propertyEditorClass);
                    }
                } else {
                    if (value == null || param != null && value.equals("")) {
                        return;
                    }
                    Object oval = JspRuntimeLibrary.convert(prop, value, type, propertyEditorClass);
                    if (oval != null) {
                        method.invoke(bean, oval);
                    }
                }
            }
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
        if (!ignoreMethodNF && method == null) {
            if (type == null) {
                throw new JasperException(Localizer.getMessage("jsp.error.beans.noproperty", prop, bean.getClass().getName()));
            }
            throw new JasperException(Localizer.getMessage("jsp.error.beans.nomethod.setproperty", prop, type.getName(), bean.getClass().getName()));
        }
    }

    public static String toString(Object o) {
        return String.valueOf(o);
    }

    public static String toString(byte b) {
        return Byte.toString(b);
    }

    public static String toString(boolean b) {
        return Boolean.toString(b);
    }

    public static String toString(short s) {
        return Short.toString(s);
    }

    public static String toString(int i) {
        return Integer.toString(i);
    }

    public static String toString(float f) {
        return Float.toString(f);
    }

    public static String toString(long l) {
        return Long.toString(l);
    }

    public static String toString(double d) {
        return Double.toString(d);
    }

    public static String toString(char c) {
        return Character.toString(c);
    }

    public static void createTypedArray(String propertyName, Object bean, Method method, String[] values, Class<?> t, Class<?> propertyEditorClass) throws JasperException {
        try {
            if (propertyEditorClass != null) {
                Integer[] tmpval = new Integer[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = JspRuntimeLibrary.getValueFromBeanInfoPropertyEditor(t, propertyName, values[i], propertyEditorClass);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Integer.class)) {
                Integer[] tmpval = new Integer[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Integer.valueOf(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Byte.class)) {
                Byte[] tmpval = new Byte[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Byte.valueOf(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Boolean.class)) {
                Boolean[] tmpval = new Boolean[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Boolean.valueOf(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Short.class)) {
                Short[] tmpval = new Short[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Short.valueOf(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Long.class)) {
                Long[] tmpval = new Long[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Long.valueOf(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Double.class)) {
                Double[] tmpval = new Double[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Double.valueOf(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Float.class)) {
                Float[] tmpval = new Float[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Float.valueOf(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Character.class)) {
                Character[] tmpval = new Character[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Character.valueOf(values[i].charAt(0));
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Integer.TYPE)) {
                int[] tmpval = new int[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Integer.parseInt(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Byte.TYPE)) {
                byte[] tmpval = new byte[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Byte.parseByte(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Boolean.TYPE)) {
                boolean[] tmpval = new boolean[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Boolean.parseBoolean(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Short.TYPE)) {
                short[] tmpval = new short[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Short.parseShort(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Long.TYPE)) {
                long[] tmpval = new long[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Long.parseLong(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Double.TYPE)) {
                double[] tmpval = new double[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Double.parseDouble(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Float.TYPE)) {
                float[] tmpval = new float[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Float.parseFloat(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Character.TYPE)) {
                char[] tmpval = new char[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = values[i].charAt(0);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else {
                Integer[] tmpval = new Integer[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = JspRuntimeLibrary.getValueFromPropertyEditorManager(t, propertyName, values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            }
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException("error in invoking method", ex);
        }
    }

    public static String escapeQueryString(String unescString) {
        if (unescString == null) {
            return null;
        }
        StringBuilder escStringBuilder = new StringBuilder();
        String shellSpChars = "&;`'\"|*?~<>^()[]{}$\\\n";
        for (int index = 0; index < unescString.length(); ++index) {
            char nextChar = unescString.charAt(index);
            if (shellSpChars.indexOf(nextChar) != -1) {
                escStringBuilder.append('\\');
            }
            escStringBuilder.append(nextChar);
        }
        return escStringBuilder.toString();
    }

    public static Object handleGetProperty(Object o, String prop) throws JasperException {
        if (o == null) {
            throw new JasperException(Localizer.getMessage("jsp.error.beans.nullbean"));
        }
        Object value = null;
        try {
            Method method = JspRuntimeLibrary.getReadMethod(o.getClass(), prop);
            value = method.invoke(o, (Object[])null);
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
        return value;
    }

    public static void handleSetPropertyExpression(Object bean, String prop, String expression, PageContext pageContext, ProtectedFunctionMapper functionMapper) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, PageContextImpl.proprietaryEvaluate(expression, method.getParameterTypes()[0], pageContext, functionMapper));
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, Object value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, value);
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, int value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, value);
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, short value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, value);
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, long value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, value);
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, double value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, value);
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, float value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, Float.valueOf(value));
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, char value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, Character.valueOf(value));
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, byte value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, value);
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, boolean value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, value);
        }
        catch (Exception ex) {
            Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(thr);
            throw new JasperException(ex);
        }
    }

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static Method getWriteMethod(Class<?> beanClass, String prop) throws JasperException {
        Method result = null;
        Class<?> type = null;
        if (GRAAL) {
            Method[] methods;
            String setter = "set" + JspRuntimeLibrary.capitalize(prop);
            for (Method method : methods = beanClass.getMethods()) {
                if (!setter.equals(method.getName())) continue;
                return method;
            }
        } else {
            try {
                PropertyDescriptor[] pd;
                BeanInfo info = Introspector.getBeanInfo(beanClass);
                for (PropertyDescriptor propertyDescriptor : pd = info.getPropertyDescriptors()) {
                    if (!propertyDescriptor.getName().equals(prop)) continue;
                    result = propertyDescriptor.getWriteMethod();
                    type = propertyDescriptor.getPropertyType();
                }
            }
            catch (Exception ex) {
                throw new JasperException(ex);
            }
        }
        if (result == null) {
            if (type == null) {
                throw new JasperException(Localizer.getMessage("jsp.error.beans.noproperty", prop, beanClass.getName()));
            }
            throw new JasperException(Localizer.getMessage("jsp.error.beans.nomethod.setproperty", prop, type.getName(), beanClass.getName()));
        }
        return result;
    }

    public static Method getReadMethod(Class<?> beanClass, String prop) throws JasperException {
        Method result = null;
        Class<?> type = null;
        if (GRAAL) {
            Method[] methods;
            String setter = "get" + JspRuntimeLibrary.capitalize(prop);
            for (Method method : methods = beanClass.getMethods()) {
                if (!setter.equals(method.getName())) continue;
                return method;
            }
        } else {
            try {
                PropertyDescriptor[] pd;
                BeanInfo info = Introspector.getBeanInfo(beanClass);
                for (PropertyDescriptor propertyDescriptor : pd = info.getPropertyDescriptors()) {
                    if (!propertyDescriptor.getName().equals(prop)) continue;
                    result = propertyDescriptor.getReadMethod();
                    type = propertyDescriptor.getPropertyType();
                }
            }
            catch (Exception ex) {
                throw new JasperException(ex);
            }
        }
        if (result == null) {
            if (type == null) {
                throw new JasperException(Localizer.getMessage("jsp.error.beans.noproperty", prop, beanClass.getName()));
            }
            throw new JasperException(Localizer.getMessage("jsp.error.beans.nomethod", prop, beanClass.getName()));
        }
        return result;
    }

    public static Object getValueFromBeanInfoPropertyEditor(Class<?> attrClass, String attrName, String attrValue, Class<?> propertyEditorClass) throws JasperException {
        try {
            PropertyEditor pe = (PropertyEditor)propertyEditorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            pe.setAsText(attrValue);
            return pe.getValue();
        }
        catch (Exception ex) {
            if (attrValue.length() == 0) {
                return null;
            }
            throw new JasperException(Localizer.getMessage("jsp.error.beans.property.conversion", attrValue, attrClass.getName(), attrName, ex.getMessage()));
        }
    }

    public static Object getValueFromPropertyEditorManager(Class<?> attrClass, String attrName, String attrValue) throws JasperException {
        try {
            PropertyEditor propEditor = PropertyEditorManager.findEditor(attrClass);
            if (propEditor != null) {
                propEditor.setAsText(attrValue);
                return propEditor.getValue();
            }
            if (attrValue.length() == 0) {
                return null;
            }
            throw new IllegalArgumentException(Localizer.getMessage("jsp.error.beans.propertyeditor.notregistered"));
        }
        catch (IllegalArgumentException ex) {
            if (attrValue.length() == 0) {
                return null;
            }
            throw new JasperException(Localizer.getMessage("jsp.error.beans.property.conversion", attrValue, attrClass.getName(), attrName, ex.getMessage()));
        }
    }

    public static String getContextRelativePath(ServletRequest request, String relativePath) {
        if (relativePath.startsWith("/")) {
            return relativePath;
        }
        if (!(request instanceof HttpServletRequest)) {
            return relativePath;
        }
        HttpServletRequest hrequest = (HttpServletRequest)request;
        String uri = (String)request.getAttribute("javax.servlet.include.servlet_path");
        if (uri != null) {
            String pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
            if (pathInfo == null && uri.lastIndexOf(47) >= 0) {
                uri = uri.substring(0, uri.lastIndexOf(47));
            }
        } else {
            uri = hrequest.getServletPath();
            if (uri.lastIndexOf(47) >= 0) {
                uri = uri.substring(0, uri.lastIndexOf(47));
            }
        }
        return uri + '/' + relativePath;
    }

    public static void include(ServletRequest request, ServletResponse response, String relativePath, JspWriter out, boolean flush) throws IOException, ServletException {
        String resourcePath;
        RequestDispatcher rd;
        if (flush && !(out instanceof BodyContent)) {
            out.flush();
        }
        if ((rd = request.getRequestDispatcher(resourcePath = JspRuntimeLibrary.getContextRelativePath(request, relativePath))) == null) {
            throw new JasperException(Localizer.getMessage("jsp.error.include.exception", resourcePath));
        }
        rd.include(request, (ServletResponse)new ServletResponseWrapperInclude(response, out));
    }

    public static String URLEncode(String s, String enc) {
        if (s == null) {
            return "null";
        }
        if (enc == null) {
            enc = "ISO-8859-1";
        }
        StringBuilder out = new StringBuilder(s.length());
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter((OutputStream)buf, enc);
        }
        catch (UnsupportedEncodingException ex) {
            writer = new OutputStreamWriter(buf);
        }
        for (int i = 0; i < s.length(); ++i) {
            byte[] ba;
            char c = s.charAt(i);
            if (c == ' ') {
                out.append('+');
                continue;
            }
            if (JspRuntimeLibrary.isSafeChar(c)) {
                out.append(c);
                continue;
            }
            try {
                writer.write(c);
                writer.flush();
            }
            catch (IOException e) {
                buf.reset();
                continue;
            }
            for (byte b : ba = buf.toByteArray()) {
                out.append('%');
                out.append(Character.forDigit(b >> 4 & 0xF, 16));
                out.append(Character.forDigit(b & 0xF, 16));
            }
            buf.reset();
        }
        return out.toString();
    }

    private static boolean isSafeChar(int c) {
        if (c >= 97 && c <= 122) {
            return true;
        }
        if (c >= 65 && c <= 90) {
            return true;
        }
        if (c >= 48 && c <= 57) {
            return true;
        }
        return c == 45 || c == 95 || c == 46 || c == 33 || c == 126 || c == 42 || c == 39 || c == 40 || c == 41;
    }

    public static JspWriter startBufferedBody(PageContext pageContext, BodyTag tag) throws JspException {
        BodyContent out = pageContext.pushBody();
        tag.setBodyContent(out);
        tag.doInitBody();
        return out;
    }

    public static void releaseTag(Tag tag, InstanceManager instanceManager, boolean reused) {
        if (!reused) {
            JspRuntimeLibrary.releaseTag(tag, instanceManager);
        }
    }

    protected static void releaseTag(Tag tag, InstanceManager instanceManager) {
        try {
            tag.release();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            Log log = LogFactory.getLog(JspRuntimeLibrary.class);
            log.warn((Object)Localizer.getMessage("jsp.warning.tagRelease", tag.getClass().getName()), t);
        }
        try {
            instanceManager.destroyInstance((Object)tag);
        }
        catch (Exception e) {
            Throwable t = ExceptionUtils.unwrapInvocationTargetException(e);
            ExceptionUtils.handleThrowable(t);
            Log log = LogFactory.getLog(JspRuntimeLibrary.class);
            log.warn((Object)Localizer.getMessage("jsp.warning.tagPreDestroy", tag.getClass().getName()), t);
        }
    }

    static {
        boolean result = false;
        try {
            Class<?> nativeImageClazz = Class.forName("org.graalvm.nativeimage.ImageInfo");
            result = nativeImageClazz.getMethod("inImageCode", new Class[0]).invoke(null, new Object[0]) != null;
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (IllegalArgumentException | ReflectiveOperationException exception) {
            // empty catch block
        }
        GRAAL = result || System.getProperty("org.graalvm.nativeimage.imagecode") != null;
    }
}

