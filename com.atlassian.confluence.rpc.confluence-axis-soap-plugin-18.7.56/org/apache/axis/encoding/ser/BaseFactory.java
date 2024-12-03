/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.lang.reflect.Method;
import org.apache.axis.utils.cache.MethodCache;

public abstract class BaseFactory {
    private static final Class[] STRING_CLASS_QNAME_CLASS = new Class[]{class$java$lang$String == null ? (class$java$lang$String = BaseFactory.class$("java.lang.String")) : class$java$lang$String, class$java$lang$Class == null ? (class$java$lang$Class = BaseFactory.class$("java.lang.Class")) : class$java$lang$Class, class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = BaseFactory.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName};
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$javax$xml$namespace$QName;

    protected Method getMethod(Class clazz, String methodName) {
        Method method = null;
        try {
            method = MethodCache.getInstance().getMethod(clazz, methodName, STRING_CLASS_QNAME_CLASS);
        }
        catch (NoSuchMethodException e) {
            // empty catch block
        }
        return method;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

