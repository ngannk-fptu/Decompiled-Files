/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

import java.io.InputStream;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.parser.node.ASTMethod;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.IntrospectionCacheData;
import org.apache.velocity.util.introspection.VelMethod;

public class ClassUtils {
    private ClassUtils() {
    }

    public static Class getClass(String clazz) throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                return Class.forName(clazz, true, loader);
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return Class.forName(clazz);
    }

    public static Object getNewInstance(String clazz) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return ClassUtils.getClass(clazz).newInstance();
    }

    public static InputStream getResourceAsStream(Class claz, String name) {
        InputStream result = null;
        while (name.startsWith("/")) {
            name = name.substring(1);
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = claz.getClassLoader();
            result = classLoader.getResourceAsStream(name);
        } else {
            result = classLoader.getResourceAsStream(name);
            if (result == null && (classLoader = claz.getClassLoader()) != null) {
                result = classLoader.getResourceAsStream(name);
            }
        }
        return result;
    }

    public static VelMethod getMethod(String methodName, Object[] params, Class[] paramClasses, Object o, InternalContextAdapter context, SimpleNode node, boolean strictRef) {
        VelMethod method = null;
        try {
            ASTMethod.MethodCacheKey mck = new ASTMethod.MethodCacheKey(methodName, paramClasses);
            IntrospectionCacheData icd = context.icacheGet(mck);
            if (icd != null && o != null && icd.contextData == o.getClass()) {
                method = (VelMethod)icd.thingy;
            } else {
                method = node.getRuntimeServices().getUberspect().getMethod(o, methodName, params, new Info(node.getTemplateName(), node.getLine(), node.getColumn()));
                if (method != null && o != null) {
                    icd = new IntrospectionCacheData();
                    icd.contextData = o.getClass();
                    icd.thingy = method;
                    context.icachePut(mck, icd);
                }
            }
            if (method == null) {
                if (strictRef) {
                    StringBuffer plist = new StringBuffer();
                    for (int i = 0; i < params.length; ++i) {
                        Class param = paramClasses[i];
                        plist.append(param == null ? "null" : param.getName());
                        if (i >= params.length - 1) continue;
                        plist.append(", ");
                    }
                    throw new MethodInvocationException("Object '" + o.getClass().getName() + "' does not contain method " + methodName + "(" + plist + ")", null, methodName, node.getTemplateName(), node.getLine(), node.getColumn());
                }
                return null;
            }
        }
        catch (MethodInvocationException mie) {
            throw mie;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "ASTMethod.execute() : exception from introspection";
            throw new VelocityException(msg, e);
        }
        return method;
    }
}

