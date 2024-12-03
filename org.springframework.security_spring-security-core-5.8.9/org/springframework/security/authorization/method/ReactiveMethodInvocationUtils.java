/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  reactor.core.Exceptions
 */
package org.springframework.security.authorization.method;

import org.aopalliance.intercept.MethodInvocation;
import reactor.core.Exceptions;

final class ReactiveMethodInvocationUtils {
    static <T> T proceed(MethodInvocation mi) {
        try {
            return (T)mi.proceed();
        }
        catch (Throwable ex) {
            throw Exceptions.propagate((Throwable)ex);
        }
    }

    private ReactiveMethodInvocationUtils() {
    }
}

