/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.ContainerElementTypeDescriptor
 *  javax.validation.metadata.GroupConversionDescriptor
 *  javax.validation.metadata.PropertyDescriptor
 */
package org.hibernate.validator.internal.metadata.descriptor;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.validation.metadata.ContainerElementTypeDescriptor;
import javax.validation.metadata.GroupConversionDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ElementDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;

public class PropertyDescriptorImpl
extends ElementDescriptorImpl
implements PropertyDescriptor {
    private final String propertyName;
    private final Set<ContainerElementTypeDescriptor> constrainedContainerElementTypes;
    private final boolean cascaded;
    private final Set<GroupConversionDescriptor> groupConversions;

    public PropertyDescriptorImpl(Type returnType, String propertyName, Set<ConstraintDescriptorImpl<?>> constraints, Set<ContainerElementTypeDescriptor> constrainedContainerElementTypes, boolean cascaded, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence, Set<GroupConversionDescriptor> groupConversions) {
        super(returnType, constraints, defaultGroupSequenceRedefined, defaultGroupSequence);
        this.propertyName = propertyName;
        this.constrainedContainerElementTypes = CollectionHelper.toImmutableSet(constrainedContainerElementTypes);
        this.cascaded = cascaded;
        this.groupConversions = CollectionHelper.toImmutableSet(groupConversions);
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Set<ContainerElementTypeDescriptor> getConstrainedContainerElementTypes() {
        return this.constrainedContainerElementTypes;
    }

    public boolean isCascaded() {
        return this.cascaded;
    }

    public Set<GroupConversionDescriptor> getGroupConversions() {
        return this.groupConversions;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PropertyDescriptorImpl");
        sb.append("{propertyName=").append(this.propertyName);
        sb.append(", cascaded=").append(this.cascaded);
        sb.append('}');
        return sb.toString();
    }
}

