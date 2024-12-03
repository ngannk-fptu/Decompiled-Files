/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bsh.EvalError
 *  bsh.Interpreter
 *  bsh.Primitive
 *  bsh.XThis
 *  org.springframework.core.NestedRuntimeException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.scripting.bsh;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.Primitive;
import bsh.XThis;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

public abstract class BshScriptUtils {
    public static Object createBshObject(String scriptSource) throws EvalError {
        return BshScriptUtils.createBshObject(scriptSource, null, null);
    }

    public static Object createBshObject(String scriptSource, Class<?> ... scriptInterfaces) throws EvalError {
        return BshScriptUtils.createBshObject(scriptSource, scriptInterfaces, ClassUtils.getDefaultClassLoader());
    }

    public static Object createBshObject(String scriptSource, @Nullable Class<?>[] scriptInterfaces, @Nullable ClassLoader classLoader) throws EvalError {
        Object result = BshScriptUtils.evaluateBshScript(scriptSource, scriptInterfaces, classLoader);
        if (result instanceof Class) {
            Class clazz = (Class)result;
            try {
                return ReflectionUtils.accessibleConstructor((Class)clazz, (Class[])new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Could not instantiate script class: " + clazz.getName(), ex);
            }
        }
        return result;
    }

    @Nullable
    static Class<?> determineBshObjectType(String scriptSource, @Nullable ClassLoader classLoader) throws EvalError {
        Object result;
        Assert.hasText((String)scriptSource, (String)"Script source must not be empty");
        Interpreter interpreter = new Interpreter();
        if (classLoader != null) {
            interpreter.setClassLoader(classLoader);
        }
        if ((result = interpreter.eval(scriptSource)) instanceof Class) {
            return (Class)result;
        }
        if (result != null) {
            return result.getClass();
        }
        return null;
    }

    static Object evaluateBshScript(String scriptSource, @Nullable Class<?>[] scriptInterfaces, @Nullable ClassLoader classLoader) throws EvalError {
        Assert.hasText((String)scriptSource, (String)"Script source must not be empty");
        Interpreter interpreter = new Interpreter();
        interpreter.setClassLoader(classLoader);
        Object result = interpreter.eval(scriptSource);
        if (result != null) {
            return result;
        }
        if (ObjectUtils.isEmpty((Object[])scriptInterfaces)) {
            throw new IllegalArgumentException("Given script requires a script proxy: At least one script interface is required.\nScript: " + scriptSource);
        }
        XThis xt = (XThis)interpreter.eval("return this");
        return Proxy.newProxyInstance(classLoader, scriptInterfaces, (InvocationHandler)new BshObjectInvocationHandler(xt));
    }

    public static final class BshExecutionException
    extends NestedRuntimeException {
        private BshExecutionException(EvalError ex) {
            super("BeanShell script execution failed", (Throwable)ex);
        }
    }

    private static class BshObjectInvocationHandler
    implements InvocationHandler {
        private final XThis xt;

        public BshObjectInvocationHandler(XThis xt) {
            this.xt = xt;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod((Method)method)) {
                return this.isProxyForSameBshObject(args[0]);
            }
            if (ReflectionUtils.isHashCodeMethod((Method)method)) {
                return this.xt.hashCode();
            }
            if (ReflectionUtils.isToStringMethod((Method)method)) {
                return "BeanShell object [" + this.xt + "]";
            }
            try {
                Object result = this.xt.invokeMethod(method.getName(), args);
                if (result == Primitive.NULL || result == Primitive.VOID) {
                    return null;
                }
                if (result instanceof Primitive) {
                    return ((Primitive)result).getValue();
                }
                return result;
            }
            catch (EvalError ex) {
                throw new BshExecutionException(ex);
            }
        }

        private boolean isProxyForSameBshObject(Object other) {
            if (!Proxy.isProxyClass(other.getClass())) {
                return false;
            }
            InvocationHandler ih = Proxy.getInvocationHandler(other);
            return ih instanceof BshObjectInvocationHandler && this.xt.equals(((BshObjectInvocationHandler)ih).xt);
        }
    }
}

