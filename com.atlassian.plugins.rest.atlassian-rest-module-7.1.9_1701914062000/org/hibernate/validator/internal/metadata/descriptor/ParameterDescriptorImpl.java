/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.ContainerElementTypeDescriptor
 *  javax.validation.metadata.GroupConversionDescriptor
 *  javax.validation.metadata.ParameterDescriptor
 */
package org.hibernate.validator.internal.metadata.descriptor;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.validation.metadata.ContainerElementTypeDescriptor;
import javax.validation.metadata.GroupConversionDescriptor;
import javax.validation.metadata.ParameterDescriptor;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ElementDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;

public class ParameterDescriptorImpl
extends ElementDescriptorImpl
implements ParameterDescriptor {
    private final int index;
    private final String name;
    private final Set<ContainerElementTypeDescriptor> constrainedContainerElementTypes;
    private final boolean cascaded;
    private final Set<GroupConversionDescriptor> groupConversions;

    public ParameterDescriptorImpl(Type type, int index, String name, Set<ConstraintDescriptorImpl<?>> constraints, Set<ContainerElementTypeDescriptor> constrainedContainerElementTypes, boolean isCascaded, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence, Set<GroupConversionDescriptor> groupConversions) {
        super(type, constraints, defaultGroupSequenceRedefined, defaultGroupSequence);
        this.index = index;
        this.name = name;
        this.constrainedContainerElementTypes = CollectionHelper.toImmutableSet(constrainedContainerElementTypes);
        this.cascaded = isCascaded;
        this.groupConversions = CollectionHelper.toImmutableSet(groupConversions);
    }

    public int getIndex() {
        return this.index;
    }

    public Set<ContainerElementTypeDescriptor> getConstrainedContainerElementTypes() {
        return this.constrainedContainerElementTypes;
    }

    public String getName() {
        return this.name;
    }

    public boolean isCascaded() {
        return this.cascaded;
    }

    public Set<GroupConversionDescriptor> getGroupConversions() {
        return this.groupConversions;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ParameterDescriptorImpl");
        sb.append("{cascaded=").append(this.cascaded);
        sb.append(", index=").append(this.index);
        sb.append(", name=").append(this.name);
        sb.append('}');
        return sb.toString();
    }
}

