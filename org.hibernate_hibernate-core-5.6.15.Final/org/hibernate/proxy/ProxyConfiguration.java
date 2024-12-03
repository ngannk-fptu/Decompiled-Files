/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.implementation.bind.annotation.AllArguments
 *  net.bytebuddy.implementation.bind.annotation.FieldValue
 *  net.bytebuddy.implementation.bind.annotation.Origin
 *  net.bytebuddy.implementation.bind.annotation.RuntimeType
 *  net.bytebuddy.implementation.bind.annotation.StubValue
 *  net.bytebuddy.implementation.bind.annotation.This
 */
package org.hibernate.proxy;

import java.lang.reflect.Method;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.StubValue;
import net.bytebuddy.implementation.bind.annotation.This;

public interface ProxyConfiguration {
    public static final String INTERCEPTOR_FIELD_NAME = "$$_hibernate_interceptor";

    public void $$_hibernate_set_interceptor(Interceptor var1);

    public static class InterceptorDispatcher {
        @RuntimeType
        public static Object intercept(@This Object instance, @Origin Method method, @AllArguments Object[] arguments, @StubValue Object stubValue, @FieldValue(value="$$_hibernate_interceptor") Interceptor interceptor) throws Throwable {
            if (interceptor == null) {
                if (method.getName().equals("getHibernateLazyInitializer")) {
                    return instance;
                }
                return stubValue;
            }
            return interceptor.intercept(instance, method, arguments);
        }
    }

    public static interface Interceptor {
        @RuntimeType
        public Object intercept(@This Object var1, @Origin Method var2, @AllArguments Object[] var3) throws Throwable;
    }
}

