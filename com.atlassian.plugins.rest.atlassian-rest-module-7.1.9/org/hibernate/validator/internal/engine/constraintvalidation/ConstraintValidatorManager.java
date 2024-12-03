/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintDeclarationException
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.ConstraintValidatorFactory
 *  javax.validation.constraints.Null
 *  javax.validation.metadata.ConstraintDescriptor
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.constraints.Null;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidator;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ConstraintValidatorManager {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    static ConstraintValidator<?, ?> DUMMY_CONSTRAINT_VALIDATOR = new ConstraintValidator<Null, Object>(){

        public boolean isValid(Object value, ConstraintValidatorContext context) {
            return false;
        }
    };
    private final ConstraintValidatorFactory defaultConstraintValidatorFactory;
    private final HibernateConstraintValidatorInitializationContext defaultConstraintValidatorInitializationContext;
    private volatile ConstraintValidatorFactory mostRecentlyUsedNonDefaultConstraintValidatorFactory;
    private volatile HibernateConstraintValidatorInitializationContext mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext;
    private final Object mostRecentlyUsedNonDefaultConstraintValidatorFactoryAndInitializationContextMutex = new Object();
    private final ConcurrentHashMap<CacheKey, ConstraintValidator<?, ?>> constraintValidatorCache;

    public ConstraintValidatorManager(ConstraintValidatorFactory defaultConstraintValidatorFactory, HibernateConstraintValidatorInitializationContext defaultConstraintValidatorInitializationContext) {
        this.defaultConstraintValidatorFactory = defaultConstraintValidatorFactory;
        this.defaultConstraintValidatorInitializationContext = defaultConstraintValidatorInitializationContext;
        this.constraintValidatorCache = new ConcurrentHashMap();
    }

    public <A extends Annotation> ConstraintValidator<A, ?> getInitializedValidator(Type validatedValueType, ConstraintDescriptorImpl<A> descriptor, ConstraintValidatorFactory constraintValidatorFactory, HibernateConstraintValidatorInitializationContext initializationContext) {
        Contracts.assertNotNull(validatedValueType);
        Contracts.assertNotNull(descriptor);
        Contracts.assertNotNull(constraintValidatorFactory);
        Contracts.assertNotNull(initializationContext);
        CacheKey key = new CacheKey(descriptor.getAnnotationDescriptor(), validatedValueType, constraintValidatorFactory, initializationContext);
        Object constraintValidator = this.constraintValidatorCache.get(key);
        if (constraintValidator == null) {
            constraintValidator = this.createAndInitializeValidator(validatedValueType, descriptor, constraintValidatorFactory, initializationContext);
            constraintValidator = this.cacheValidator(key, (ConstraintValidator<A, ?>)constraintValidator);
        } else {
            LOG.tracef("Constraint validator %s found in cache.", constraintValidator);
        }
        return DUMMY_CONSTRAINT_VALIDATOR == constraintValidator ? null : constraintValidator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <A extends Annotation> ConstraintValidator<A, ?> cacheValidator(CacheKey key, ConstraintValidator<A, ?> constraintValidator) {
        ConstraintValidator<A, ?> cached;
        if (key.getConstraintValidatorFactory() != this.defaultConstraintValidatorFactory && key.getConstraintValidatorFactory() != this.mostRecentlyUsedNonDefaultConstraintValidatorFactory || key.getConstraintValidatorInitializationContext() != this.defaultConstraintValidatorInitializationContext && key.getConstraintValidatorInitializationContext() != this.mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext) {
            Object object = this.mostRecentlyUsedNonDefaultConstraintValidatorFactoryAndInitializationContextMutex;
            synchronized (object) {
                if (key.constraintValidatorFactory != this.mostRecentlyUsedNonDefaultConstraintValidatorFactory || key.constraintValidatorInitializationContext != this.mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext) {
                    this.clearEntries(this.mostRecentlyUsedNonDefaultConstraintValidatorFactory, this.mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext);
                    this.mostRecentlyUsedNonDefaultConstraintValidatorFactory = key.getConstraintValidatorFactory();
                    this.mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext = key.getConstraintValidatorInitializationContext();
                }
            }
        }
        return (cached = this.constraintValidatorCache.putIfAbsent(key, constraintValidator)) != null ? cached : constraintValidator;
    }

    private <A extends Annotation> ConstraintValidator<A, ?> createAndInitializeValidator(Type validatedValueType, ConstraintDescriptorImpl<A> descriptor, ConstraintValidatorFactory constraintValidatorFactory, HibernateConstraintValidatorInitializationContext initializationContext) {
        Object constraintValidator;
        ConstraintValidatorDescriptor<A> validatorDescriptor = this.findMatchingValidatorDescriptor(descriptor, validatedValueType);
        if (validatorDescriptor == null) {
            constraintValidator = DUMMY_CONSTRAINT_VALIDATOR;
        } else {
            constraintValidator = validatorDescriptor.newInstance(constraintValidatorFactory);
            this.initializeValidator((ConstraintDescriptor<A>)descriptor, (ConstraintValidator<A, ?>)constraintValidator, initializationContext);
        }
        return constraintValidator;
    }

    private void clearEntries(ConstraintValidatorFactory constraintValidatorFactory, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext) {
        Iterator<Map.Entry<CacheKey, ConstraintValidator<?, ?>>> cacheEntries = this.constraintValidatorCache.entrySet().iterator();
        while (cacheEntries.hasNext()) {
            Map.Entry<CacheKey, ConstraintValidator<?, ?>> cacheEntry = cacheEntries.next();
            if (cacheEntry.getKey().getConstraintValidatorFactory() != constraintValidatorFactory || cacheEntry.getKey().getConstraintValidatorInitializationContext() != constraintValidatorInitializationContext) continue;
            constraintValidatorFactory.releaseInstance(cacheEntry.getValue());
            cacheEntries.remove();
        }
    }

    public void clear() {
        for (Map.Entry<CacheKey, ConstraintValidator<?, ?>> entry : this.constraintValidatorCache.entrySet()) {
            entry.getKey().getConstraintValidatorFactory().releaseInstance(entry.getValue());
        }
        this.constraintValidatorCache.clear();
    }

    public ConstraintValidatorFactory getDefaultConstraintValidatorFactory() {
        return this.defaultConstraintValidatorFactory;
    }

    public HibernateConstraintValidatorInitializationContext getDefaultConstraintValidatorInitializationContext() {
        return this.defaultConstraintValidatorInitializationContext;
    }

    public int numberOfCachedConstraintValidatorInstances() {
        return this.constraintValidatorCache.size();
    }

    private <A extends Annotation> ConstraintValidatorDescriptor<A> findMatchingValidatorDescriptor(ConstraintDescriptorImpl<A> descriptor, Type validatedValueType) {
        Map<Type, ConstraintValidatorDescriptor<A>> availableValidatorDescriptors = TypeHelper.getValidatorTypes(descriptor.getAnnotationType(), descriptor.getMatchingConstraintValidatorDescriptors());
        List<Type> discoveredSuitableTypes = this.findSuitableValidatorTypes(validatedValueType, availableValidatorDescriptors.keySet());
        this.resolveAssignableTypes(discoveredSuitableTypes);
        if (discoveredSuitableTypes.size() == 0) {
            return null;
        }
        if (discoveredSuitableTypes.size() > 1) {
            throw LOG.getMoreThanOneValidatorFoundForTypeException(validatedValueType, discoveredSuitableTypes);
        }
        Type suitableType = discoveredSuitableTypes.get(0);
        return availableValidatorDescriptors.get(suitableType);
    }

    private <A extends Annotation> List<Type> findSuitableValidatorTypes(Type type, Iterable<Type> availableValidatorTypes) {
        ArrayList<Type> determinedSuitableTypes = CollectionHelper.newArrayList();
        for (Type validatorType : availableValidatorTypes) {
            if (!TypeHelper.isAssignable(validatorType, type) || determinedSuitableTypes.contains(validatorType)) continue;
            determinedSuitableTypes.add(validatorType);
        }
        return determinedSuitableTypes;
    }

    private <A extends Annotation> void initializeValidator(ConstraintDescriptor<A> descriptor, ConstraintValidator<A, ?> constraintValidator, HibernateConstraintValidatorInitializationContext initializationContext) {
        try {
            if (constraintValidator instanceof HibernateConstraintValidator) {
                ((HibernateConstraintValidator)constraintValidator).initialize(descriptor, initializationContext);
            }
            constraintValidator.initialize(descriptor.getAnnotation());
        }
        catch (RuntimeException e) {
            if (e instanceof ConstraintDeclarationException) {
                throw e;
            }
            throw LOG.getUnableToInitializeConstraintValidatorException(constraintValidator.getClass(), e);
        }
    }

    private void resolveAssignableTypes(List<Type> assignableTypes) {
        if (assignableTypes.size() == 0 || assignableTypes.size() == 1) {
            return;
        }
        ArrayList<Type> typesToRemove = new ArrayList<Type>();
        do {
            typesToRemove.clear();
            Type type = assignableTypes.get(0);
            for (int i = 1; i < assignableTypes.size(); ++i) {
                if (TypeHelper.isAssignable(type, assignableTypes.get(i))) {
                    typesToRemove.add(type);
                    continue;
                }
                if (!TypeHelper.isAssignable(assignableTypes.get(i), type)) continue;
                typesToRemove.add(assignableTypes.get(i));
            }
            assignableTypes.removeAll(typesToRemove);
        } while (typesToRemove.size() > 0);
    }

    private static final class CacheKey {
        private ConstraintAnnotationDescriptor<?> annotationDescriptor;
        private Type validatedType;
        private ConstraintValidatorFactory constraintValidatorFactory;
        private HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext;
        private int hashCode;

        private CacheKey(ConstraintAnnotationDescriptor<?> annotationDescriptor, Type validatorType, ConstraintValidatorFactory constraintValidatorFactory, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext) {
            this.annotationDescriptor = annotationDescriptor;
            this.validatedType = validatorType;
            this.constraintValidatorFactory = constraintValidatorFactory;
            this.constraintValidatorInitializationContext = constraintValidatorInitializationContext;
            this.hashCode = this.createHashCode();
        }

        public ConstraintValidatorFactory getConstraintValidatorFactory() {
            return this.constraintValidatorFactory;
        }

        public HibernateConstraintValidatorInitializationContext getConstraintValidatorInitializationContext() {
            return this.constraintValidatorInitializationContext;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            CacheKey other = (CacheKey)o;
            if (!this.annotationDescriptor.equals(other.annotationDescriptor)) {
                return false;
            }
            if (!this.validatedType.equals(other.validatedType)) {
                return false;
            }
            if (!this.constraintValidatorFactory.equals(other.constraintValidatorFactory)) {
                return false;
            }
            return this.constraintValidatorInitializationContext.equals(other.constraintValidatorInitializationContext);
        }

        public int hashCode() {
            return this.hashCode;
        }

        private int createHashCode() {
            int result = this.annotationDescriptor.hashCode();
            result = 31 * result + this.validatedType.hashCode();
            result = 31 * result + this.constraintValidatorFactory.hashCode();
            result = 31 * result + this.constraintValidatorInitializationContext.hashCode();
            return result;
        }
    }
}

