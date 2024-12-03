/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import nonapi.io.github.classgraph.reflection.ReflectionDriver;

class StandardReflectionDriver
extends ReflectionDriver {
    private static Method setAccessibleMethod;
    private static Method trySetAccessibleMethod;
    private static Class<?> accessControllerClass;
    private static Class<?> privilegedActionClass;
    private static Method accessControllerDoPrivileged;

    StandardReflectionDriver() {
    }

    private <T> T doPrivileged(Callable<T> callable) throws Throwable {
        if (accessControllerDoPrivileged != null) {
            Object privilegedAction = Proxy.newProxyInstance(privilegedActionClass.getClassLoader(), new Class[]{privilegedActionClass}, new PrivilegedActionInvocationHandler<T>(callable));
            return (T)accessControllerDoPrivileged.invoke(null, privilegedAction);
        }
        return callable.call();
    }

    private static boolean tryMakeAccessible(AccessibleObject obj) {
        if (trySetAccessibleMethod != null) {
            try {
                return (Boolean)trySetAccessibleMethod.invoke((Object)obj, new Object[0]);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (setAccessibleMethod != null) {
            try {
                setAccessibleMethod.invoke((Object)obj, true);
                return true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return false;
    }

    @Override
    public boolean makeAccessible(Object instance, final AccessibleObject obj) {
        if (this.isAccessible(instance, obj)) {
            return true;
        }
        try {
            return this.doPrivileged(new Callable<Boolean>(){

                @Override
                public Boolean call() throws Exception {
                    return StandardReflectionDriver.tryMakeAccessible(obj);
                }
            });
        }
        catch (Throwable t) {
            return StandardReflectionDriver.tryMakeAccessible(obj);
        }
    }

    @Override
    Class<?> findClass(String className) throws Exception {
        return Class.forName(className);
    }

    @Override
    Method[] getDeclaredMethods(Class<?> cls) throws Exception {
        return cls.getDeclaredMethods();
    }

    @Override
    <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls) throws Exception {
        return cls.getDeclaredConstructors();
    }

    @Override
    Field[] getDeclaredFields(Class<?> cls) throws Exception {
        return cls.getDeclaredFields();
    }

    @Override
    Object getField(Object object, Field field) throws Exception {
        this.makeAccessible(object, field);
        return field.get(object);
    }

    @Override
    void setField(Object object, Field field, Object value) throws Exception {
        this.makeAccessible(object, field);
        field.set(object, value);
    }

    @Override
    Object getStaticField(Field field) throws Exception {
        this.makeAccessible(null, field);
        return field.get(null);
    }

    @Override
    void setStaticField(Field field, Object value) throws Exception {
        this.makeAccessible(null, field);
        field.set(null, value);
    }

    @Override
    Object invokeMethod(Object object, Method method, Object ... args) throws Exception {
        this.makeAccessible(object, method);
        return method.invoke(object, args);
    }

    @Override
    Object invokeStaticMethod(Method method, Object ... args) throws Exception {
        this.makeAccessible(null, method);
        return method.invoke(null, args);
    }

    static {
        try {
            setAccessibleMethod = AccessibleObject.class.getDeclaredMethod("setAccessible", Boolean.TYPE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            trySetAccessibleMethod = AccessibleObject.class.getDeclaredMethod("trySetAccessible", new Class[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            accessControllerClass = Class.forName("java.security.AccessController");
            privilegedActionClass = Class.forName("java.security.PrivilegedAction");
            accessControllerDoPrivileged = accessControllerClass.getMethod("doPrivileged", privilegedActionClass);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private class PrivilegedActionInvocationHandler<T>
    implements InvocationHandler {
        private final Callable<T> callable;

        public PrivilegedActionInvocationHandler(Callable<T> callable) {
            this.callable = callable;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return this.callable.call();
        }
    }
}

