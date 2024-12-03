/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.groups.Default
 *  javax.validation.metadata.ConstraintDescriptor
 *  javax.validation.metadata.ElementDescriptor
 *  javax.validation.metadata.ElementDescriptor$ConstraintFinder
 *  javax.validation.metadata.Scope
 */
package org.hibernate.validator.internal.metadata.descriptor;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.Scope;
import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.engine.groups.ValidationOrder;
import org.hibernate.validator.internal.engine.groups.ValidationOrderGenerator;
import org.hibernate.validator.internal.metadata.core.ConstraintOrigin;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeHelper;

public abstract class ElementDescriptorImpl
implements ElementDescriptor,
Serializable {
    private final Class<?> type;
    private final Set<ConstraintDescriptorImpl<?>> constraintDescriptors;
    private final boolean defaultGroupSequenceRedefined;
    private final List<Class<?>> defaultGroupSequence;

    public ElementDescriptorImpl(Type type, Set<ConstraintDescriptorImpl<?>> constraintDescriptors, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        this.type = (Class)TypeHelper.getErasedType(type);
        this.constraintDescriptors = CollectionHelper.toImmutableSet(constraintDescriptors);
        this.defaultGroupSequenceRedefined = defaultGroupSequenceRedefined;
        this.defaultGroupSequence = CollectionHelper.toImmutableList(defaultGroupSequence);
    }

    public final boolean hasConstraints() {
        return this.constraintDescriptors.size() != 0;
    }

    public final Class<?> getElementClass() {
        return this.type;
    }

    public final Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
        return this.findConstraints().getConstraintDescriptors();
    }

    public final ElementDescriptor.ConstraintFinder findConstraints() {
        return new ConstraintFinderImpl();
    }

    private class ConstraintFinderImpl
    implements ElementDescriptor.ConstraintFinder {
        private List<Class<?>> groups;
        private final EnumSet<ConstraintOrigin> definedInSet;
        private final EnumSet<ElementType> elementTypes = EnumSet.of(ElementType.TYPE, new ElementType[]{ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.TYPE_USE, ElementType.PARAMETER});

        ConstraintFinderImpl() {
            this.definedInSet = EnumSet.allOf(ConstraintOrigin.class);
            this.groups = Collections.emptyList();
        }

        public ElementDescriptor.ConstraintFinder unorderedAndMatchingGroups(Class<?> ... classes) {
            this.groups = CollectionHelper.newArrayList();
            for (Class<?> clazz : classes) {
                if (Default.class.equals(clazz) && ElementDescriptorImpl.this.defaultGroupSequenceRedefined) {
                    this.groups.addAll(ElementDescriptorImpl.this.defaultGroupSequence);
                    continue;
                }
                this.groups.add(clazz);
            }
            return this;
        }

        public ElementDescriptor.ConstraintFinder lookingAt(Scope visibility) {
            if (visibility.equals((Object)Scope.LOCAL_ELEMENT)) {
                this.definedInSet.remove((Object)ConstraintOrigin.DEFINED_IN_HIERARCHY);
            }
            return this;
        }

        public ElementDescriptor.ConstraintFinder declaredOn(ElementType ... elementTypes) {
            this.elementTypes.clear();
            this.elementTypes.addAll(Arrays.asList(elementTypes));
            return this;
        }

        public Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
            HashSet matchingDescriptors = new HashSet();
            this.findMatchingDescriptors(matchingDescriptors);
            return CollectionHelper.toImmutableSet(matchingDescriptors);
        }

        public boolean hasConstraints() {
            return this.getConstraintDescriptors().size() != 0;
        }

        private void addMatchingDescriptorsForGroup(Class<?> group, Set<ConstraintDescriptor<?>> matchingDescriptors) {
            for (ConstraintDescriptorImpl descriptor : ElementDescriptorImpl.this.constraintDescriptors) {
                if (!this.definedInSet.contains((Object)descriptor.getDefinedOn()) || !this.elementTypes.contains((Object)descriptor.getElementType()) || !descriptor.getGroups().contains(group)) continue;
                matchingDescriptors.add(descriptor);
            }
        }

        private void findMatchingDescriptors(Set<ConstraintDescriptor<?>> matchingDescriptors) {
            if (!this.groups.isEmpty()) {
                ValidationOrder validationOrder = new ValidationOrderGenerator().getValidationOrder(this.groups);
                Iterator<Group> groupIterator = validationOrder.getGroupIterator();
                while (groupIterator.hasNext()) {
                    Group g = groupIterator.next();
                    this.addMatchingDescriptorsForGroup(g.getDefiningClass(), matchingDescriptors);
                }
            } else {
                for (ConstraintDescriptorImpl descriptor : ElementDescriptorImpl.this.constraintDescriptors) {
                    if (!this.definedInSet.contains((Object)descriptor.getDefinedOn()) || !this.elementTypes.contains((Object)descriptor.getElementType())) continue;
                    matchingDescriptors.add(descriptor);
                }
            }
        }
    }
}

