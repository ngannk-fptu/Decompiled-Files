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
import java.util.Map;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import org.hibernate.validator.internal.engine.resolver.AbstractTraversableHolder;

class CachingTraversableResolverForSingleValidation
implements TraversableResolver {
    private final TraversableResolver delegate;
    private final Map<TraversableHolder, TraversableHolder> traversables = new HashMap<TraversableHolder, TraversableHolder>();

    public CachingTraversableResolverForSingleValidation(TraversableResolver delegate) {
        this.delegate = delegate;
    }

    public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        TraversableHolder currentLH = new TraversableHolder(traversableObject, traversableProperty);
        TraversableHolder cachedLH = this.traversables.get(currentLH);
        if (cachedLH == null) {
            currentLH.isReachable = this.delegate.isReachable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType);
            this.traversables.put(currentLH, currentLH);
            cachedLH = currentLH;
        } else if (cachedLH.isReachable == null) {
            cachedLH.isReachable = this.delegate.isReachable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType);
        }
        return cachedLH.isReachable;
    }

    public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        TraversableHolder currentLH = new TraversableHolder(traversableObject, traversableProperty);
        TraversableHolder cachedLH = this.traversables.get(currentLH);
        if (cachedLH == null) {
            currentLH.isCascadable = this.delegate.isCascadable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType);
            this.traversables.put(currentLH, currentLH);
            cachedLH = currentLH;
        } else if (cachedLH.isCascadable == null) {
            cachedLH.isCascadable = this.delegate.isCascadable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType);
        }
        return cachedLH.isCascadable;
    }

    private static final class TraversableHolder
    extends AbstractTraversableHolder {
        private Boolean isReachable;
        private Boolean isCascadable;

        private TraversableHolder(Object traversableObject, Path.Node traversableProperty) {
            super(traversableObject, traversableProperty);
        }
    }
}

