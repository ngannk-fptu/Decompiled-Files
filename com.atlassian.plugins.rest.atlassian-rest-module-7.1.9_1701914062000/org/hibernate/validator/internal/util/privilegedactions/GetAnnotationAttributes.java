/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public final class GetAnnotationAttributes
implements PrivilegedAction<Map<String, Object>> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Annotation annotation;

    public static GetAnnotationAttributes action(Annotation annotation) {
        return new GetAnnotationAttributes(annotation);
    }

    private GetAnnotationAttributes(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public Map<String, Object> run() {
        Method[] declaredMethods = this.annotation.annotationType().getDeclaredMethods();
        HashMap<String, Object> attributes = CollectionHelper.newHashMap(declaredMethods.length);
        for (Method m : declaredMethods) {
            if (m.isSynthetic()) continue;
            m.setAccessible(true);
            String attributeName = m.getName();
            try {
                attributes.put(m.getName(), m.invoke((Object)this.annotation, new Object[0]));
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw LOG.getUnableToGetAnnotationAttributeException(this.annotation.getClass(), attributeName, e);
            }
        }
        return CollectionHelper.toImmutableMap(attributes);
    }
}

