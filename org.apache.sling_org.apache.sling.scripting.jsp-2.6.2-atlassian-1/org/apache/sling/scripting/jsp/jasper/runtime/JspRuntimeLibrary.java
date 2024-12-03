/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.tagext.BodyContent
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import org.apache.sling.scripting.jsp.jasper.Constants;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.runtime.PageContextImpl;
import org.apache.sling.scripting.jsp.jasper.runtime.ProtectedFunctionMapper;
import org.apache.sling.scripting.jsp.jasper.runtime.ServletResponseWrapperInclude;

public class JspRuntimeLibrary {
    private static final String SERVLET_EXCEPTION = "javax.servlet.error.exception";
    private static final String JSP_EXCEPTION = "javax.servlet.jsp.jspException";

    public static Throwable getThrowable(ServletRequest request) {
        Throwable error = (Throwable)request.getAttribute(SERVLET_EXCEPTION);
        if (error == null && (error = (Throwable)request.getAttribute(JSP_EXCEPTION)) != null) {
            request.setAttribute(SERVLET_EXCEPTION, (Object)error);
        }
        return error;
    }

    public static boolean coerceToBoolean(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        return Boolean.valueOf(s);
    }

    public static byte coerceToByte(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        return Byte.valueOf(s);
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
        return Double.valueOf(s);
    }

    public static float coerceToFloat(String s) {
        if (s == null || s.length() == 0) {
            return 0.0f;
        }
        return Float.valueOf(s).floatValue();
    }

    public static int coerceToInt(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        return Integer.valueOf(s);
    }

    public static short coerceToShort(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        return Short.valueOf(s);
    }

    public static long coerceToLong(String s) {
        if (s == null || s.length() == 0) {
            return 0L;
        }
        return Long.valueOf(s);
    }

    public static Object coerce(String s, Class target) {
        boolean isNullOrEmpty;
        boolean bl = isNullOrEmpty = s == null || s.length() == 0;
        if (target == Boolean.class) {
            if (isNullOrEmpty) {
                s = "false";
            }
            return new Boolean(s);
        }
        if (target == Byte.class) {
            if (isNullOrEmpty) {
                return new Byte(0);
            }
            return new Byte(s);
        }
        if (target == Character.class) {
            if (isNullOrEmpty) {
                return new Character('\u0000');
            }
            return new Character(s.charAt(0));
        }
        if (target == Double.class) {
            if (isNullOrEmpty) {
                return new Double(0.0);
            }
            return new Double(s);
        }
        if (target == Float.class) {
            if (isNullOrEmpty) {
                return new Float(0.0f);
            }
            return new Float(s);
        }
        if (target == Integer.class) {
            if (isNullOrEmpty) {
                return new Integer(0);
            }
            return new Integer(s);
        }
        if (target == Short.class) {
            if (isNullOrEmpty) {
                return new Short(0);
            }
            return new Short(s);
        }
        if (target == Long.class) {
            if (isNullOrEmpty) {
                return new Long(0L);
            }
            return new Long(s);
        }
        return null;
    }

    public static Object convert(String propertyName, String s, Class t, Class propertyEditorClass) throws JasperException {
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
                s = s.equalsIgnoreCase("on") || s.equalsIgnoreCase("true") ? "true" : "false";
                return new Boolean(s);
            }
            if (t.equals(Byte.class) || t.equals(Byte.TYPE)) {
                return new Byte(s);
            }
            if (t.equals(Character.class) || t.equals(Character.TYPE)) {
                return s.length() > 0 ? new Character(s.charAt(0)) : null;
            }
            if (t.equals(Short.class) || t.equals(Short.TYPE)) {
                return new Short(s);
            }
            if (t.equals(Integer.class) || t.equals(Integer.TYPE)) {
                return new Integer(s);
            }
            if (t.equals(Float.class) || t.equals(Float.TYPE)) {
                return new Float(s);
            }
            if (t.equals(Long.class) || t.equals(Long.TYPE)) {
                return new Long(s);
            }
            if (t.equals(Double.class) || t.equals(Double.TYPE)) {
                return new Double(s);
            }
            if (t.equals(String.class)) {
                return s;
            }
            if (t.equals(File.class)) {
                return new File(s);
            }
            if (t.getName().equals("java.lang.Object")) {
                return new Object[]{s};
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
        if (Constants.IS_SECURITY_ENABLED) {
            try {
                PrivilegedIntrospectHelper dp = new PrivilegedIntrospectHelper(bean, prop, value, request, param, ignoreMethodNF);
                AccessController.doPrivileged(dp);
            }
            catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                throw (JasperException)((Object)e);
            }
        } else {
            JspRuntimeLibrary.internalIntrospecthelper(bean, prop, value, request, param, ignoreMethodNF);
        }
    }

    private static void internalIntrospecthelper(Object bean, String prop, String value, ServletRequest request, String param, boolean ignoreMethodNF) throws JasperException {
        Method method = null;
        Class<?> type = null;
        Class<?> propertyEditorClass = null;
        try {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            if (info != null) {
                PropertyDescriptor[] pd = info.getPropertyDescriptors();
                for (int i = 0; i < pd.length; ++i) {
                    if (!pd[i].getName().equals(prop)) continue;
                    method = pd[i].getWriteMethod();
                    type = pd[i].getPropertyType();
                    propertyEditorClass = pd[i].getPropertyEditorClass();
                    break;
                }
            }
            if (method != null) {
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
                        Object tmpval = null;
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
        return new Byte(b).toString();
    }

    public static String toString(boolean b) {
        return new Boolean(b).toString();
    }

    public static String toString(short s) {
        return new Short(s).toString();
    }

    public static String toString(int i) {
        return new Integer(i).toString();
    }

    public static String toString(float f) {
        return new Float(f).toString();
    }

    public static String toString(long l) {
        return new Long(l).toString();
    }

    public static String toString(double d) {
        return new Double(d).toString();
    }

    public static String toString(char c) {
        return new Character(c).toString();
    }

    public static void createTypedArray(String propertyName, Object bean, Method method, String[] values, Class t, Class propertyEditorClass) throws JasperException {
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
                    tmpval[i] = new Integer(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Byte.class)) {
                Byte[] tmpval = new Byte[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = new Byte(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Boolean.class)) {
                Boolean[] tmpval = new Boolean[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = new Boolean(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Short.class)) {
                Short[] tmpval = new Short[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = new Short(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Long.class)) {
                Long[] tmpval = new Long[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = new Long(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Double.class)) {
                Double[] tmpval = new Double[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = new Double(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Float.class)) {
                Float[] tmpval = new Float[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = new Float(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Character.class)) {
                Character[] tmpval = new Character[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = new Character(values[i].charAt(0));
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
                    tmpval[i] = Boolean.valueOf(values[i]);
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
                    tmpval[i] = Double.valueOf(values[i]);
                }
                method.invoke(bean, new Object[]{tmpval});
            } else if (t.equals(Float.TYPE)) {
                float[] tmpval = new float[values.length];
                for (int i = 0; i < values.length; ++i) {
                    tmpval[i] = Float.valueOf(values[i]).floatValue();
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
        catch (Exception ex) {
            throw new JasperException("error in invoking method", ex);
        }
    }

    public static String escapeQueryString(String unescString) {
        if (unescString == null) {
            return null;
        }
        String escString = "";
        String shellSpChars = "&;`'\"|*?~<>^()[]{}$\\\n";
        for (int index = 0; index < unescString.length(); ++index) {
            char nextChar = unescString.charAt(index);
            if (shellSpChars.indexOf(nextChar) != -1) {
                escString = escString + "\\";
            }
            escString = escString + nextChar;
        }
        return escString;
    }

    public static String decode(String encoded) {
        if (encoded == null) {
            return null;
        }
        if (encoded.indexOf(37) == -1 && encoded.indexOf(43) == -1) {
            return encoded;
        }
        byte[] holdbuffer = new byte[encoded.length()];
        int bufcount = 0;
        for (int count = 0; count < encoded.length(); ++count) {
            char cur = encoded.charAt(count);
            if (cur == '%') {
                holdbuffer[bufcount++] = (byte)Integer.parseInt(encoded.substring(count + 1, count + 3), 16);
                if (count + 2 >= encoded.length()) {
                    count = encoded.length();
                    continue;
                }
                count += 2;
                continue;
            }
            holdbuffer[bufcount++] = cur == '+' ? 32 : (byte)cur;
        }
        return new String(holdbuffer, 0, bufcount);
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
            throw new JasperException(ex);
        }
        return value;
    }

    public static void handleSetPropertyExpression(Object bean, String prop, String expression, PageContext pageContext, ProtectedFunctionMapper functionMapper) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, PageContextImpl.proprietaryEvaluate(expression, method.getParameterTypes()[0], pageContext, functionMapper, false));
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, Object value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, value);
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, int value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, new Integer(value));
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, short value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, new Short(value));
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, long value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, new Long(value));
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, double value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, new Double(value));
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, float value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, new Float(value));
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, char value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, new Character(value));
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, byte value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, new Byte(value));
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static void handleSetProperty(Object bean, String prop, boolean value) throws JasperException {
        try {
            Method method = JspRuntimeLibrary.getWriteMethod(bean.getClass(), prop);
            method.invoke(bean, new Boolean(value));
        }
        catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public static Method getWriteMethod(Class beanClass, String prop) throws JasperException {
        Class<?> type;
        Method method;
        block6: {
            method = null;
            type = null;
            try {
                BeanInfo info = Introspector.getBeanInfo(beanClass);
                if (info != null) {
                    PropertyDescriptor[] pd = info.getPropertyDescriptors();
                    for (int i = 0; i < pd.length; ++i) {
                        if (!pd[i].getName().equals(prop)) continue;
                        method = pd[i].getWriteMethod();
                        type = pd[i].getPropertyType();
                        break block6;
                    }
                    break block6;
                }
                throw new JasperException(Localizer.getMessage("jsp.error.beans.nobeaninfo", beanClass.getName()));
            }
            catch (Exception ex) {
                throw new JasperException(ex);
            }
        }
        if (method == null) {
            if (type == null) {
                throw new JasperException(Localizer.getMessage("jsp.error.beans.noproperty", prop, beanClass.getName()));
            }
            throw new JasperException(Localizer.getMessage("jsp.error.beans.nomethod.setproperty", prop, type.getName(), beanClass.getName()));
        }
        return method;
    }

    public static Method getReadMethod(Class beanClass, String prop) throws JasperException {
        Class<?> type;
        Method method;
        block6: {
            method = null;
            type = null;
            try {
                BeanInfo info = Introspector.getBeanInfo(beanClass);
                if (info != null) {
                    PropertyDescriptor[] pd = info.getPropertyDescriptors();
                    for (int i = 0; i < pd.length; ++i) {
                        if (!pd[i].getName().equals(prop)) continue;
                        method = pd[i].getReadMethod();
                        type = pd[i].getPropertyType();
                        break block6;
                    }
                    break block6;
                }
                throw new JasperException(Localizer.getMessage("jsp.error.beans.nobeaninfo", beanClass.getName()));
            }
            catch (Exception ex) {
                throw new JasperException(ex);
            }
        }
        if (method == null) {
            if (type == null) {
                throw new JasperException(Localizer.getMessage("jsp.error.beans.noproperty", prop, beanClass.getName()));
            }
            throw new JasperException(Localizer.getMessage("jsp.error.beans.nomethod", prop, beanClass.getName()));
        }
        return method;
    }

    public static Object getValueFromBeanInfoPropertyEditor(Class attrClass, String attrName, String attrValue, Class propertyEditorClass) throws JasperException {
        try {
            PropertyEditor pe = (PropertyEditor)propertyEditorClass.newInstance();
            pe.setAsText(attrValue);
            return pe.getValue();
        }
        catch (Exception ex) {
            throw new JasperException(Localizer.getMessage("jsp.error.beans.property.conversion", attrValue, attrClass.getName(), attrName, ex.getMessage()));
        }
    }

    public static Object getValueFromPropertyEditorManager(Class attrClass, String attrName, String attrValue) throws JasperException {
        try {
            PropertyEditor propEditor = PropertyEditorManager.findEditor(attrClass);
            if (propEditor != null) {
                propEditor.setAsText(attrValue);
                return propEditor.getValue();
            }
            throw new IllegalArgumentException(Localizer.getMessage("jsp.error.beans.propertyeditor.notregistered"));
        }
        catch (IllegalArgumentException ex) {
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
        if (flush && !(out instanceof BodyContent)) {
            out.flush();
        }
        String resourcePath = JspRuntimeLibrary.getContextRelativePath(request, relativePath);
        RequestDispatcher rd = request.getRequestDispatcher(resourcePath);
        rd.include(request, (ServletResponse)new ServletResponseWrapperInclude(response, out));
    }

    public static String URLEncode(String s, String enc) {
        if (s == null) {
            return "null";
        }
        if (enc == null) {
            enc = "ISO-8859-1";
        }
        StringBuffer out = new StringBuffer(s.length());
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter((OutputStream)buf, enc);
        }
        catch (UnsupportedEncodingException ex) {
            writer = new OutputStreamWriter(buf);
        }
        for (int i = 0; i < s.length(); ++i) {
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
            byte[] ba = buf.toByteArray();
            for (int j = 0; j < ba.length; ++j) {
                out.append('%');
                out.append(Character.forDigit(ba[j] >> 4 & 0xF, 16));
                out.append(Character.forDigit(ba[j] & 0xF, 16));
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

    protected static class PrivilegedIntrospectHelper
    implements PrivilegedExceptionAction {
        private Object bean;
        private String prop;
        private String value;
        private ServletRequest request;
        private String param;
        private boolean ignoreMethodNF;

        PrivilegedIntrospectHelper(Object bean, String prop, String value, ServletRequest request, String param, boolean ignoreMethodNF) {
            this.bean = bean;
            this.prop = prop;
            this.value = value;
            this.request = request;
            this.param = param;
            this.ignoreMethodNF = ignoreMethodNF;
        }

        public Object run() throws JasperException {
            JspRuntimeLibrary.internalIntrospecthelper(this.bean, this.prop, this.value, this.request, this.param, this.ignoreMethodNF);
            return null;
        }
    }
}

