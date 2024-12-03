/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.reflection;

import io.github.classgraph.ClassGraph;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import nonapi.io.github.classgraph.reflection.JVMDriverReflectionDriver;
import nonapi.io.github.classgraph.reflection.NarcissusReflectionDriver;
import nonapi.io.github.classgraph.reflection.ReflectionDriver;
import nonapi.io.github.classgraph.reflection.StandardReflectionDriver;

public final class ReflectionUtils {
    public ReflectionDriver reflectionDriver;
    private Class<?> accessControllerClass;
    private Class<?> privilegedActionClass;
    private Method accessControllerDoPrivileged;

    public ReflectionUtils() {
        if (ClassGraph.CIRCUMVENT_ENCAPSULATION == ClassGraph.CircumventEncapsulationMethod.NARCISSUS) {
            try {
                this.reflectionDriver = new NarcissusReflectionDriver();
            }
            catch (Throwable t) {
                System.err.println("Could not load Narcissus reflection driver: " + t);
            }
        } else if (ClassGraph.CIRCUMVENT_ENCAPSULATION == ClassGraph.CircumventEncapsulationMethod.JVM_DRIVER) {
            try {
                this.reflectionDriver = new JVMDriverReflectionDriver();
            }
            catch (Throwable t) {
                System.err.println("Could not load JVM-Driver reflection driver: " + t);
            }
        }
        if (this.reflectionDriver == null) {
            this.reflectionDriver = new StandardReflectionDriver();
        }
        try {
            this.accessControllerClass = this.reflectionDriver.findClass("java.security.AccessController");
            this.privilegedActionClass = this.reflectionDriver.findClass("java.security.PrivilegedAction");
            this.accessControllerDoPrivileged = this.reflectionDriver.findMethod(this.accessControllerClass, null, "doPrivileged", this.privilegedActionClass);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public Object getFieldVal(boolean throwException, Object obj, Field field) throws IllegalArgumentException {
        if (this.reflectionDriver == null) {
            throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
        }
        if (obj == null || field == null) {
            if (throwException) {
                throw new NullPointerException();
            }
            return null;
        }
        try {
            return this.reflectionDriver.getField(obj, field);
        }
        catch (Throwable e) {
            if (throwException) {
                throw new IllegalArgumentException("Can't read field " + obj.getClass().getName() + "." + field.getName(), e);
            }
            return null;
        }
    }

    public Object getFieldVal(boolean throwException, Object obj, String fieldName) throws IllegalArgumentException {
        if (this.reflectionDriver == null) {
            throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
        }
        if (obj == null || fieldName == null) {
            if (throwException) {
                throw new NullPointerException();
            }
            return null;
        }
        try {
            return this.reflectionDriver.getField(obj, this.reflectionDriver.findInstanceField(obj, fieldName));
        }
        catch (Throwable e) {
            if (throwException) {
                throw new IllegalArgumentException("Can't read field " + obj.getClass().getName() + "." + fieldName, e);
            }
            return null;
        }
    }

    public Object getStaticFieldVal(boolean throwException, Class<?> cls, String fieldName) throws IllegalArgumentException {
        if (this.reflectionDriver == null) {
            throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
        }
        if (cls == null || fieldName == null) {
            if (throwException) {
                throw new NullPointerException();
            }
            return null;
        }
        try {
            return this.reflectionDriver.getStaticField(this.reflectionDriver.findStaticField(cls, fieldName));
        }
        catch (Throwable e) {
            if (throwException) {
                throw new IllegalArgumentException("Can't read field " + cls.getName() + "." + fieldName, e);
            }
            return null;
        }
    }

    public Object invokeMethod(boolean throwException, Object obj, String methodName) throws IllegalArgumentException {
        if (this.reflectionDriver == null) {
            throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
        }
        if (obj == null || methodName == null) {
            if (throwException) {
                throw new IllegalArgumentException("Unexpected null argument");
            }
            return null;
        }
        try {
            return this.reflectionDriver.invokeMethod(obj, this.reflectionDriver.findInstanceMethod(obj, methodName, new Class[0]), new Object[0]);
        }
        catch (Throwable e) {
            if (throwException) {
                throw new IllegalArgumentException("Method \"" + methodName + "\" could not be invoked", e);
            }
            return null;
        }
    }

    public Object invokeMethod(boolean throwException, Object obj, String methodName, Class<?> argType, Object param) throws IllegalArgumentException {
        if (this.reflectionDriver == null) {
            throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
        }
        if (obj == null || methodName == null || argType == null) {
            if (throwException) {
                throw new IllegalArgumentException("Unexpected null argument");
            }
            return null;
        }
        try {
            return this.reflectionDriver.invokeMethod(obj, this.reflectionDriver.findInstanceMethod(obj, methodName, argType), param);
        }
        catch (Throwable e) {
            if (throwException) {
                throw new IllegalArgumentException("Method \"" + methodName + "\" could not be invoked", e);
            }
            return null;
        }
    }

    public Object invokeStaticMethod(boolean throwException, Class<?> cls, String methodName) throws IllegalArgumentException {
        if (this.reflectionDriver == null) {
            throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
        }
        if (cls == null || methodName == null) {
            if (throwException) {
                throw new IllegalArgumentException("Unexpected null argument");
            }
            return null;
        }
        try {
            return this.reflectionDriver.invokeStaticMethod(this.reflectionDriver.findStaticMethod(cls, methodName, new Class[0]), new Object[0]);
        }
        catch (Throwable e) {
            if (throwException) {
                throw new IllegalArgumentException("Method \"" + methodName + "\" could not be invoked", e);
            }
            return null;
        }
    }

    public Object invokeStaticMethod(boolean throwException, Class<?> cls, String methodName, Class<?> argType, Object param) throws IllegalArgumentException {
        if (this.reflectionDriver == null) {
            throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
        }
        if (cls == null || methodName == null || argType == null) {
            if (throwException) {
                throw new IllegalArgumentException("Unexpected null argument");
            }
            return null;
        }
        try {
            return this.reflectionDriver.invokeStaticMethod(this.reflectionDriver.findStaticMethod(cls, methodName, argType), param);
        }
        catch (Throwable e) {
            if (throwException) {
                throw new IllegalArgumentException("Fethod \"" + methodName + "\" could not be invoked", e);
            }
            return null;
        }
    }

    public Class<?> classForNameOrNull(String className) {
        if (this.reflectionDriver == null) {
            throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
        }
        try {
            return this.reflectionDriver.findClass(className);
        }
        catch (Throwable e) {
            return null;
        }
    }

    public Method staticMethodForNameOrNull(String className, String staticMethodName) {
        if (this.reflectionDriver == null) {
            throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
        }
        try {
            return this.reflectionDriver.findStaticMethod(this.reflectionDriver.findClass(className), staticMethodName, new Class[0]);
        }
        catch (Throwable e) {
            return null;
        }
    }

    public <T> T doPrivileged(Callable<T> callable) throws Throwable {
        if (this.accessControllerDoPrivileged != null) {
            Object privilegedAction = Proxy.newProxyInstance(this.privilegedActionClass.getClassLoader(), new Class[]{this.privilegedActionClass}, new PrivilegedActionInvocationHandler<T>(callable));
            return (T)this.accessControllerDoPrivileged.invoke(null, privilegedAction);
        }
        return callable.call();
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

