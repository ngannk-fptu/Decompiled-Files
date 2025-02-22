/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.TraversableResolver
 *  javax.validation.ValidationException
 */
package org.hibernate.validator.internal.engine.resolver;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.validation.TraversableResolver;
import javax.validation.ValidationException;
import org.hibernate.validator.internal.engine.resolver.CachingJPATraversableResolverForSingleValidation;
import org.hibernate.validator.internal.engine.resolver.CachingTraversableResolverForSingleValidation;
import org.hibernate.validator.internal.engine.resolver.TraverseAllTraversableResolver;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;

public class TraversableResolvers {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String PERSISTENCE_CLASS_NAME = "javax.persistence.Persistence";
    private static final String PERSISTENCE_UTIL_METHOD = "getPersistenceUtil";
    private static final String JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME = "org.hibernate.validator.internal.engine.resolver.JPATraversableResolver";

    private TraversableResolvers() {
    }

    public static TraversableResolver getDefault() {
        Class persistenceClass;
        try {
            persistenceClass = (Class)TraversableResolvers.run(LoadClass.action(PERSISTENCE_CLASS_NAME, TraversableResolvers.class.getClassLoader()));
        }
        catch (ValidationException e) {
            LOG.debugf("Cannot find %s on classpath. Assuming non JPA 2 environment. All properties will per default be traversable.", PERSISTENCE_CLASS_NAME);
            return TraversableResolvers.getTraverseAllTraversableResolver();
        }
        Method persistenceUtilGetter = TraversableResolvers.run(GetMethod.action(persistenceClass, PERSISTENCE_UTIL_METHOD));
        if (persistenceUtilGetter == null) {
            LOG.debugf("Found %s on classpath, but no method '%s'. Assuming JPA 1 environment. All properties will per default be traversable.", PERSISTENCE_CLASS_NAME, PERSISTENCE_UTIL_METHOD);
            return TraversableResolvers.getTraverseAllTraversableResolver();
        }
        try {
            Object persistence = TraversableResolvers.run(NewInstance.action(persistenceClass, "persistence provider"));
            ReflectionHelper.getValue(persistenceUtilGetter, persistence);
        }
        catch (Exception e) {
            LOG.debugf("Unable to invoke %s.%s. Inconsistent JPA environment. All properties will per default be traversable.", PERSISTENCE_CLASS_NAME, PERSISTENCE_UTIL_METHOD);
            return TraversableResolvers.getTraverseAllTraversableResolver();
        }
        LOG.debugf("Found %s on classpath containing '%s'. Assuming JPA 2 environment. Trying to instantiate JPA aware TraversableResolver", PERSISTENCE_CLASS_NAME, PERSISTENCE_UTIL_METHOD);
        try {
            Class jpaAwareResolverClass = (Class)TraversableResolvers.run(LoadClass.action(JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME, TraversableResolvers.class.getClassLoader()));
            LOG.debugf("Instantiated JPA aware TraversableResolver of type %s.", JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME);
            return (TraversableResolver)TraversableResolvers.run(NewInstance.action(jpaAwareResolverClass, ""));
        }
        catch (ValidationException e) {
            LOG.logUnableToLoadOrInstantiateJPAAwareResolver(JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME);
            return TraversableResolvers.getTraverseAllTraversableResolver();
        }
    }

    public static TraversableResolver wrapWithCachingForSingleValidation(TraversableResolver traversableResolver, boolean traversableResolverResultCacheEnabled) {
        if (TraverseAllTraversableResolver.class.equals(traversableResolver.getClass()) || !traversableResolverResultCacheEnabled) {
            return traversableResolver;
        }
        if (JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME.equals(traversableResolver.getClass().getName())) {
            return new CachingJPATraversableResolverForSingleValidation(traversableResolver);
        }
        return new CachingTraversableResolverForSingleValidation(traversableResolver);
    }

    private static TraversableResolver getTraverseAllTraversableResolver() {
        return new TraverseAllTraversableResolver();
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

