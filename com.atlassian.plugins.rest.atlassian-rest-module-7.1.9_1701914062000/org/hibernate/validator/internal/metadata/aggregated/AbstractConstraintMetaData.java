/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.ContainerElementTypeDescriptor
 *  javax.validation.metadata.GroupConversionDescriptor
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.metadata.ContainerElementTypeDescriptor;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ContainerCascadingMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ContainerElementTypeDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.location.TypeArgumentConstraintLocation;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeVariables;

public abstract class AbstractConstraintMetaData
implements ConstraintMetaData {
    private final String name;
    private final Type type;
    private final Set<MetaConstraint<?>> directConstraints;
    private final Set<MetaConstraint<?>> containerElementsConstraints;
    private final Set<MetaConstraint<?>> allConstraints;
    private final boolean isCascading;
    private final boolean isConstrained;

    public AbstractConstraintMetaData(String name, Type type, Set<MetaConstraint<?>> directConstraints, Set<MetaConstraint<?>> containerElementsConstraints, boolean isCascading, boolean isConstrained) {
        this.name = name;
        this.type = type;
        this.directConstraints = CollectionHelper.toImmutableSet(directConstraints);
        this.containerElementsConstraints = CollectionHelper.toImmutableSet(containerElementsConstraints);
        this.allConstraints = Stream.concat(directConstraints.stream(), containerElementsConstraints.stream()).collect(Collectors.collectingAndThen(Collectors.toSet(), CollectionHelper::toImmutableSet));
        this.isCascading = isCascading;
        this.isConstrained = isConstrained;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Iterator<MetaConstraint<?>> iterator() {
        return this.allConstraints.iterator();
    }

    public Set<MetaConstraint<?>> getAllConstraints() {
        return this.allConstraints;
    }

    public Set<MetaConstraint<?>> getDirectConstraints() {
        return this.directConstraints;
    }

    public Set<MetaConstraint<?>> getContainerElementsConstraints() {
        return this.containerElementsConstraints;
    }

    @Override
    public final boolean isCascading() {
        return this.isCascading;
    }

    @Override
    public boolean isConstrained() {
        return this.isConstrained;
    }

    public String toString() {
        return "AbstractConstraintMetaData [name=" + this.name + ", type=" + this.type + ", directConstraints=" + this.directConstraints + ", containerElementsConstraints=" + this.containerElementsConstraints + ", isCascading=" + this.isCascading + ", isConstrained=" + this.isConstrained + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AbstractConstraintMetaData other = (AbstractConstraintMetaData)obj;
        return !(this.name == null ? other.name != null : !this.name.equals(other.name));
    }

    protected Set<ConstraintDescriptorImpl<?>> asDescriptors(Set<MetaConstraint<?>> constraints) {
        HashSet<ConstraintDescriptorImpl<?>> theValue = CollectionHelper.newHashSet();
        for (MetaConstraint<?> oneConstraint : constraints) {
            theValue.add(oneConstraint.getDescriptor());
        }
        return theValue;
    }

    protected Set<ContainerElementTypeDescriptor> asContainerElementTypeDescriptors(Set<MetaConstraint<?>> containerElementsConstraints, CascadingMetaData cascadingMetaData, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        return this.asContainerElementTypeDescriptors(this.type, ContainerElementMetaDataTree.of(cascadingMetaData, containerElementsConstraints), defaultGroupSequenceRedefined, defaultGroupSequence);
    }

    private Set<ContainerElementTypeDescriptor> asContainerElementTypeDescriptors(Type type, ContainerElementMetaDataTree containerElementMetaDataTree, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        HashSet<ContainerElementTypeDescriptor> containerElementTypeDescriptors = new HashSet<ContainerElementTypeDescriptor>();
        for (Map.Entry entry : containerElementMetaDataTree.nodes.entrySet()) {
            TypeVariable childTypeParameter = (TypeVariable)entry.getKey();
            ContainerElementMetaDataTree childContainerElementMetaDataTree = (ContainerElementMetaDataTree)entry.getValue();
            Set<ContainerElementTypeDescriptor> childrenDescriptors = this.asContainerElementTypeDescriptors(childContainerElementMetaDataTree.elementType, childContainerElementMetaDataTree, defaultGroupSequenceRedefined, defaultGroupSequence);
            containerElementTypeDescriptors.add(new ContainerElementTypeDescriptorImpl(childContainerElementMetaDataTree.elementType, childContainerElementMetaDataTree.containerClass, TypeVariables.getTypeParameterIndex(childTypeParameter), this.asDescriptors(childContainerElementMetaDataTree.constraints), childrenDescriptors, childContainerElementMetaDataTree.cascading, defaultGroupSequenceRedefined, defaultGroupSequence, childContainerElementMetaDataTree.groupConversionDescriptors));
        }
        return containerElementTypeDescriptors;
    }

    private static class ContainerElementMetaDataTree {
        private final Map<TypeVariable<?>, ContainerElementMetaDataTree> nodes = new HashMap();
        private Type elementType = null;
        private Class<?> containerClass;
        private final Set<MetaConstraint<?>> constraints = new HashSet();
        private boolean cascading = false;
        private Set<GroupConversionDescriptor> groupConversionDescriptors = new HashSet<GroupConversionDescriptor>();

        private ContainerElementMetaDataTree() {
        }

        private static ContainerElementMetaDataTree of(CascadingMetaData cascadingMetaData, Set<MetaConstraint<?>> containerElementsConstraints) {
            ContainerElementMetaDataTree containerElementMetaConstraintTree = new ContainerElementMetaDataTree();
            for (MetaConstraint<?> constraint : containerElementsConstraints) {
                ConstraintLocation currentLocation = constraint.getLocation();
                ArrayList constraintPath = new ArrayList();
                while (currentLocation instanceof TypeArgumentConstraintLocation) {
                    TypeArgumentConstraintLocation typeArgumentConstraintLocation = (TypeArgumentConstraintLocation)currentLocation;
                    constraintPath.add(typeArgumentConstraintLocation.getTypeParameter());
                    currentLocation = typeArgumentConstraintLocation.getDelegate();
                }
                Collections.reverse(constraintPath);
                containerElementMetaConstraintTree.addConstraint(constraintPath, constraint);
            }
            if (cascadingMetaData != null && cascadingMetaData.isContainer() && cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements()) {
                containerElementMetaConstraintTree.addCascadingMetaData(new ArrayList(), cascadingMetaData.as(ContainerCascadingMetaData.class));
            }
            return containerElementMetaConstraintTree;
        }

        private void addConstraint(List<TypeVariable<?>> path, MetaConstraint<?> constraint) {
            ContainerElementMetaDataTree tree = this;
            for (TypeVariable<?> typeArgument : path) {
                tree = tree.nodes.computeIfAbsent(typeArgument, ta -> new ContainerElementMetaDataTree());
            }
            TypeArgumentConstraintLocation constraintLocation = (TypeArgumentConstraintLocation)constraint.getLocation();
            tree.elementType = constraintLocation.getTypeForValidatorResolution();
            tree.containerClass = ((TypeArgumentConstraintLocation)constraint.getLocation()).getContainerClass();
            tree.constraints.add(constraint);
        }

        private void addCascadingMetaData(List<TypeVariable<?>> path, ContainerCascadingMetaData cascadingMetaData) {
            for (ContainerCascadingMetaData nestedCascadingMetaData : cascadingMetaData.getContainerElementTypesCascadingMetaData()) {
                ArrayList nestedPath = new ArrayList(path);
                nestedPath.add(nestedCascadingMetaData.getTypeParameter());
                ContainerElementMetaDataTree tree = this;
                for (TypeVariable typeVariable : nestedPath) {
                    tree = tree.nodes.computeIfAbsent(typeVariable, ta -> new ContainerElementMetaDataTree());
                }
                tree.elementType = TypeVariables.getContainerElementType(nestedCascadingMetaData.getEnclosingType(), nestedCascadingMetaData.getTypeParameter());
                tree.containerClass = nestedCascadingMetaData.getDeclaredContainerClass();
                tree.cascading = nestedCascadingMetaData.isCascading();
                tree.groupConversionDescriptors = nestedCascadingMetaData.getGroupConversionDescriptors();
                if (!nestedCascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements()) continue;
                this.addCascadingMetaData(nestedPath, nestedCascadingMetaData);
            }
        }
    }
}

