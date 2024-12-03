/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.internal.engine;

import com.fasterxml.classmate.ResolvedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.privilegedactions.GetInstancesFromServiceLoader;
import org.hibernate.validator.spi.cfg.ConstraintMappingContributor;

public class ServiceLoaderBasedConstraintMappingContributor
implements ConstraintMappingContributor {
    private final TypeResolutionHelper typeResolutionHelper;
    private final ClassLoader primaryClassLoader;

    public ServiceLoaderBasedConstraintMappingContributor(TypeResolutionHelper typeResolutionHelper, ClassLoader primaryClassLoader) {
        this.primaryClassLoader = primaryClassLoader;
        this.typeResolutionHelper = typeResolutionHelper;
    }

    @Override
    public void createConstraintMappings(ConstraintMappingContributor.ConstraintMappingBuilder builder) {
        HashMap customValidators = CollectionHelper.newHashMap();
        List discoveredConstraintValidators = (List)this.run(GetInstancesFromServiceLoader.action(this.primaryClassLoader, ConstraintValidator.class));
        for (ConstraintValidator constraintValidator : discoveredConstraintValidators) {
            Class<?> constraintValidatorClass = constraintValidator.getClass();
            Class<?> annotationType = this.determineAnnotationType(constraintValidatorClass);
            ArrayList validators = (ArrayList)customValidators.get(annotationType);
            if (annotationType != null && validators == null) {
                validators = new ArrayList();
                customValidators.put(annotationType, validators);
            }
            validators.add(constraintValidatorClass);
        }
        ConstraintMapping constraintMapping = builder.addConstraintMapping();
        for (Map.Entry entry : customValidators.entrySet()) {
            this.registerConstraintDefinition(constraintMapping, (Class)entry.getKey(), (List)entry.getValue());
        }
    }

    private <A extends Annotation> void registerConstraintDefinition(ConstraintMapping constraintMapping, Class<?> constraintType, List<Class<?>> validatorTypes) {
        ConstraintDefinitionContext<?> context = constraintMapping.constraintDefinition(constraintType).includeExistingValidators(true);
        for (Class<?> validatorType : validatorTypes) {
            context.validatedBy(validatorType);
        }
    }

    private Class<?> determineAnnotationType(Class<? extends ConstraintValidator> constraintValidatorClass) {
        ResolvedType resolvedType = this.typeResolutionHelper.getTypeResolver().resolve(constraintValidatorClass, new Type[0]);
        return resolvedType.typeParametersFor(ConstraintValidator.class).get(0).getErasedType();
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

