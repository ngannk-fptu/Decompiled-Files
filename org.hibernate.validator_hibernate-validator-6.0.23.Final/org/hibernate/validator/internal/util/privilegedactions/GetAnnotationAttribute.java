/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public final class GetAnnotationAttribute<T>
implements PrivilegedAction<T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Annotation annotation;
    private final String attributeName;
    private final Class<T> type;

    public static <T> GetAnnotationAttribute<T> action(Annotation annotation, String attributeName, Class<T> type) {
        return new GetAnnotationAttribute<T>(annotation, attributeName, type);
    }

    private GetAnnotationAttribute(Annotation annotation, String attributeName, Class<T> type) {
        this.annotation = annotation;
        this.attributeName = attributeName;
        this.type = type;
    }

    @Override
    public T run() {
        try {
            Method m = this.annotation.getClass().getMethod(this.attributeName, new Class[0]);
            m.setAccessible(true);
            Object o = m.invoke((Object)this.annotation, new Object[0]);
            if (this.type.isAssignableFrom(o.getClass())) {
                return (T)o;
            }
            throw LOG.getWrongAnnotationAttributeTypeException(this.annotation.annotationType(), this.attributeName, this.type, o.getClass());
        }
        catch (NoSuchMethodException e) {
            throw LOG.getUnableToFindAnnotationAttributeException(this.annotation.annotationType(), this.attributeName, e);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw LOG.getUnableToGetAnnotationAttributeException(this.annotation.annotationType(), this.attributeName, e);
        }
    }
}

