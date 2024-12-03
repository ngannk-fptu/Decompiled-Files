/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ElementKind
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ElementKind;
import org.hibernate.validator.HibernateValidatorPermission;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.FieldCascadable;
import org.hibernate.validator.internal.metadata.aggregated.GetterCascadable;
import org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.PropertyDescriptorImpl;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.location.GetterConstraintLocation;
import org.hibernate.validator.internal.metadata.location.TypeArgumentConstraintLocation;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.metadata.raw.ConstrainedType;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethod;

public class PropertyMetaData
extends AbstractConstraintMetaData {
    private final Set<Cascadable> cascadables;

    private PropertyMetaData(String propertyName, Type type, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> containerElementsConstraints, Set<Cascadable> cascadables, boolean cascadingProperty) {
        super(propertyName, type, constraints, containerElementsConstraints, !cascadables.isEmpty(), !cascadables.isEmpty() || !constraints.isEmpty() || !containerElementsConstraints.isEmpty());
        this.cascadables = CollectionHelper.toImmutableSet(cascadables);
    }

    @Override
    public PropertyDescriptorImpl asDescriptor(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        CascadingMetaData firstCascadingMetaData = this.cascadables.isEmpty() ? null : this.cascadables.iterator().next().getCascadingMetaData();
        return new PropertyDescriptorImpl(this.getType(), this.getName(), this.asDescriptors(this.getDirectConstraints()), this.asContainerElementTypeDescriptors(this.getContainerElementsConstraints(), firstCascadingMetaData, defaultGroupSequenceRedefined, defaultGroupSequence), firstCascadingMetaData != null ? firstCascadingMetaData.isCascading() : false, defaultGroupSequenceRedefined, defaultGroupSequence, firstCascadingMetaData != null ? firstCascadingMetaData.getGroupConversionDescriptors() : Collections.emptySet());
    }

    public Set<Cascadable> getCascadables() {
        return this.cascadables;
    }

    @Override
    public String toString() {
        return "PropertyMetaData [type=" + this.getType() + ", propertyName=" + this.getName() + "]]";
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.PROPERTY;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return this.getClass() == obj.getClass();
    }

    public static class Builder
    extends MetaDataBuilder {
        private static final EnumSet<ConstrainedElement.ConstrainedElementKind> SUPPORTED_ELEMENT_KINDS = EnumSet.of(ConstrainedElement.ConstrainedElementKind.TYPE, ConstrainedElement.ConstrainedElementKind.FIELD, ConstrainedElement.ConstrainedElementKind.METHOD);
        private final String propertyName;
        private final Map<Member, Cascadable.Builder> cascadableBuilders = new HashMap<Member, Cascadable.Builder>();
        private final Type propertyType;
        private boolean cascadingProperty = false;
        private Method getterAccessibleMethod;

        public Builder(Class<?> beanClass, ConstrainedField constrainedField, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.propertyName = constrainedField.getField().getName();
            this.propertyType = ReflectionHelper.typeOf(constrainedField.getField());
            this.add(constrainedField);
        }

        public Builder(Class<?> beanClass, ConstrainedType constrainedType, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.propertyName = null;
            this.propertyType = null;
            this.add(constrainedType);
        }

        public Builder(Class<?> beanClass, ConstrainedExecutable constrainedMethod, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.propertyName = ReflectionHelper.getPropertyName(constrainedMethod.getExecutable());
            this.propertyType = ReflectionHelper.typeOf(constrainedMethod.getExecutable());
            this.add(constrainedMethod);
        }

        @Override
        public boolean accepts(ConstrainedElement constrainedElement) {
            if (!SUPPORTED_ELEMENT_KINDS.contains((Object)constrainedElement.getKind())) {
                return false;
            }
            if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.METHOD && !((ConstrainedExecutable)constrainedElement).isGetterMethod()) {
                return false;
            }
            return Objects.equals(this.getPropertyName(constrainedElement), this.propertyName);
        }

        @Override
        public final void add(ConstrainedElement constrainedElement) {
            if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.METHOD && constrainedElement.isConstrained()) {
                this.getterAccessibleMethod = this.getAccessible((Method)((ConstrainedExecutable)constrainedElement).getExecutable());
            }
            super.add(constrainedElement);
            boolean bl = this.cascadingProperty = this.cascadingProperty || constrainedElement.getCascadingMetaDataBuilder().isCascading();
            if (constrainedElement.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements() || constrainedElement.getCascadingMetaDataBuilder().hasGroupConversionsOnAnnotatedObjectOrContainerElements()) {
                if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.FIELD) {
                    Field field = ((ConstrainedField)constrainedElement).getField();
                    Cascadable.Builder builder = this.cascadableBuilders.get(field);
                    if (builder == null) {
                        builder = new FieldCascadable.Builder(this.valueExtractorManager, field, constrainedElement.getCascadingMetaDataBuilder());
                        this.cascadableBuilders.put(field, builder);
                    } else {
                        builder.mergeCascadingMetaData(constrainedElement.getCascadingMetaDataBuilder());
                    }
                } else if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.METHOD) {
                    Method method = (Method)((ConstrainedExecutable)constrainedElement).getExecutable();
                    Cascadable.Builder builder = this.cascadableBuilders.get(method);
                    if (builder == null) {
                        builder = new GetterCascadable.Builder(this.valueExtractorManager, this.getterAccessibleMethod, constrainedElement.getCascadingMetaDataBuilder());
                        this.cascadableBuilders.put(method, builder);
                    } else {
                        builder.mergeCascadingMetaData(constrainedElement.getCascadingMetaDataBuilder());
                    }
                }
            }
        }

        @Override
        protected Set<MetaConstraint<?>> adaptConstraints(ConstrainedElement constrainedElement, Set<MetaConstraint<?>> constraints) {
            if (constraints.isEmpty() || constrainedElement.getKind() != ConstrainedElement.ConstrainedElementKind.METHOD) {
                return constraints;
            }
            ConstraintLocation getterConstraintLocation = ConstraintLocation.forGetter(this.getterAccessibleMethod);
            return constraints.stream().map(c -> this.withGetterLocation(getterConstraintLocation, (MetaConstraint<?>)c)).collect(Collectors.toSet());
        }

        private MetaConstraint<?> withGetterLocation(ConstraintLocation getterConstraintLocation, MetaConstraint<?> constraint) {
            ConstraintLocation converted = null;
            if (!(constraint.getLocation() instanceof TypeArgumentConstraintLocation)) {
                converted = constraint.getLocation() instanceof GetterConstraintLocation ? constraint.getLocation() : getterConstraintLocation;
            } else {
                ArrayDeque<ConstraintLocation> locationStack = new ArrayDeque<ConstraintLocation>();
                ConstraintLocation current = constraint.getLocation();
                do {
                    locationStack.addFirst(current);
                } while ((current = current instanceof TypeArgumentConstraintLocation ? ((TypeArgumentConstraintLocation)current).getDelegate() : null) != null);
                for (ConstraintLocation location : locationStack) {
                    if (!(location instanceof TypeArgumentConstraintLocation)) {
                        if (location instanceof GetterConstraintLocation) {
                            converted = location;
                            continue;
                        }
                        converted = getterConstraintLocation;
                        continue;
                    }
                    converted = ConstraintLocation.forTypeArgument(converted, ((TypeArgumentConstraintLocation)location).getTypeParameter(), location.getTypeForValidatorResolution());
                }
            }
            return MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraint.getDescriptor(), converted);
        }

        private String getPropertyName(ConstrainedElement constrainedElement) {
            if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.FIELD) {
                return ReflectionHelper.getPropertyName(((ConstrainedField)constrainedElement).getField());
            }
            if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.METHOD) {
                return ReflectionHelper.getPropertyName(((ConstrainedExecutable)constrainedElement).getExecutable());
            }
            return null;
        }

        private Method getAccessible(Method original) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPermission(HibernateValidatorPermission.ACCESS_PRIVATE_MEMBERS);
            }
            Class<?> clazz = original.getDeclaringClass();
            return this.run(GetDeclaredMethod.andMakeAccessible(clazz, original.getName(), new Class[0]));
        }

        private <T> T run(PrivilegedAction<T> action) {
            return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
        }

        @Override
        public PropertyMetaData build() {
            Set cascadables = this.cascadableBuilders.values().stream().map(b -> b.build()).collect(Collectors.toSet());
            return new PropertyMetaData(this.propertyName, this.propertyType, this.adaptOriginsAndImplicitGroups(this.getDirectConstraints()), this.adaptOriginsAndImplicitGroups(this.getContainerElementConstraints()), cascadables, this.cascadingProperty);
        }
    }
}

