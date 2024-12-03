/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Path
 *  javax.validation.Path$Node
 *  javax.validation.TraversableResolver
 */
package org.hibernate.validator.internal.engine.resolver;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import org.hibernate.validator.internal.engine.resolver.AbstractTraversableHolder;

class CachingJPATraversableResolverForSingleValidation
implements TraversableResolver {
    private final TraversableResolver delegate;
    private final HashMap<TraversableHolder, Boolean> traversables = new HashMap();

    public CachingJPATraversableResolverForSingleValidation(TraversableResolver delegate) {
        this.delegate = delegate;
    }

    public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        if (traversableObject == null) {
            return true;
        }
        return this.traversables.computeIfAbsent(new TraversableHolder(traversableObject, traversableProperty), th -> this.delegate.isReachable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType));
    }

    public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        return true;
    }

    private static class TraversableHolder
    extends AbstractTraversableHolder {
        private TraversableHolder(Object traversableObject, Path.Node traversableProperty) {
            super(traversableObject, traversableProperty);
        }
    }
}

