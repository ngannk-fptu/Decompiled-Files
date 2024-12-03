/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.BeanDescriptor
 *  javax.validation.metadata.ConstructorDescriptor
 *  javax.validation.metadata.MethodDescriptor
 *  javax.validation.metadata.MethodType
 *  javax.validation.metadata.PropertyDescriptor
 */
package org.hibernate.validator.internal.metadata.descriptor;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstructorDescriptor;
import javax.validation.metadata.MethodDescriptor;
import javax.validation.metadata.MethodType;
import javax.validation.metadata.PropertyDescriptor;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ElementDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ExecutableDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.logging.Messages;

public class BeanDescriptorImpl
extends ElementDescriptorImpl
implements BeanDescriptor {
    private final Map<String, PropertyDescriptor> constrainedProperties;
    private final Map<String, ExecutableDescriptorImpl> constrainedMethods;
    private final Map<String, ConstructorDescriptor> constrainedConstructors;

    public BeanDescriptorImpl(Type beanClass, Set<ConstraintDescriptorImpl<?>> classLevelConstraints, Map<String, PropertyDescriptor> constrainedProperties, Map<String, ExecutableDescriptorImpl> constrainedMethods, Map<String, ConstructorDescriptor> constrainedConstructors, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        super(beanClass, classLevelConstraints, defaultGroupSequenceRedefined, defaultGroupSequence);
        this.constrainedProperties = CollectionHelper.toImmutableMap(constrainedProperties);
        this.constrainedMethods = CollectionHelper.toImmutableMap(constrainedMethods);
        this.constrainedConstructors = CollectionHelper.toImmutableMap(constrainedConstructors);
    }

    public final boolean isBeanConstrained() {
        return this.hasConstraints() || !this.constrainedProperties.isEmpty();
    }

    public final PropertyDescriptor getConstraintsForProperty(String propertyName) {
        Contracts.assertNotNull(propertyName, "The property name cannot be null");
        return this.constrainedProperties.get(propertyName);
    }

    public final Set<PropertyDescriptor> getConstrainedProperties() {
        return CollectionHelper.newHashSet(this.constrainedProperties.values());
    }

    public ConstructorDescriptor getConstraintsForConstructor(Class<?> ... parameterTypes) {
        return this.constrainedConstructors.get(ExecutableHelper.getSignature(this.getElementClass().getSimpleName(), parameterTypes));
    }

    public Set<ConstructorDescriptor> getConstrainedConstructors() {
        return CollectionHelper.newHashSet(this.constrainedConstructors.values());
    }

    public Set<MethodDescriptor> getConstrainedMethods(MethodType methodType, MethodType ... methodTypes) {
        boolean includeGetters = MethodType.GETTER.equals((Object)methodType);
        boolean includeNonGetters = MethodType.NON_GETTER.equals((Object)methodType);
        if (methodTypes != null) {
            for (MethodType type : methodTypes) {
                if (MethodType.GETTER.equals((Object)type)) {
                    includeGetters = true;
                }
                if (!MethodType.NON_GETTER.equals((Object)type)) continue;
                includeNonGetters = true;
            }
        }
        HashSet<MethodDescriptor> matchingMethodDescriptors = CollectionHelper.newHashSet();
        for (ExecutableDescriptorImpl constrainedMethod : this.constrainedMethods.values()) {
            boolean addToSet = false;
            if (constrainedMethod.isGetter() && includeGetters || !constrainedMethod.isGetter() && includeNonGetters) {
                addToSet = true;
            }
            if (!addToSet) continue;
            matchingMethodDescriptors.add(constrainedMethod);
        }
        return matchingMethodDescriptors;
    }

    public MethodDescriptor getConstraintsForMethod(String methodName, Class<?> ... parameterTypes) {
        Contracts.assertNotNull(methodName, Messages.MESSAGES.methodNameMustNotBeNull());
        return this.constrainedMethods.get(ExecutableHelper.getSignature(methodName, parameterTypes));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BeanDescriptorImpl");
        sb.append("{class='");
        sb.append(this.getElementClass().getSimpleName());
        sb.append("'}");
        return sb.toString();
    }
}

