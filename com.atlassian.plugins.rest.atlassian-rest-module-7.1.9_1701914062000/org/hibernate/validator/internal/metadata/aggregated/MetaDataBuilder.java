/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.ConstraintOrigin;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

public abstract class MetaDataBuilder {
    protected final ConstraintHelper constraintHelper;
    protected final TypeResolutionHelper typeResolutionHelper;
    protected final ValueExtractorManager valueExtractorManager;
    private final Class<?> beanClass;
    private final Set<MetaConstraint<?>> directConstraints = CollectionHelper.newHashSet();
    private final Set<MetaConstraint<?>> containerElementsConstraints = CollectionHelper.newHashSet();
    private boolean isCascading = false;

    protected MetaDataBuilder(Class<?> beanClass, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        this.beanClass = beanClass;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
    }

    public abstract boolean accepts(ConstrainedElement var1);

    public void add(ConstrainedElement constrainedElement) {
        this.directConstraints.addAll(this.adaptConstraints(constrainedElement, constrainedElement.getConstraints()));
        this.containerElementsConstraints.addAll(this.adaptConstraints(constrainedElement, constrainedElement.getTypeArgumentConstraints()));
        this.isCascading = this.isCascading || constrainedElement.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements();
    }

    public abstract ConstraintMetaData build();

    protected Set<MetaConstraint<?>> getDirectConstraints() {
        return this.directConstraints;
    }

    public Set<MetaConstraint<?>> getContainerElementConstraints() {
        return this.containerElementsConstraints;
    }

    protected boolean isCascading() {
        return this.isCascading;
    }

    protected Class<?> getBeanClass() {
        return this.beanClass;
    }

    protected Set<MetaConstraint<?>> adaptOriginsAndImplicitGroups(Set<MetaConstraint<?>> constraints) {
        HashSet<MetaConstraint<?>> adaptedConstraints = CollectionHelper.newHashSet();
        for (MetaConstraint<?> oneConstraint : constraints) {
            adaptedConstraints.add(this.adaptOriginAndImplicitGroup(oneConstraint));
        }
        return adaptedConstraints;
    }

    private <A extends Annotation> MetaConstraint<A> adaptOriginAndImplicitGroup(MetaConstraint<A> constraint) {
        ConstraintOrigin definedIn = this.definedIn(this.beanClass, constraint.getLocation().getDeclaringClass());
        if (definedIn == ConstraintOrigin.DEFINED_LOCALLY) {
            return constraint;
        }
        Class<?> constraintClass = constraint.getLocation().getDeclaringClass();
        ConstraintDescriptorImpl<A> descriptor = new ConstraintDescriptorImpl<A>(this.constraintHelper, constraint.getLocation().getMember(), constraint.getDescriptor().getAnnotationDescriptor(), constraint.getElementType(), constraintClass.isInterface() ? constraintClass : null, definedIn, constraint.getDescriptor().getConstraintType());
        return MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, descriptor, constraint.getLocation());
    }

    protected Set<MetaConstraint<?>> adaptConstraints(ConstrainedElement constrainedElement, Set<MetaConstraint<?>> constraints) {
        return constraints;
    }

    private ConstraintOrigin definedIn(Class<?> rootClass, Class<?> hierarchyClass) {
        if (hierarchyClass.equals(rootClass)) {
            return ConstraintOrigin.DEFINED_LOCALLY;
        }
        return ConstraintOrigin.DEFINED_IN_HIERARCHY;
    }
}

