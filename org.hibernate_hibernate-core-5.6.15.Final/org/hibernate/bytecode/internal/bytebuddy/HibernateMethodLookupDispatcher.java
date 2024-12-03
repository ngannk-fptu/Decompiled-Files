/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.internal.bytebuddy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import org.hibernate.HibernateException;

public class HibernateMethodLookupDispatcher {
    private static final int MIN_STACK_FRAMES = 3;
    private static final int MAX_STACK_FRAMES = 16;
    private static final PrivilegedAction<Class<?>[]> GET_CALLER_STACK_ACTION;
    private static Set<String> authorizedClasses;

    public static Method getDeclaredMethod(final Class<?> type, final String name, final Class<?>[] parameters) {
        PrivilegedAction<Method> getDeclaredMethodAction = new PrivilegedAction<Method>(){

            @Override
            public Method run() {
                try {
                    return type.getDeclaredMethod(name, parameters);
                }
                catch (NoSuchMethodException | SecurityException e) {
                    return null;
                }
            }
        };
        return HibernateMethodLookupDispatcher.doPrivilegedAction(getDeclaredMethodAction);
    }

    public static Method getMethod(final Class<?> type, final String name, final Class<?>[] parameters) {
        PrivilegedAction<Method> getMethodAction = new PrivilegedAction<Method>(){

            @Override
            public Method run() {
                try {
                    return type.getMethod(name, parameters);
                }
                catch (NoSuchMethodException | SecurityException e) {
                    return null;
                }
            }
        };
        return HibernateMethodLookupDispatcher.doPrivilegedAction(getMethodAction);
    }

    private static Method doPrivilegedAction(PrivilegedAction<Method> privilegedAction) {
        Class<?> callerClass = HibernateMethodLookupDispatcher.getCallerClass();
        if (!authorizedClasses.contains(callerClass.getName())) {
            throw new SecurityException("Unauthorized call by class " + callerClass);
        }
        return System.getSecurityManager() != null ? AccessController.doPrivileged(privilegedAction) : privilegedAction.run();
    }

    static void registerAuthorizedClass(String className) {
        authorizedClasses.add(className);
    }

    private static Class<?> getCallerClass() {
        Class<?>[] stackTrace;
        Class<?>[] classArray = stackTrace = System.getSecurityManager() != null ? AccessController.doPrivileged(GET_CALLER_STACK_ACTION) : GET_CALLER_STACK_ACTION.run();
        if (stackTrace.length <= 3) {
            throw new SecurityException("Unable to determine the caller class");
        }
        boolean hibernateMethodLookupDispatcherDetected = false;
        int maxFrames = Math.min(16, stackTrace.length);
        for (int i = 3; i < maxFrames; ++i) {
            if (stackTrace[i].getName().equals(HibernateMethodLookupDispatcher.class.getName())) {
                hibernateMethodLookupDispatcherDetected = true;
                continue;
            }
            if (!hibernateMethodLookupDispatcherDetected) continue;
            return stackTrace[i];
        }
        throw new SecurityException("Unable to determine the caller class");
    }

    static {
        authorizedClasses = ConcurrentHashMap.newKeySet();
        PrivilegedAction<PrivilegedAction<Class<?>[]>> initializeGetCallerStackAction = new PrivilegedAction<PrivilegedAction<Class<?>[]>>(){

            @Override
            public PrivilegedAction<Class<?>[]> run() {
                Class<?> stackWalkerClass = null;
                try {
                    stackWalkerClass = Class.forName("java.lang.StackWalker");
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
                if (stackWalkerClass != null) {
                    try {
                        Class<?> optionClass = Class.forName("java.lang.StackWalker$Option");
                        Object stackWalker = stackWalkerClass.getMethod("getInstance", optionClass).invoke(null, optionClass.getEnumConstants()[0]);
                        Method stackWalkerWalkMethod = stackWalkerClass.getMethod("walk", Function.class);
                        Method stackFrameGetDeclaringClass = Class.forName("java.lang.StackWalker$StackFrame").getMethod("getDeclaringClass", new Class[0]);
                        return new StackWalkerGetCallerStackAction(stackWalker, stackWalkerWalkMethod, stackFrameGetDeclaringClass);
                    }
                    catch (Throwable e) {
                        throw new HibernateException("Unable to initialize the stack walker", e);
                    }
                }
                return new SecurityManagerClassContextGetCallerStackAction();
            }
        };
        GET_CALLER_STACK_ACTION = System.getSecurityManager() != null ? AccessController.doPrivileged(initializeGetCallerStackAction) : (PrivilegedAction)initializeGetCallerStackAction.run();
    }

    private static class StackWalkerGetCallerStackAction
    implements PrivilegedAction<Class<?>[]> {
        private final Object stackWalker;
        private final Method stackWalkerWalkMethod;
        private final Method stackFrameGetDeclaringClass;
        private final Function<Stream, Object> stackFrameExtractFunction = new Function<Stream, Object>(){

            @Override
            public Object apply(Stream stream) {
                return stream.map(stackFrameGetDeclaringClassFunction).limit(16L).toArray(Class[]::new);
            }
        };
        private final Function<Object, Class<?>> stackFrameGetDeclaringClassFunction = new Function<Object, Class<?>>(){

            @Override
            public Class<?> apply(Object t) {
                try {
                    return (Class)stackFrameGetDeclaringClass.invoke(t, new Object[0]);
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new HibernateException("Unable to get stack frame declaring class", e);
                }
            }
        };

        StackWalkerGetCallerStackAction(Object stackWalker, Method stackWalkerWalkMethod, Method stackFrameGetDeclaringClass) {
            this.stackWalker = stackWalker;
            this.stackWalkerWalkMethod = stackWalkerWalkMethod;
            this.stackFrameGetDeclaringClass = stackFrameGetDeclaringClass;
        }

        @Override
        public Class<?>[] run() {
            try {
                return (Class[])this.stackWalkerWalkMethod.invoke(this.stackWalker, this.stackFrameExtractFunction);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new SecurityException("Unable to determine the caller class", e);
            }
        }
    }

    private static class SecurityManagerClassContextGetCallerStackAction
    extends SecurityManager
    implements PrivilegedAction<Class<?>[]> {
        private SecurityManagerClassContextGetCallerStackAction() {
        }

        @Override
        public Class<?>[] run() {
            return this.getClassContext();
        }
    }
}

