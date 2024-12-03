/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public final class ConstructorInstance<T>
implements PrivilegedAction<T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Constructor<T> constructor;
    private final Object[] initArgs;

    public static <T> ConstructorInstance<T> action(Constructor<T> constructor, Object ... initArgs) {
        return new ConstructorInstance<T>(constructor, initArgs);
    }

    private ConstructorInstance(Constructor<T> constructor, Object ... initArgs) {
        this.constructor = constructor;
        this.initArgs = initArgs;
    }

    @Override
    public T run() {
        try {
            return this.constructor.newInstance(this.initArgs);
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
            throw LOG.getUnableToInstantiateException(this.constructor.getDeclaringClass(), e);
        }
    }
}

