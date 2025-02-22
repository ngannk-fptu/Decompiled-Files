/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Persistence
 *  javax.validation.Path
 *  javax.validation.Path$Node
 *  javax.validation.TraversableResolver
 */
package org.hibernate.validator.internal.engine.resolver;

import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import javax.persistence.Persistence;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class JPATraversableResolver
implements TraversableResolver {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    public final boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Calling isReachable on object %s with node name %s.", traversableObject, (Object)traversableProperty.getName());
        }
        if (traversableObject == null) {
            return true;
        }
        return Persistence.getPersistenceUtil().isLoaded(traversableObject, traversableProperty.getName());
    }

    public final boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        return true;
    }
}

