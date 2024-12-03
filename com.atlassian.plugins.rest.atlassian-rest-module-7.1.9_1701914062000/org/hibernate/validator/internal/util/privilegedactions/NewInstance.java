/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public final class NewInstance<T>
implements PrivilegedAction<T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Class<T> clazz;
    private final String message;

    public static <T> NewInstance<T> action(Class<T> clazz, String message) {
        return new NewInstance<T>(clazz, message);
    }

    private NewInstance(Class<T> clazz, String message) {
        this.clazz = clazz;
        this.message = message;
    }

    @Override
    public T run() {
        try {
            return this.clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw LOG.getUnableToInstantiateException(this.message, this.clazz, e);
        }
        catch (IllegalAccessException e) {
            throw LOG.getUnableToInstantiateException(this.clazz, e);
        }
        catch (RuntimeException e) {
            throw LOG.getUnableToInstantiateException(this.clazz, e);
        }
    }
}

