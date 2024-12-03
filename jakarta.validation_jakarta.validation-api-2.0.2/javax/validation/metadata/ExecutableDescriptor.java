/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.metadata;

import java.util.List;
import java.util.Set;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.CrossParameterDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.ParameterDescriptor;
import javax.validation.metadata.ReturnValueDescriptor;

public interface ExecutableDescriptor
extends ElementDescriptor {
    public String getName();

    public List<ParameterDescriptor> getParameterDescriptors();

    public CrossParameterDescriptor getCrossParameterDescriptor();

    public ReturnValueDescriptor getReturnValueDescriptor();

    public boolean hasConstrainedParameters();

    public boolean hasConstrainedReturnValue();

    @Override
    public boolean hasConstraints();

    @Override
    public Set<ConstraintDescriptor<?>> getConstraintDescriptors();

    @Override
    public ElementDescriptor.ConstraintFinder findConstraints();
}

