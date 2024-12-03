/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.ConstructorDescriptor
 *  javax.validation.metadata.CrossParameterDescriptor
 *  javax.validation.metadata.MethodDescriptor
 *  javax.validation.metadata.ParameterDescriptor
 *  javax.validation.metadata.ReturnValueDescriptor
 */
package org.hibernate.validator.internal.metadata.descriptor;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.metadata.ConstructorDescriptor;
import javax.validation.metadata.CrossParameterDescriptor;
import javax.validation.metadata.MethodDescriptor;
import javax.validation.metadata.ParameterDescriptor;
import javax.validation.metadata.ReturnValueDescriptor;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.CrossParameterDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ElementDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;

public class ExecutableDescriptorImpl
extends ElementDescriptorImpl
implements ConstructorDescriptor,
MethodDescriptor {
    private final String name;
    private final List<ParameterDescriptor> parameters;
    private final CrossParameterDescriptor crossParameterDescriptor;
    private final ReturnValueDescriptor returnValueDescriptor;
    private final boolean isGetter;

    public ExecutableDescriptorImpl(Type returnType, String name, Set<ConstraintDescriptorImpl<?>> crossParameterConstraints, ReturnValueDescriptor returnValueDescriptor, List<ParameterDescriptor> parameters, boolean defaultGroupSequenceRedefined, boolean isGetter, List<Class<?>> defaultGroupSequence) {
        super(returnType, Collections.emptySet(), defaultGroupSequenceRedefined, defaultGroupSequence);
        this.name = name;
        this.parameters = CollectionHelper.toImmutableList(parameters);
        this.returnValueDescriptor = returnValueDescriptor;
        this.crossParameterDescriptor = new CrossParameterDescriptorImpl(crossParameterConstraints, defaultGroupSequenceRedefined, defaultGroupSequence);
        this.isGetter = isGetter;
    }

    public String getName() {
        return this.name;
    }

    public List<ParameterDescriptor> getParameterDescriptors() {
        return this.parameters;
    }

    public ReturnValueDescriptor getReturnValueDescriptor() {
        return this.returnValueDescriptor;
    }

    public boolean hasConstrainedParameters() {
        if (this.crossParameterDescriptor.hasConstraints()) {
            return true;
        }
        for (ParameterDescriptor oneParameter : this.parameters) {
            if (!oneParameter.hasConstraints() && !oneParameter.isCascaded()) continue;
            return true;
        }
        return false;
    }

    public boolean hasConstrainedReturnValue() {
        return this.returnValueDescriptor != null && (this.returnValueDescriptor.hasConstraints() || this.returnValueDescriptor.isCascaded());
    }

    public CrossParameterDescriptor getCrossParameterDescriptor() {
        return this.crossParameterDescriptor;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExecutableDescriptorImpl");
        sb.append("{name='").append(this.name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public boolean isGetter() {
        return this.isGetter;
    }
}

