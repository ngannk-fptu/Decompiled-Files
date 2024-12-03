/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.CrossParameterDescriptor
 */
package org.hibernate.validator.internal.metadata.descriptor;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.validation.metadata.CrossParameterDescriptor;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ElementDescriptorImpl;

public class CrossParameterDescriptorImpl
extends ElementDescriptorImpl
implements CrossParameterDescriptor {
    public CrossParameterDescriptorImpl(Set<ConstraintDescriptorImpl<?>> constraintDescriptors, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        super((Type)((Object)Object[].class), constraintDescriptors, defaultGroupSequenceRedefined, defaultGroupSequence);
    }
}

