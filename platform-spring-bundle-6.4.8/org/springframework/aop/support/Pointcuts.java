/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.Assert;

public abstract class Pointcuts {
    public static final Pointcut SETTERS = SetterPointcut.INSTANCE;
    public static final Pointcut GETTERS = GetterPointcut.INSTANCE;

    public static Pointcut union(Pointcut pc1, Pointcut pc2) {
        return new ComposablePointcut(pc1).union(pc2);
    }

    public static Pointcut intersection(Pointcut pc1, Pointcut pc2) {
        return new ComposablePointcut(pc1).intersection(pc2);
    }

    public static boolean matches(Pointcut pointcut, Method method, Class<?> targetClass, Object ... args) {
        MethodMatcher mm;
        Assert.notNull((Object)pointcut, "Pointcut must not be null");
        if (pointcut == Pointcut.TRUE) {
            return true;
        }
        if (pointcut.getClassFilter().matches(targetClass) && (mm = pointcut.getMethodMatcher()).matches(method, targetClass)) {
            return !mm.isRuntime() || mm.matches(method, targetClass, args);
        }
        return false;
    }

    private static class GetterPointcut
    extends StaticMethodMatcherPointcut
    implements Serializable {
        public static final GetterPointcut INSTANCE = new GetterPointcut();

        private GetterPointcut() {
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return method.getName().startsWith("get") && method.getParameterCount() == 0;
        }

        private Object readResolve() {
            return INSTANCE;
        }

        public String toString() {
            return "Pointcuts.GETTERS";
        }
    }

    private static class SetterPointcut
    extends StaticMethodMatcherPointcut
    implements Serializable {
        public static final SetterPointcut INSTANCE = new SetterPointcut();

        private SetterPointcut() {
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return method.getName().startsWith("set") && method.getParameterCount() == 1 && method.getReturnType() == Void.TYPE;
        }

        private Object readResolve() {
            return INSTANCE;
        }

        public String toString() {
            return "Pointcuts.SETTERS";
        }
    }
}

